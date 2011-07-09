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

import com.aperigeek.dropvault.web.service.IndexException;
import com.aperigeek.dropvault.web.service.IndexService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Vivien Barousse
 */
@Path("query/{user}/{query}")
@Stateless
public class SearchService {
    
    @EJB
    private IndexService indexService;
    
    @GET
    public String query(@PathParam("user") String user,
            @QueryParam("password") String password,
            @PathParam("query") String query) throws IndexException {
        return indexService.search(user, password, query).toString();
    }
    
}
