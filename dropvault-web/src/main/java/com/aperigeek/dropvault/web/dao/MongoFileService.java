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

import com.aperigeek.dropvault.web.beans.Resource;
import com.aperigeek.dropvault.web.service.FileTypeDetectionService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.bson.types.ObjectId;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
public class MongoFileService {
    
    @EJB
    private MongoService mongo;
    
    @EJB
    private FileTypeDetectionService fileTypeDetectionService;
    
    public Resource getRootFolder(String username) {
        DBCollection col = mongo.getDataBase().getCollection("files");
        
        DBObject filter = new BasicDBObject();
        filter.put("user", username);
        filter.put("name", "/");
        
        DBObject root = col.findOne(filter);
        
        Resource res = buildResource(root);
        
        return res;
    }
    
    public List<Resource> getChildren(Resource resource) {
        DBCollection col = mongo.getDataBase().getCollection("files");
        
        DBObject filter = new BasicDBObject();
        filter.put("parent", resource.getId());
        
        List<Resource> children = new ArrayList<Resource>();
        
        DBCursor cursor = col.find(filter);
        while (cursor.hasNext()) {
            children.add(buildResource(cursor.next()));
        }
        
        return children;
    }
    
    // TODO: implement support for ..
    public Resource getChild(Resource resource, String name) {
        if ("".equals(name) || ".".equals(name)) {
            return resource;
        }
        
        DBCollection col = mongo.getDataBase().getCollection("files");
        
        DBObject filter = new BasicDBObject();
        filter.put("name", name);
        filter.put("parent", resource.getId());
        
        DBObject child = col.findOne(filter);
        
        Resource childRes = buildResource(child);
        
        return childRes;
    }
    
    public Resource mkcol(String username, String resource) throws ResourceAlreadyExistsException, ResourceNotFoundException {
        String[] path = resource.split("/");
        Resource parent = getRootFolder(username);
        for (int i = 0; i < path.length - 1; i++) {
            parent = getChild(parent, path[i]);
            if (parent == null) {
                throw new ResourceNotFoundException();
            }
        }
        
        if (getChild(parent, path[path.length - 1]) != null) {
            throw new ResourceAlreadyExistsException();
        }
        
        DBCollection col = mongo.getDataBase().getCollection("files");
        
        DBObject obj = new BasicDBObject();
        obj.put("type", Resource.ResourceType.FOLDER.toString());
        obj.put("user", "viv");
        obj.put("name", path[path.length - 1]);
        obj.put("creationDate", new Date());
        obj.put("modificationDate", new Date());
        obj.put("parent", parent.getId());
        
        col.insert(obj);
        
        return buildResource(obj);
    }
    
    public Resource getResource(String username, String resource) {
        String[] path = resource.split("/");
        Resource parent = getRootFolder(username);
        for (int i = 0; i < path.length; i++) {
            parent = getChild(parent, path[i]);
        }
        return parent;
    }
    
    public void put(String username, String resource, byte[] data, 
            String contentType) {
        String[] path = resource.split("/");
        Resource parent = getRootFolder(username);
        for (int i = 0; i < path.length - 1; i++) {
            parent = getChild(parent, path[i]);
        }
        
        if (contentType == null) {
            contentType = fileTypeDetectionService
                    .detectFileType(path[path.length - 1], data);
        }
        
        DBCollection files = mongo.getDataBase().getCollection("files");
        DBCollection contents = mongo.getDataBase().getCollection("contents");
        
        Resource child = getChild(parent, path[path.length - 1]);
        if (child != null) {
            DBObject filter = new BasicDBObject();
            filter.put("_id", child.getId());
            DBObject update = new BasicDBObject("modificationDate", new Date());
            update.put("contentLength", data.length);
            update.put("contentType", contentType);
            files.update(filter, new BasicDBObject("$set", update));
            
            contents.update(new BasicDBObject("resource", child.getId()), 
                    new BasicDBObject("$set", new BasicDBObject("binary", data)));
        } else {
            DBObject childObj = new BasicDBObject();
            ObjectId objId = new ObjectId();
            childObj.put("_id", objId);
            childObj.put("user", username);
            childObj.put("name", path[path.length - 1]);
            childObj.put("parent", parent.getId());
            childObj.put("type", Resource.ResourceType.FILE.toString());
            childObj.put("creationDate", new Date());
            childObj.put("modificationDate", new Date());
            childObj.put("contentType", contentType);
            childObj.put("contentLength", data.length);
            
            files.insert(childObj);
            
            DBObject content = new BasicDBObject();
            content.put("resource", objId);
            content.put("binary", data);
            
            contents.insert(content);
        }
    }
    
    public byte[] get(Resource resource) {
        DBCollection col = mongo.getDataBase().getCollection("contents");
        
        DBObject filter = new BasicDBObject();
        filter.put("resource", resource.getId());
        
        DBObject result = col.findOne(filter);
        byte[] binary = (byte[]) result.get("binary");
        
        return binary;
    }
    
    public void delete(Resource resource) {
        DBCollection files = mongo.getDataBase().getCollection("files");
        DBCollection contents = mongo.getDataBase().getCollection("contents");
        
        for (Resource child : getChildren(resource)) {
            delete(child);
        }
        
        files.remove(new BasicDBObject("_id", resource.getId()));
        contents.remove(new BasicDBObject("resource", resource.getId()));
    }
    
    protected Resource buildResource(DBObject obj) {
        if (obj == null) {
            return null;
        }
        
        Resource childRes = new Resource(
                (ObjectId) obj.get("_id"),
                (String) obj.get("name"),
                (Date) obj.get("creationDate"),
                (Date) obj.get("modificationDate"));
        
        if ("FILE".equals(obj.get("type"))) {
            childRes.setType(Resource.ResourceType.FILE);
            childRes.setContentLength((Integer) obj.get("contentLength"));
            childRes.setContentType((String) obj.get("contentType"));
        }
        
        return childRes;
    }
    
}
