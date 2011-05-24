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
package com.aperigeek.dropvault.web.rest.webdav;

import java.net.URI;
import java.util.Date;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;
import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.HRef;
import net.java.dev.webdav.jaxrs.xml.elements.MultiStatus;
import net.java.dev.webdav.jaxrs.xml.elements.Prop;
import net.java.dev.webdav.jaxrs.xml.elements.PropStat;
import net.java.dev.webdav.jaxrs.xml.elements.Response;
import net.java.dev.webdav.jaxrs.xml.elements.Status;
import net.java.dev.webdav.jaxrs.xml.properties.CreationDate;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;
import net.java.dev.webdav.jaxrs.xml.properties.ResourceType;

/**
 *
 * @author Vivien Barousse
 */
@Path("/dav/{user}/")
public class RootFolderRestService {
    
    @Produces("application/xml")
    @PROPFIND
    public javax.ws.rs.core.Response propfind(@Context UriInfo uriInfo) {
        URI uri = uriInfo.getRequestUri();
        
        Response folder = new Response(new HRef(uri), 
                null, 
                null, 
                null, 
                new PropStat(
                        new Prop(new CreationDate(new Date()), 
                                new GetLastModified(new Date()), 
                                ResourceType.COLLECTION), 
                        new Status((StatusType) javax.ws.rs.core.Response.Status.OK)));
        
        // TODO: add support for files in the root folder
        
        return javax.ws.rs.core.Response.status(207)
                .entity(new MultiStatus(folder))
                .type("application/xml;charset=UTF-8")
                .build();
    }
    
    @OPTIONS
    public javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.ok()
                .header("DAV", 1)
                .build();
    }
    
}
