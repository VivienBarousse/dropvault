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
package com.aperigeek.dropvault;

import java.util.Date;

/**
 *
 * @author Vivien Barousse
 */
public class Resource {
    
    public static final int CREATED = 1;
    
    public static final int DELETED = 1 << 1;
    
    public enum ResourceType {
        FILE,
        FOLDER;
    }
    
    private ResourceType type;
    
    private String href;
    
    private String name;
    
    private String contentType;
    
    private Date lastModificationDate;
    
    private int flags;

    public Resource() {
    }

    public Resource(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
    
    public boolean getCreated() {
        return (flags & CREATED) != 0;
    }
    
    public void setCreated(boolean created) {
        if (created) {
            flags |= CREATED;
        } else {
            flags &= 0xFFFFFFFF ^ CREATED;
        }
    }
    
    public boolean getDeleted() {
        return (flags & DELETED) != 0;
    }
    
    public void setDeleted(boolean deleted) {
        if (deleted) {
            flags |= DELETED;
        } else {
            flags &= 0xFFFFFFFF ^ DELETED;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Resource other = (Resource) obj;
        if ((this.href == null) ? (other.href != null) : !this.href.equals(other.href)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.href != null ? this.href.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Resource{" + "href=" + href + '}';
    }
    
}
