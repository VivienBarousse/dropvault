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
package com.aperigeek.dropvault.web.dao.user;

import com.aperigeek.dropvault.web.beans.User;
import com.aperigeek.dropvault.web.dao.MongoService;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
public class UsersDAO {
    
    @EJB
    private MongoService mongo;
    
    public User login(String username, String passHash) throws InvalidPasswordException {
        DBCollection users = mongo.getDataBase().getCollection("users");
        
        DBObject filter = new BasicDBObjectBuilder()
                .add("name", username)
                .add("password", passHash)
                .get();
        
        DBObject result = users.findOne(filter);
        
        if (result == null) {
            throw new InvalidPasswordException();
        }
        
        return new User(username);
    }
    
}
