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

import com.aperigeek.dropvault.web.dao.MongoFileService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import net.java.dev.webdav.jaxrs.methods.PROPFIND;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
@Path("/dav/{user}/")
public class RootFolderRestService extends AbstractResourceRestService {
    
    @EJB
    private MongoFileService fileService;
    
    @Produces("application/xml")
    @PROPFIND
    public javax.ws.rs.core.Response propfind(@Context UriInfo uriInfo,
            @PathParam("user") String user,
            @HeaderParam("Depth") String depthStr) {

        return super.propfind(uriInfo, user, ".", depthStr);
        
    }
    
    @Override
    protected MongoFileService getFileService() {
        return fileService;
    }
    
}
