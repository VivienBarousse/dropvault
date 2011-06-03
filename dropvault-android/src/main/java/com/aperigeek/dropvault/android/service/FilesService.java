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
import com.aperigeek.dropvault.android.Resource;
import com.aperigeek.dropvault.android.dao.FilesDAO;
import com.aperigeek.dropvault.android.dav.DAVException;
import com.aperigeek.dropvault.android.dav.DropDAVClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.rmi.Remote;
import java.util.List;
import java.util.logging.Logger;

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
            
            sync(client, null, client.getRootResource());
        } catch (DAVException ex) {
            throw new SyncException(ex);
        } catch (IOException ex) {
            throw new SyncException(ex);
        }
    }
    
    public File getFile(Resource res) {
        File folder = context.getExternalFilesDir(null);
        folder = new File(folder, "DropVault");
        
        String path = res.getHref().substring(getBaseURI().length());
        path = URLDecoder.decode(path);
        
        File file = new File(folder, path);
        return file;
    }
    
    private void sync(DropDAVClient client, Resource parent, Resource current) 
            throws DAVException, IOException {
        Resource local = dao.getResource(current.getHref());
        
        if (local == null || current.getLastModificationDate().after(local.getLastModificationDate())) {
            Logger.getAnonymousLogger().info(current.getHref() + " remotely modified, pulling");
            pull(client, parent, current);
        } else if (current.getLastModificationDate().before(local.getLastModificationDate())) {
            Logger.getAnonymousLogger().info(current.getHref() + " locally modified, pushing");
            push(client, parent, current);
        } else {
            Logger.getAnonymousLogger().info(current.getHref() + " not modified, syncing children");
            for (Resource child : client.getResources(current)) {
                sync(client, current, child);
            }
        }
    }
    
    private void pull(DropDAVClient client, Resource parent, Resource current) throws IOException, DAVException {
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
        }
        dao.insert(parent, current);
        
        List<Resource> local = dao.getChildren(current);
        List<Resource> remote = client.getResources(current, 1);
        local.removeAll(remote);
        
        dao.removeAll(local);
        
        for (Resource child : remote) {
            sync(client, current, child);
        }
    }
    
    // TODO: push isn't finished yet
    // The application doesn't allow file editing yet so this should be 
    // ok for now
    private void push(DropDAVClient client, Resource parent, Resource current) throws DAVException, IOException {
        if (current.getType() == Resource.ResourceType.FILE) {
//            FileInputStream in = new FileInputStream(getFile(current));
//            client.put(current, in);
        }
        // Set update date
        
        List<Resource> remote = client.getResources(current, 1);
        List<Resource> local = dao.getChildren(current);
        remote.removeAll(local);
        
//        client.deleteAll(remote);
        
        for (Resource child : local) {
            sync(client, current, child);
        }
    }
    
    /*
    private void syncFile(DropDAVClient client, Resource remote, Resource local) throws DAVException, IOException {
        File file = getFile(remote);
        file.getParentFile().mkdirs();

        FileOutputStream out = new FileOutputStream(file);
        InputStream in = client.get(remote);
        byte[] buffer = new byte[4096];
        int readed;
        while ((readed = in.read(buffer)) != -1) {
            out.write(buffer, 0, readed);
        }
        out.close();
        in.close();
    }
     */
    
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
