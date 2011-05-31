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
import com.aperigeek.dropvault.android.Resource;
import com.aperigeek.dropvault.android.dao.FilesDAO;
import com.aperigeek.dropvault.android.dav.DAVClient;
import com.aperigeek.dropvault.android.dav.DAVException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 *
 * @author Vivien Barousse
 */
public class FilesService {
    
    private String baseURI;
    
    private Context context;
    
    private FilesDAO dao;
    
    private DAVClient client = new DAVClient();

    public FilesService(String baseURI, Context context) {
        this.baseURI = baseURI;
        this.context = context;
        dao = new FilesDAO(context);
    }
    
    public Resource getRoot() {
        return dao.getResource(baseURI);
    }
    
    public Resource getParent(Resource res) {
        return dao.getParent(res);
    }
    
    public List<Resource> getChildren(Resource parent) {
        return dao.getChildren(parent);
    }
    
    public void sync() throws SyncException {
        try {
            dao.clear();
            
            insert(null, client.getResource(baseURI));
        } catch (DAVException ex) {
            throw new SyncException(ex);
        } catch (IOException ex) {
            throw new SyncException(ex);
        }
    }
    
    private void insert(Resource parent, Resource current) 
            throws DAVException, IOException {
        if (parent != null) {
            dao.insert(parent, current);
        } else {
            dao.insert(current);
        }
        
        if (current.getType() == Resource.ResourceType.FILE) {
            dump(current);
        }
        
        for (Resource child : client.getResources(current, 1)) {
            insert(current, child);
        }
    }
    
    private void dump(Resource res) throws DAVException, IOException {
        File folder = context.getExternalFilesDir(null);
        folder = new File(folder, "DropVault");
        
        String path = res.getHref().substring(baseURI.length());
        path = URLDecoder.decode(path);
        
        File file = new File(folder, path);
        file.getParentFile().mkdirs();
        
        FileOutputStream out = new FileOutputStream(file);
        InputStream in = client.get(res);
        byte[] buffer = new byte[4096];
        int readed;
        while ((readed = in.read(buffer)) != -1) {
            out.write(buffer, 0, readed);
        }
        out.close();
        in.close();
    }
}
