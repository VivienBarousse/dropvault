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

import com.aperigeek.dropvault.web.conf.ConfigService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import net.java.dev.webdav.jaxrs.methods.MKCOL;
import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.HRef;
import net.java.dev.webdav.jaxrs.xml.elements.MultiStatus;
import net.java.dev.webdav.jaxrs.xml.elements.Prop;
import net.java.dev.webdav.jaxrs.xml.elements.PropStat;
import net.java.dev.webdav.jaxrs.xml.elements.Response;
import net.java.dev.webdav.jaxrs.xml.elements.Rfc1123DateFormat;
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
@Path("/dav/{user}/{resource}")
public class ResourceRestService {

    @EJB
    private ConfigService config;

    @Produces("application/xml")
    @PROPFIND
    public javax.ws.rs.core.Response propfind(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @PathParam("resource") String resource) {

        File current = config.getStorageFolder(user, resource);

        List<Response> responses = new ArrayList<Response>();

        responses.add(new Response(new HRef(uriInfo.getRequestUri()),
                null, null, null, fileStat(current)));

        if (current.isDirectory()) {
            for (File child : current.listFiles()) {
                responses.add(new Response(new HRef(uriInfo.getRequestUriBuilder().path(child.getName()).build()),
                        null, null, null, fileStat(child)));
            }
        }

        return javax.ws.rs.core.Response.status(207).entity(new MultiStatus(responses.toArray(new Response[responses.size()]))).build();
    }

    @Produces("application/octet-stream")
    @GET
    public javax.ws.rs.core.Response get(@PathParam("user") String user,
            @PathParam("resource") String resource) {

        File file = config.getStorageFolder(user, resource);

        try {
            FileInputStream in = new FileInputStream(file);
            
            return javax.ws.rs.core.Response.ok()
                    .header("Last-Modified", new Rfc1123DateFormat().format(new Date(file.lastModified())))
                    .header("Content-Length", file.length())
                    .entity(in)
                    .build();
            
        } catch (IOException ex) {
            return javax.ws.rs.core.Response.serverError().build();
        }
    }
    
    @Consumes("*/*")
    @PUT
    public javax.ws.rs.core.Response put(@PathParam("user") String user,
            @PathParam("resource") String resource,
            @HeaderParam("Content-Length") long contentLength,
            InputStream data) {
        
        File file = config.getStorageFolder(user, resource);
        
        try {
            FileOutputStream out = new FileOutputStream(file);
            
            byte[] buffer = new byte[1024];
            int readed;
            
            while ((readed = data.read(buffer)) != -1) {
                out.write(buffer, 0, readed);
            }
            
            out.close();
            
            return javax.ws.rs.core.Response.ok().build();
            
        } catch (IOException ex) {
            return javax.ws.rs.core.Response.serverError().build();
        }
        
    }
    
    @MKCOL
    public javax.ws.rs.core.Response mkcol(@PathParam("user") String user,
            @PathParam("resource") String resource) {
        
        File file = config.getStorageFolder(user, resource);
        file.mkdirs();
        
        return javax.ws.rs.core.Response.status(201).build();
        
    }

    @OPTIONS
    public javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.ok().header("DAV", 1).build();
    }

    protected PropStat fileStat(File file) {
        List<Object> props = new ArrayList<Object>();

        props.add(new CreationDate(new Date(file.lastModified())));
        props.add(new GetLastModified(new Date(file.lastModified())));

        if (file.isDirectory()) {
            props.add(ResourceType.COLLECTION);
        } else {
            props.add(new GetContentType("application/octet-stream"));
            props.add(new GetContentLength(file.length()));
        }

        Prop prop = new Prop(props.toArray());
        PropStat stat = new PropStat(prop, new Status(javax.ws.rs.core.Response.Status.OK));

        return stat;
    }
}
