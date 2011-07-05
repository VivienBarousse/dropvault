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

import com.aperigeek.dropvault.dav.DAVException;
import com.aperigeek.dropvault.dav.DropDAVClient;
import com.aperigeek.dropvault.dav.InvalidPasswordException;
import com.aperigeek.dropvault.desktop.dao.DesktopFilesDAO;
import com.aperigeek.dropvault.service.AbstractFilesService;
import com.aperigeek.dropvault.service.FilesService;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class DesktopFilesService extends AbstractFilesService implements FilesService {

    public DesktopFilesService() {
        super(new DesktopFilesDAO(new File(new File(System.getProperty("user.home")), "DropVaultDB")));
    }

    public DesktopFilesService(String username, String password) {
        super(username, password, new DesktopFilesDAO(new File(new File(System.getProperty("user.home")), "DropVaultDB")));
    }

    @Override
    protected File getStorageDirectory() {
        File userHome = new File(System.getProperty("user.home"));
        File folder = new File(userHome, "DropVault");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
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
