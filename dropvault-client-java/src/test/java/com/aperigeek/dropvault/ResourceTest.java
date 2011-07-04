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
package com.aperigeek.dropvault;

import com.aperigeek.dropvault.Resource;
import junit.framework.TestCase;

/**
 *
 * @author Vivien Barousse
 */
public class ResourceTest extends TestCase {
    
    public void testCreatedTrue() {
        Resource resource = new Resource();
        resource.setCreated(true);
        assertEquals(Resource.CREATED, resource.getFlags());
        assertEquals(true, resource.getCreated());
    }
    
    public void testCreatedFalse() {
        Resource resource = new Resource();
        resource.setCreated(false);
        assertEquals(0, resource.getFlags());
        assertEquals(false, resource.getCreated());
    }
    
    public void testDeletedTrue() {
        Resource resource = new Resource();
        resource.setDeleted(true);
        assertEquals(Resource.DELETED, resource.getFlags());
        assertEquals(true, resource.getDeleted());
    }
    
    public void testDeletedFalse() {
        Resource resource = new Resource();
        resource.setDeleted(false);
        assertEquals(0, resource.getFlags());
        assertEquals(false, resource.getDeleted());
    }
    
}
