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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
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
import net.java.dev.webdav.jaxrs.methods.MOVE;
import net.java.dev.webdav.jaxrs.methods.PROPFIND;
import net.java.dev.webdav.jaxrs.xml.elements.Rfc1123DateFormat;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
@Path("/dav/{user}/{resource:(.*)}")
public class ResourceRestService extends AbstractResourceRestService {
    
    /*
     * Maximal content length allowed by this endpoint.
     * 
     * Fixed to 16 MiB minus 10 KiB due to a limitation in MongoDB. 
     * But hey, that's more than 11 floppy disks!
     * 
     * The 16 MiB - 10 KiB value comes for a limitation in the BSON model
     * used by MongoDB for its ondisk storage. BSON entities can't be
     * bigger than 16 MiB, including fields other than the actual file content.
     * 10 KiB are reserved for those other fields.
     */
    public static final long MAX_CONTENT_LENGTH = 16 * 1024 * 1024 - 10 * 1024;
    
    @EJB
    private MongoFileService fileService;

    @Override
    @Produces("application/xml")
    @PROPFIND
    public javax.ws.rs.core.Response propfind(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @PathParam("resource") String resource,
            @HeaderParam("Depth") String depthStr) {

        return super.propfind(uriInfo, user, resource, depthStr);
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
        
        if (contentLength > MAX_CONTENT_LENGTH) {
            return javax.ws.rs.core.Response.status(507).build();
        }
        
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream((int) contentLength);
            byte[] buffer = new byte[4096];
            int readed;
            while ((readed = in.read(buffer)) != -1) {
                out.write(buffer, 0, readed);
            }
            
            fileService.put(user, resource, out.toByteArray());
            
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
        
        String uri = URLDecoder.decode(uriInfo.getRequestUri().toString());
        String dest = URLDecoder.decode(destination).substring(uri.length() - resource.length());
        
        Resource res = fileService.getResource(user, resource);
        byte[] data = fileService.get(res);
        fileService.put(user, dest, data);
        
        return javax.ws.rs.core.Response.created(URI.create(destination)).build();
        
    }
    
    @MOVE
    public javax.ws.rs.core.Response move(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @PathParam("resource") String resource,
            @HeaderParam("Destination") String destination) {
        
        String uri = URLDecoder.decode(uriInfo.getRequestUri().toString());
        String dest = URLDecoder.decode(destination).substring(uri.length() - resource.length());
        
        // Quick and dirty implementation. It works, but the document changes
        // id (it is deleted and then recreated)
        // Changing the parent reference and the name would be more time
        // and memory efficient
        
        // TODO: Change this!
        Resource res = fileService.getResource(user, resource);
        byte[] data = fileService.get(res);
        fileService.put(user, dest, data);
        
        fileService.delete(res);
        // End of Quick and dirty
        
        return javax.ws.rs.core.Response.created(URI.create(destination)).build();
        
    }
    
    @Override
    protected MongoFileService getFileService() {
        return fileService;
    }
}
