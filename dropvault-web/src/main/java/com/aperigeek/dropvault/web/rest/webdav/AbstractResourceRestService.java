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
import com.aperigeek.dropvault.web.dao.user.InvalidPasswordException;
import com.aperigeek.dropvault.web.dao.user.UsersDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import net.java.dev.webdav.jaxrs.xml.elements.HRef;
import net.java.dev.webdav.jaxrs.xml.elements.MultiStatus;
import net.java.dev.webdav.jaxrs.xml.elements.Prop;
import net.java.dev.webdav.jaxrs.xml.elements.PropStat;
import net.java.dev.webdav.jaxrs.xml.elements.Response;
import net.java.dev.webdav.jaxrs.xml.elements.Status;
import net.java.dev.webdav.jaxrs.xml.properties.CreationDate;
import net.java.dev.webdav.jaxrs.xml.properties.DisplayName;
import net.java.dev.webdav.jaxrs.xml.properties.GetContentLength;
import net.java.dev.webdav.jaxrs.xml.properties.GetContentType;
import net.java.dev.webdav.jaxrs.xml.properties.GetLastModified;
import net.java.dev.webdav.jaxrs.xml.properties.ResourceType;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Vivien Barousse
 */
public abstract class AbstractResourceRestService {
    
    protected javax.ws.rs.core.Response propfind(UriInfo uriInfo,
            String user,
            String resource,
            String depthStr) {

        int depth = (depthStr == null || "Infinity".equals(depthStr)) ?
                -1 : Integer.parseInt(depthStr);
        
        Resource current = getFileService().getResource(user, resource);
        
        if (current == null) {
            return javax.ws.rs.core.Response.status(404).build();
        }

        List<Response> responses = new ArrayList<Response>();
        
        addWithChildren(uriInfo.getRequestUriBuilder(), current, responses, depth);

        return javax.ws.rs.core.Response.status(207).entity(new MultiStatus(responses.toArray(new Response[responses.size()]))).build();
    }
    
    private void addWithChildren(UriBuilder uri, Resource parent, List<Response> responses, int level) {
        responses.add(new Response(new HRef(uri.build()),
                null, null, null, fileStat(parent)));
        
        if (level == 0) {
            return;
        }
        
        if (parent.isDirectory()) {
            for (Resource child : getFileService().getChildren(parent)) {
                addWithChildren(uri.clone().path(child.getName()), child, responses, level - 1);
            }
        }
    }
    
    @OPTIONS
    public javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.ok()
                .header("DAV", 1)
                .header("Allow", "OPTIONS,MKCOL,GET,DELETE,MOVE,PROPFIND,COPY,HEAD,PUT")
                .build();
    }

    protected PropStat fileStat(Resource res) {
        List<Object> props = new ArrayList<Object>();

        props.add(new DisplayName(res.getName()));
        props.add(new CreationDate(res.getCreationDate()));
        props.add(new GetLastModified(res.getModificationDate()));

        if (res.isDirectory()) {
            props.add(ResourceType.COLLECTION);
        } else {
            props.add(new GetContentType(res.getContentType()));
            props.add(new GetContentLength(res.getContentLength()));
        }

        Prop prop = new Prop(props.toArray());
        PropStat stat = new PropStat(prop, new Status((StatusType) javax.ws.rs.core.Response.Status.OK));

        return stat;
    }
    
    protected abstract MongoFileService getFileService();
    
    protected abstract UsersDAO getUsersDAO();
    
    protected void checkAuthentication(String username, String header) 
            throws InvalidPasswordException, NotAuthorizedException, ProtocolException {
        
        if (header == null) {
            throw new InvalidPasswordException();
        }
        
        Pattern headerPattern = Pattern.compile("Basic (.+)");
        Matcher headerMatcher = headerPattern.matcher(header);
        
        if (!headerMatcher.matches()) {
            throw new ProtocolException("Invalid Authorization header");
        }
        
        String b64 = headerMatcher.group(1);
        String headerContent = new String(Base64.decodeBase64(b64));

        Pattern passwordPattern = Pattern.compile("(.+):([^:]+)");
        Matcher passwordMatcher = passwordPattern.matcher(headerContent);

        if (!passwordMatcher.matches()) {
            throw new ProtocolException("Invalid authentication header");
        }

        String user = passwordMatcher.group(1);
        String password = passwordMatcher.group(2);
        
        getUsersDAO().login(user, password);
        
        if (!user.equals(username)) {
            throw new NotAuthorizedException();
        }
    }
    
}
