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

import com.aperigeek.dropvault.desktop.config.LocalStorageManager;
import com.aperigeek.dropvault.desktop.ui.DropVaultFrame;
import java.io.File;
import javax.swing.UIManager;

/**
 *
 * @author Vivien Barousse
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Do nothing, report a warning?
        }
        
        File storage = new File(System.getProperty("user.home"));
        storage = new File(storage, ".dropvault");
        LocalStorageManager storageManager = new LocalStorageManager(storage);
        
        DropVaultFrame frame = new DropVaultFrame(storageManager);
        frame.setVisible(true);
    }
    
}
