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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Vivien Barousse
 */
public class ConfigManager {
    
    private Properties properties;
    
    private File storageFile;

    public ConfigManager(File storageFile) {
        this.storageFile = storageFile;
        load();
    }
    
    public String getValue(String key, String def) {
        return properties.getProperty(key, def);
    }
    
    public void setValue(String key, String value) {
        properties.setProperty(key, value);
        save();
    }
    
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }
    
    protected final void load() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream(storageFile));
        } catch (IOException ex) {
            // TODO: Warn
        }
    }
    
    protected final void save() {
        try {
            properties.store(new FileOutputStream(storageFile), "DropVault configuration");
        } catch (IOException ex) {
            // TODO: Warn
        }
    }
    
}
