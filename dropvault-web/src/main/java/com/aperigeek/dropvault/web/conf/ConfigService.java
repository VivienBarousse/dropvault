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
package com.aperigeek.dropvault.web.conf;

import java.io.File;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Vivien Barousse
 */
@Singleton
@Startup
public class ConfigService {
    
    private static final File STORAGE_FOLDER = new File("/path/to/storage");
    
    @PostConstruct
    protected void init() {
        if (!STORAGE_FOLDER.exists()) {
            STORAGE_FOLDER.mkdirs();
        }
    }
    
    public File getStorageFolder() {
        return STORAGE_FOLDER;
    }
    
    public File getStorageFolder(String username) {
        File file = new File(STORAGE_FOLDER, username);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
    
    public File getStorageFolder(String username, String subPath) {
        return new File(getStorageFolder(username), subPath);
    }
    
}
