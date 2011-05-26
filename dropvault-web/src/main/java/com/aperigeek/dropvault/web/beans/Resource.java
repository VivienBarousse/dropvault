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
package com.aperigeek.dropvault.web.beans;

import java.util.Date;
import org.bson.types.ObjectId;

/**
 *
 * @author Vivien Barousse
 */
public class Resource {
    
    public enum ResourceType {
        FILE,
        FOLDER;
    }
    
    private ObjectId id;
    
    private String name;
    
    private Date creationDate;
    
    private Date modificationDate;
    
    private ResourceType type;
    
    private String contentType;
    
    private long contentLength;

    public Resource(ObjectId id, String name, Date creationDate, Date modificationDate) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.type = ResourceType.FOLDER;
    }

    public Resource(ObjectId id, String name, Date creationDate, Date modificationDate, String contentType, long contentLength) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.type = ResourceType.FILE;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public boolean isDirectory() {
        return type == ResourceType.FOLDER;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = ResourceType.valueOf(type);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ObjectId getId() {
        return id;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
