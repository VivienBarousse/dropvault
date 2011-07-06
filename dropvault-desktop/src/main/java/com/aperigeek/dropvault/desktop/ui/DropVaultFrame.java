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
package com.aperigeek.dropvault.desktop.ui;

import com.aperigeek.dropvault.desktop.service.DesktopFilesService;
import com.aperigeek.dropvault.desktop.ui.event.FolderSelectionListener;
import com.aperigeek.dropvault.desktop.ui.event.LoginListener;
import com.aperigeek.dropvault.desktop.ui.layout.CenteredLayout;
import com.aperigeek.dropvault.service.SyncException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Vivien Barousse
 */
public class DropVaultFrame extends JFrame {
    
    private JPanel glassPane;

    private JPanel mainPane;
    
    private JPanel syncPane;
    
    private JButton syncButton;
    
    private DesktopFilesService filesService;

    public DropVaultFrame() {
        filesService = new DesktopFilesService();

        init();

        setTitle("DropVault");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    filesService.close();
                } catch (RuntimeException ex) {
                    // Do nothing, improper shutdown... Report a warning?
                }
            }
        
        });
        
        step1();
    }

    private void init() {
        mainPane = new JPanel(new BorderLayout());

        syncPane = new JPanel(new CenteredLayout());

        syncButton = new JButton("Sync");
        syncButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                sync();
            }
            
        });

        syncPane.add(syncButton);

        mainPane.add(syncPane);
        
        setContentPane(mainPane);
    }
    
    private void step1() {
        glassPane = (JPanel) getGlassPane();
        glassPane.setLayout(new CenteredLayout());
        glassPane.setOpaque(true);
        glassPane.setVisible(true);
        
        FolderSelectionPane folderSelectionPane = new FolderSelectionPane();
        
        folderSelectionPane.addFolderSelectionListener(new FolderSelectionListener() {

            @Override
            public void folderSelected(File folder) {
                filesService.setStorageDirectory(folder);
                glassPane.setVisible(false);
                step2();
            }
            
        });
        
        glassPane.removeAll();
        glassPane.add(folderSelectionPane);
    }
    
    private void step2() {
        glassPane = (JPanel) getGlassPane();
        glassPane.setLayout(new CenteredLayout());
        glassPane.setOpaque(true);
        glassPane.setVisible(true);
        
        LoginPane loginPane = new LoginPane();
        
        loginPane.addLoginListener(new LoginListener() {

            @Override
            public void loggedIn(String username, char[] password) {
                filesService.setUsername(username);
                filesService.setPassword(new String(password));
                glassPane.setVisible(false);
            }
            
        });
        
        glassPane.removeAll();
        glassPane.add(loginPane);
    }
    
    private void sync() {
        try {
            filesService.sync();
        } catch (SyncException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error during sync", JOptionPane.ERROR_MESSAGE);
        }
    }
}
