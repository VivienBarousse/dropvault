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
package com.aperigeek.dropvault.web.rest;

import com.aperigeek.dropvault.web.rest.webdav.ResourceRestService;
import com.aperigeek.dropvault.web.rest.webdav.RootFolderRestService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.xml.bind.JAXBException;
import net.java.dev.webdav.jaxrs.xml.WebDavContextResolver;

/**
 *
 * @author Vivien Barousse
 */
@ApplicationPath("/rs")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(
                RootFolderRestService.class,
                ResourceRestService.class));
    }
    
    @Override
    public Set<Object> getSingletons() {
        try {
            return new HashSet<Object>(Arrays.asList(new WebDavContextResolver()));
        } catch (JAXBException ex) {
            Logger.getLogger(RestApplication.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
