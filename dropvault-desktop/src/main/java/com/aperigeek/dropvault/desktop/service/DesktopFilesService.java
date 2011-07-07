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
package com.aperigeek.dropvault.desktop.service;

import com.aperigeek.dropvault.dav.DropDAVClient;
import com.aperigeek.dropvault.desktop.config.ConfigManager;
import com.aperigeek.dropvault.desktop.config.LocalStorageManager;
import com.aperigeek.dropvault.desktop.dao.DesktopFilesDAO;
import com.aperigeek.dropvault.service.AbstractFilesService;
import com.aperigeek.dropvault.service.FilesService;
import java.io.File;

/**
 *
 * @author Vivien Barousse
 */
public class DesktopFilesService extends AbstractFilesService implements FilesService {

    private LocalStorageManager storageManager;
    
    private ConfigManager configManager;
    
    public DesktopFilesService(LocalStorageManager storageManager) {
        super(new DesktopFilesDAO(storageManager.getDatabaseStorageFolder()));
        this.storageManager = storageManager;
        configManager = storageManager.getConfigManager();
    }

    public DesktopFilesService(String username, String password, LocalStorageManager storageManager) {
        this(storageManager);
        setUsername(username);
        setPassword(password);
    }

    @Override
    protected File getStorageDirectory() {
        return new File(configManager.getValue("files.dir", null));
    }

    @Override
    protected String getBaseURI() {
        try {
            return new DropDAVClient(getUsername(), getPassword()).getBaseURI();
        } catch (Exception ex) {
            throw new RuntimeException("Server unavailable", ex);
        }
    }

    @Override
    protected void setBaseURI(String baseUri) {
        
    }
    
}
