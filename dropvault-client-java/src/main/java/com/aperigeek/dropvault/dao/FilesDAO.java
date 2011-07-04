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
package com.aperigeek.dropvault.dao;

import com.aperigeek.dropvault.Resource;
import java.util.List;

/**
 *
 * @author Vivien Barousse
 */
public interface FilesDAO {
    
    public Resource getParent(Resource resource);
    
    public Resource getResource(String baseURI);
    
    public List<Resource> getChildren(Resource parent);
    
    public List<Resource> getChildren(Resource parent, boolean includeDeleted);
    
    public List<Resource> getAllResources();
    
    public void insert(Resource resource);
    
    public void insert(Resource parent, Resource resource);
    
    public void clear();

    public void removeAll(List<Resource> remove);

    public void remove(Resource res);
    
    public void close();
    
}
