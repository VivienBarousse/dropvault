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
package com.aperigeek.dropvault.service;

import com.aperigeek.dropvault.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author Vivien Barousse
 */
public interface FilesService {
    
    public Resource getRoot();
    
    public Resource getParent(Resource res);
    
    public List<Resource> getChildren(Resource parent);
    
    public void importFile(InputStream in, String mimeType, String name) throws IOException;
    
    public void close();
    
    public void sync() throws SyncException;
    
    public File getFile(Resource res);
    
    public void delete(Resource resource);

    public String getPassword();

    public void setPassword(String password);

    public String getUsername();

    public void setUsername(String username);
    
}
