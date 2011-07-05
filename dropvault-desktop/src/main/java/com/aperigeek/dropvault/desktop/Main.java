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
package com.aperigeek.dropvault.desktop;

import com.aperigeek.dropvault.desktop.service.DesktopFilesService;
import com.aperigeek.dropvault.service.FilesService;
import com.aperigeek.dropvault.service.SyncException;

/**
 *
 * @author Vivien Barousse
 */
public class Main {
    
    public static void main(String[] args) throws SyncException {
        FilesService service = new DesktopFilesService("viv", "viv");
        service.sync();
    }
    
}
