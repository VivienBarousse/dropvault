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
package com.aperigeek.dropvault.desktop.dao;

import com.aperigeek.dropvault.Resource;
import com.aperigeek.dropvault.Resource.ResourceType;
import com.aperigeek.dropvault.dao.FilesDAO;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class DesktopFilesDAO implements FilesDAO {

    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    
    private Connection connection;
    
    private File derbyDb;

    public DesktopFilesDAO(File derbyDb) {
        this.derbyDb = derbyDb;
        try {
            Class.forName(DRIVER);

            if (derbyDb.exists()) {
                connection = DriverManager.getConnection("jdbc:derby:" + derbyDb.getAbsolutePath());
            } else {
                connection = DriverManager.getConnection("jdbc:derby:" + derbyDb.getAbsolutePath() + ";create=true");
                onCreate(connection);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DesktopFilesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void onCreate(Connection db) throws SQLException {
        db.prepareStatement("CREATE TABLE files ("
                + "name VARCHAR(256),"
                + "parent VARCHAR(1024),"
                + "type VARCHAR(64),"
                + "content_type VARCHAR(64),"
                + "href VARCHAR(1024) PRIMARY KEY,"
                + "lastmodified BIGINT,"
                + "flags INTEGER"
                + ")").execute();
    }

    public Resource getParent(Resource resource) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT p.name, p.href, p.type, p.content_type, p.lastmodified "
                    + "FROM files AS c "
                    + "INNER JOIN files AS p "
                    + "ON c.parent = p.href "
                    + "WHERE c.href=?");
            
            statement.setString(1, resource.getHref());
            ResultSet results = statement.executeQuery();

            if (!results.next()) {
                return null;
            }

            Resource parent = new Resource();
            parent.setName(results.getString(1));
            parent.setHref(results.getString(2));
            parent.setType(ResourceType.valueOf(results.getString(3)));
            parent.setContentType(results.getString(4));
            parent.setLastModificationDate(new Date(results.getLong(5)));

            return parent;
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public Resource getResource(String baseURI) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT name, href, type, content_type, lastmodified FROM files WHERE href=?");
            
            statement.setString(1, baseURI);
            
            ResultSet results = statement.executeQuery();

            if (!results.next()) {
                return null;
            }

            Resource resource = new Resource();
            resource.setName(results.getString(1));
            resource.setHref(results.getString(2));
            resource.setType(ResourceType.valueOf(results.getString(3)));
            resource.setContentType(results.getString(4));
            resource.setLastModificationDate(new Date(results.getLong(5)));

            return resource;
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public List<Resource> getChildren(Resource parent) {
        return getChildren(parent, false);
    }

    public List<Resource> getChildren(Resource parent, boolean includeDeleted) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT name, href, type, content_type, lastmodified "
                    + "FROM files "
                    + "WHERE parent=? "
                    + (!includeDeleted ? "AND flags != " + Resource.DELETED + " " : "")
                    + "ORDER BY type DESC, name ASC");

            statement.setString(1, parent.getHref());
            ResultSet results = statement.executeQuery();

            List<Resource> resources = new ArrayList<Resource>();

            while (results.next()) {
                Resource resource = new Resource();
                resource.setName(results.getString(1));
                resource.setHref(results.getString(2));
                resource.setType(ResourceType.valueOf(results.getString(3)));
                resource.setContentType(results.getString(4));
                resource.setLastModificationDate(new Date(results.getLong(5)));
                resources.add(resource);
            }

            return resources;
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public List<Resource> getAllResources() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT name, href, type, content_type, lastmodified, flags FROM files");

            ResultSet results = statement.executeQuery();
            
            List<Resource> resources = new ArrayList<Resource>();

            while (results.next()) {
                Resource resource = new Resource();
                resource.setName(results.getString(1));
                resource.setHref(results.getString(2));
                resource.setType(ResourceType.valueOf(results.getString(3)));
                resource.setContentType(results.getString(4));
                resource.setLastModificationDate(new Date(results.getLong(5)));
                resource.setFlags(results.getInt(6));
                resources.add(resource);
            }

            return resources;
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public void insert(Resource resource) {
        try {
            remove(resource);
            
            PreparedStatement statement = connection.prepareStatement("INSERT "
                    + "INTO files(name,href,type,content_type,lastmodified, flags)"
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            
            statement.setString(1, resource.getName());
            statement.setString(2, resource.getHref());
            statement.setString(3, resource.getType().toString());
            statement.setString(4, resource.getContentType());
            statement.setLong(5, resource.getLastModificationDate().getTime());
            statement.setInt(6, resource.getFlags());
            
            statement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public void insert(Resource parent, Resource resource) {
        try {
            remove(resource);
            
            PreparedStatement statement = connection.prepareStatement("INSERT "
                + "INTO files(name,href,type,content_type, parent, lastmodified, flags)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            
            statement.setString(1, resource.getName());
            statement.setString(2, resource.getHref());
            statement.setString(3, resource.getType().toString());
            statement.setString(4, resource.getContentType());
            statement.setString(5, parent.getHref());
            statement.setLong(6, resource.getLastModificationDate().getTime());
            statement.setInt(7, resource.getFlags());
            
            statement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public void clear() {
        try {
            connection.createStatement().execute("DELETE FROM files;");
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public void removeAll(List<Resource> remove) {
        for (Resource res : remove) {
            remove(res);
        }
    }

    public void remove(Resource res) {
        try {
            for (Resource child : getChildren(res)) {
                remove(child);
            }
            PreparedStatement statement = connection.prepareStatement("DELETE FROM files WHERE href=?");
            statement.setString(1, res.getHref());
            statement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }

    public void close() {
        try {
            connection.close();
            
            DriverManager.getConnection("jdbc:derby:" + derbyDb.getAbsolutePath() + ";shutdown=true");
        } catch (SQLException ex) {
            throw new RuntimeException("SQL exception", ex);
        }
    }
}
