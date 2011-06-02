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

import com.aperigeek.dropvault.web.beans.User;
import com.aperigeek.dropvault.web.dao.user.InvalidPasswordException;
import com.aperigeek.dropvault.web.dao.user.UsersDAO;
import java.net.URI;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
@Path("login")
public class LoginRestService {
    
    @EJB
    private UsersDAO users;
    
    @GET
    public Response login(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @Context UriInfo uriInfo) {
        
        try {
            User user = users.login(username, password);
            
            URI davUri = uriInfo.getBaseUriBuilder()
                    .path("dav")
                    .path(user.getUsername())
                    .build();
            
            return Response.ok(davUri.toString()).build();
        } catch (InvalidPasswordException ex) {
            return Response.status(401).entity("invalid_password").build();
        }
        
    }
    
}
