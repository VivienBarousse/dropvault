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
import com.aperigeek.dropvault.web.dao.ResourceAlreadyExistsException;
import com.aperigeek.dropvault.web.dao.ResourceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import net.java.dev.webdav.jaxrs.methods.COPY;
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
@Path("/dav/{user}/{resource:(.*)}")
public class ResourceRestService {
    
    @EJB
    private MongoFileService fileService;

    @Produces("application/xml")
    @PROPFIND
    public javax.ws.rs.core.Response propfind(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @PathParam("resource") String resource) {

        Resource current = fileService.getResource(user, resource);
        
        if (current == null) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        List<Response> responses = new ArrayList<Response>();
        
        responses.add(new Response(new HRef(uriInfo.getRequestUri()),
                null, null, null, fileStat(current)));

        if (current.isDirectory()) {
            for (Resource child : fileService.getChildren(current)) {
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
        
        Resource res = fileService.getResource(user, resource);
        
        if (res == null) {
            return javax.ws.rs.core.Response.status(404).build();
        }
        
        byte[] data = fileService.get(res);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        
        return javax.ws.rs.core.Response.ok()
                .header("Content-Type", res.getContentType())
                .header("Content-Length", res.getContentLength())
                .header("Last-Modified", new Rfc1123DateFormat().format(res.getModificationDate()))
                .entity(in)
                .build();
        
    }
    
    @Consumes("*/*")
    @PUT
    public javax.ws.rs.core.Response put(@PathParam("user") String user,
            @PathParam("resource") String resource,
            @HeaderParam("Content-Length") long contentLength,
            InputStream in) {
        
        if (contentLength > Integer.MAX_VALUE) {
            return javax.ws.rs.core.Response.status(507).build();
        }
        
        try {
            byte[] data = new byte[(int) contentLength];
            in.read(data, 0, (int) contentLength);
            
            fileService.put(user, resource, data);
            
            return javax.ws.rs.core.Response.ok().build();
        } catch (IOException ex) {
            return javax.ws.rs.core.Response.serverError().build();
        }
        
    }
    
    @MKCOL
    public javax.ws.rs.core.Response mkcol(@PathParam("user") String user,
            @PathParam("resource") String resource) {
        
        try {
            fileService.mkcol(user, resource);
        } catch (ResourceAlreadyExistsException ex) {
            return javax.ws.rs.core.Response.status(405).build();
        } catch (ResourceNotFoundException ex) {
            return javax.ws.rs.core.Response.status(409).build();
        }
        
        return javax.ws.rs.core.Response.status(201).build();
        
    }
    
    @DELETE
    public javax.ws.rs.core.Response delete(@PathParam("user") String user,
            @PathParam("resource") String resource) {
        
        Resource res = fileService.getResource(user, resource);
        fileService.delete(res);
        
        return javax.ws.rs.core.Response.ok().build();
        
    }
    
    @COPY
    public javax.ws.rs.core.Response copy(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @PathParam("resource") String resource,
            @HeaderParam("Destination") String destination) {
        
        URI uri = uriInfo.getRequestUri();
        String uriStr = uri.toString();
        String baseUriStr = uriStr.substring(0, uriStr.length() - resource.length());
        URI baseUri = URI.create(baseUriStr);
        
        String dest = baseUri.relativize(URI.create(destination)).toString();
        
        Resource res = fileService.getResource(user, resource);
        byte[] data = fileService.get(res);
        fileService.put(user, dest, data);
        
        return javax.ws.rs.core.Response.ok().build();
        
    }

    @OPTIONS
    public javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.ok().header("DAV", 1).build();
    }

    protected PropStat fileStat(Resource res) {
        List<Object> props = new ArrayList<Object>();

        props.add(new CreationDate(res.getCreationDate()));
        props.add(new GetLastModified(res.getModificationDate()));

        if (res.isDirectory()) {
            props.add(ResourceType.COLLECTION);
        } else {
            props.add(new GetContentType(res.getContentType()));
            props.add(new GetContentLength(res.getContentLength()));
        }

        Prop prop = new Prop(props.toArray());
        PropStat stat = new PropStat(prop, new Status(javax.ws.rs.core.Response.Status.OK));

        return stat;
    }
}
