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

import com.aperigeek.dropvault.web.beans.Resource;
import com.aperigeek.dropvault.web.dao.MongoFileService;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import net.java.dev.webdav.jaxrs.xml.properties.GetContentLength;
import net.java.dev.webdav.jaxrs.xml.properties.GetContentType;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;
import net.java.dev.webdav.jaxrs.xml.properties.ResourceType;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
@Path("/dav/{user}/")
public class RootFolderRestService {
    
    @EJB
    private MongoFileService fileService;
    
    @Produces("application/xml")
    @PROPFIND
    public javax.ws.rs.core.Response propfind(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @HeaderParam("Depth") String depthStr) {

        int depth = (depthStr == null || "Infinity".equals(depthStr)) ?
                -1 : Integer.parseInt(depthStr);
        
        URI uri = uriInfo.getRequestUri();
        
        Resource userHome = fileService.getRootFolder(user);
        
        Response folder = new Response(new HRef(uri), 
                null, 
                null, 
                null, 
                new PropStat(
                        new Prop(new CreationDate(userHome.getCreationDate()), 
                                new GetLastModified(userHome.getModificationDate()), 
                                ResourceType.COLLECTION), 
                        new Status((StatusType) javax.ws.rs.core.Response.Status.OK)));
        
        List<Response> files = new ArrayList<Response>();
        
        if (depth != 0) {
            for (Resource file : fileService.getChildren(userHome)) {
                List<Object> props = new ArrayList<Object>();

                props.add(new CreationDate(new Date()));
                props.add(new GetLastModified(new Date()));

                if (file.isDirectory()) {
                    props.add(ResourceType.COLLECTION);
                } else {
                    props.add(new GetContentType("application/octet-stream"));
                    props.add(new GetContentLength(file.getContentLength()));
                }

                Prop prop = new Prop(props.toArray());

                Response fileRep = new Response(new HRef(uriInfo.getRequestUriBuilder().path(file.getName()).build()),
                        null,
                        null,
                        null,
                        new PropStat(prop, new Status(javax.ws.rs.core.Response.Status.OK)));
                files.add(fileRep);
            }
        }
        
        files.add(folder);
        
        return javax.ws.rs.core.Response.status(207)
                .entity(new MultiStatus(files.toArray(new Response[files.size()])))
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
