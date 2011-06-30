/*  
 * This file is part of dropvault.
 *
 * dropvault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dropvault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dropvault.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aperigeek.dropvault.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import com.aperigeek.dropvault.android.Resource;
import com.aperigeek.dropvault.android.dao.FilesDAO;
import com.aperigeek.dropvault.android.dav.DAVException;
import com.aperigeek.dropvault.android.dav.DropDAVClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vivien Barousse
 */
public class FilesService {
    
    private String username;
    
    private String password;
    
    private Context context;
    
    private FilesDAO dao;
    
    public FilesService(Context context) {
        this.context = context;
        dao = new FilesDAO(context);
    }
    
    public FilesService(String username, String password, Context context) {
        this(context);
        this.username = username;
        this.password = password;
    }
    
    public Resource getRoot() {
        String baseUri = getBaseURI();
        
        if (baseUri == null) {
            return null;
        }
        
        return dao.getResource(baseUri);
    }
    
    public Resource getParent(Resource res) {
        if (res == null) {
            return null;
        }
        return dao.getParent(res);
    }
    
    public List<Resource> getChildren(Resource parent) {
        return dao.getChildren(parent);
    }
    
    public void close() {
        dao.close();
    }
    
    public void sync() throws SyncException {
        try {
            DropDAVClient client = new DropDAVClient(username, password);
            
            setBaseURI(client.getBaseURI());
            
            push(client, client.getRootResource());
            pull(client, null, client.getRootResource());
        } catch (DAVException ex) {
            throw new SyncException(ex);
        } catch (IOException ex) {
            throw new SyncException(ex);
        }
    }
    
    public File getFile(Resource res) {
        File folder = Environment.getExternalStorageDirectory();
        folder = new File(folder, "DropVault");
        if (!folder.exists()) {
            folder.mkdir();
        }
        
        String path = res.getHref().substring(getBaseURI().length());
        path = URLDecoder.decode(path);
        
        File file = new File(folder, path);
        return file;
    }
    
    private void push(DropDAVClient client, Resource dbFolder) throws IOException, DAVException {
        File fsFolder = getFile(dbFolder);
        System.out.println(fsFolder);
        
        List<Resource> dbChildren = dao.getChildren(dbFolder);
        List<File> created = new ArrayList<File>(
                Arrays.asList(fsFolder.listFiles()));
        
        for (Resource dbChild : dbChildren) {
            File fsChild = getFile(dbChild);
            if (!fsChild.exists()) {
                client.delete(dbChild.getHref());
                dao.remove(dbChild);
                dbChildren.remove(dbChild);
            } else if (fsChild.isFile() &&
                    fsChild.lastModified() != dbChild.getLastModificationDate().getTime()) {
                client.put(dbChild.getHref(), fsChild);
            }
            created.remove(fsChild);
        }
        
        for (File createdFile : created) {
            String href = dbFolder.getHref() + "/" + createdFile.getName();
            if (createdFile.isDirectory()) {
                client.mkcol(href);
                Resource createdRes = client.getResource(href);
                dao.insert(createdRes);
                dbChildren.add(createdRes);
            } else {
                client.put(href, createdFile);
            }
        }
        
        for (Resource dbChild : dbChildren) {
            if (dbChild.getType() == Resource.ResourceType.FOLDER) {
                push(client, dbChild);
            }
        }
        
    }
    
    private void pull(DropDAVClient client, Resource parent, Resource current) throws IOException, DAVException {
        Resource local = dao.getResource(current.getHref());
        List<Resource> remote = client.getResources(current, 1);
        
        if (local == null || !current.getLastModificationDate().equals(local.getLastModificationDate())) {
            if (current.getType() == Resource.ResourceType.FILE) {
                dao.insert(parent, current);

                File file = getFile(current);
                file.getParentFile().mkdirs();

                FileOutputStream out = new FileOutputStream(file);
                InputStream in = client.get(current);
                byte[] buffer = new byte[4096];
                int readed;
                while ((readed = in.read(buffer)) != -1) {
                    out.write(buffer, 0, readed);
                }
                out.close();
                in.close();
                file.setLastModified(current.getLastModificationDate().getTime());
            }
            dao.remove(current);
            dao.insert(parent, current);

            List<Resource> locals = dao.getChildren(current);
            locals.removeAll(remote);

            dao.removeAll(locals);
            for (Resource remove : locals) {
                deleteR(getFile(remove));
            }
        }
        
        for (Resource child : remote) {
            pull(client, current, child);
        }
    }
    
    protected void deleteR(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteR(f);
            }
        }
        file.delete();
    }
    
    protected String getBaseURI() {
        SharedPreferences prefs = context.getSharedPreferences("URI_PREFS", 0);
        return prefs.getString("baseURI", null);
    }
    
    protected void setBaseURI(String baseUri) {
        SharedPreferences prefs = context.getSharedPreferences("URI_PREFS", 0);
        prefs.edit()
                .putString("baseURI", baseUri)
                .commit();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
