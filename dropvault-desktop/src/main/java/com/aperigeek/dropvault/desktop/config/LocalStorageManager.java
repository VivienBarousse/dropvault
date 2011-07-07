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
package com.aperigeek.dropvault.desktop.config;

import java.io.File;

/**
 *
 * @author Vivien Barousse
 */
public class LocalStorageManager {
    
    private ConfigManager configManager;

    private File storageFolder;
    
    public LocalStorageManager(File storageFolder) {
        this.storageFolder = storageFolder;
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }
    
    public ConfigManager getConfigManager() {
        if (configManager == null) {
            configManager = new ConfigManager(new File(storageFolder, "config.properties"));
        }
        return configManager;
    }
    
    public File getDatabaseStorageFolder() {
        return new File(storageFolder, "filesdb");
    }
    
}
