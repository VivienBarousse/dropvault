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
package com.aperigeek.dropvault.android.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.aperigeek.dropvault.android.Resource;
import com.aperigeek.dropvault.android.Resource.ResourceType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vivien Barousse
 */
public class FilesDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "FILES";
    
    private static final int DB_VERSION = 3;

    public FilesDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE files ("
                + "name TEXT,"
                + "parent TEXT,"
                + "type INTEGER,"
                + "content_type TEXT,"
                + "href TEXT PRIMARY KEY"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE files;");
        onCreate(db);
    }
    
    public Resource getParent(Resource resource) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT p.name, p.href, p.type, p.content_type "
                + "FROM files AS c "
                + "INNER JOIN files AS p "
                + "ON c.parent = p.href "
                + "WHERE c.href=?",
                new String[]{resource.getHref()});
        
        if (!cursor.moveToNext()) {
            return null;
        }
        
        Resource parent = new Resource();
        parent.setName(cursor.getString(0));
        parent.setHref(cursor.getString(1));
        parent.setType(ResourceType.valueOf(cursor.getString(2)));
        parent.setContentType(cursor.getString(3));
        
        return parent;
    }
    
    public Resource getResource(String baseURI) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, href, type, content_type FROM files WHERE href=?",
                new String[]{baseURI});
        
        if (!cursor.moveToNext()) {
            return null;
        }
        
        Resource resource = new Resource();
        resource.setName(cursor.getString(0));
        resource.setHref(cursor.getString(1));
        resource.setType(ResourceType.valueOf(cursor.getString(2)));
        resource.setContentType(cursor.getString(3));
        
        return resource;
    }
    
    public List<Resource> getChildren(Resource parent) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, href, type, content_type FROM files WHERE parent=?",
                new String[]{parent.getHref()});
        
        List<Resource> resources = new ArrayList<Resource>();
        
        while (cursor.moveToNext()) {
            Resource resource = new Resource();
            resource.setName(cursor.getString(0));
            resource.setHref(cursor.getString(1));
            resource.setType(ResourceType.valueOf(cursor.getString(2)));
            resource.setContentType(cursor.getString(3));
            resources.add(resource);
        }
        
        return resources;
    }
    
    public List<Resource> getAllResources() {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name, href, type, content_type FROM files",
                new String[]{});
        
        List<Resource> resources = new ArrayList<Resource>();
        
        while (cursor.moveToNext()) {
            Resource resource = new Resource();
            resource.setName(cursor.getString(0));
            resource.setHref(cursor.getString(1));
            resource.setType(ResourceType.valueOf(cursor.getString(2)));
            resource.setContentType(cursor.getString(3));
            resources.add(resource);
        }
        
        return resources;
    }
    
    public void insert(Resource resource) {
        getWritableDatabase().execSQL("INSERT INTO files(name,href,type,content_type)"
                + "VALUES (?, ?, ?, ?)", 
                new Object[]{
                    resource.getName(),
                    resource.getHref(),
                    resource.getType().toString(),
                    resource.getContentType()
                });
    }
    
    public void insert(Resource parent, Resource resource) {
        getWritableDatabase().execSQL("INSERT INTO files(name,href,type,content_type, parent)"
                + "VALUES (?, ?, ?, ?, ?)", 
                new Object[]{
                    resource.getName(),
                    resource.getHref(),
                    resource.getType().toString(),
                    resource.getContentType(),
                    parent.getHref()
                });
    }
    
    public void clear() {
        getWritableDatabase().execSQL("DELETE FROM files;");
    }

    public void removeAll(List<Resource> remove) {
        for (Resource res : remove) {
            remove(res);
        }
    }

    public void remove(Resource res) {
        getWritableDatabase().execSQL("DELETE FROM files WHERE href=?",
                new String[]{res.getHref()});
    }
    
}
