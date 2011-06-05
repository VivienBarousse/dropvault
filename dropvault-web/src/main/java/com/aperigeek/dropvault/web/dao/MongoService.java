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
package com.aperigeek.dropvault.web.dao;

import com.mongodb.DB;
import com.mongodb.Mongo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Vivien Barousse
 */
@Singleton
@Startup
public class MongoService {
    
    private static final Logger log = Logger.getLogger(MongoService.class.getName());
    
    // TODO: Export in config file
    private static final String db_host = "127.0.0.1";
    
    // TODO: Export in config file
    private static final int db_port = 27017;
    
    // TODO: Export in config file
    private static final String db_name = "dropvault";
    
    private Mongo mongo;
    
    @PostConstruct
    protected void init() {
        try {
            mongo = new Mongo(db_host, db_port);
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    public DB getDataBase() {
        return mongo.getDB(db_name);
    }
    
    @PreDestroy
    protected void close() {
        mongo.close();
    }
    
}
