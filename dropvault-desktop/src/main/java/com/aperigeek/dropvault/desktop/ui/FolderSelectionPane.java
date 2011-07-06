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

import com.aperigeek.dropvault.desktop.ui.event.FolderSelectionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Vivien Barousse
 */
public class FolderSelectionPane extends JPanel {

    private JLabel messageLabel;
    
    private JPanel filePanel;
    
    private JTextField fileNameField;
    
    private JButton chooseFileButton;
    
    private JPanel actionPanel;
    
    private JButton okButton;
    
    private JFileChooser fileChooser;
    
    private List<FolderSelectionListener> listeners;
    
    public FolderSelectionPane() {
        listeners = new ArrayList<FolderSelectionListener>();
        
        init();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        messageLabel = new JLabel("Please select the folder to synchronize", JLabel.CENTER);
        
        filePanel = new JPanel();
        
        fileNameField = new JTextField(15);
        fileNameField.setEnabled(false);
        
        chooseFileButton = new JButton("Choose...");
        chooseFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFolder();
            }
            
        });
        
        filePanel.add(fileNameField);
        filePanel.add(chooseFileButton);
        
        actionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ok();
            }
            
        });
        
        actionPanel.add(okButton);
        
        add(messageLabel, BorderLayout.NORTH);
        add(filePanel);
        add(actionPanel, BorderLayout.SOUTH);
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    
    private void chooseFolder() {
        fileChooser.showOpenDialog(this);
        fileNameField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }
    
    private void ok() {
        for (FolderSelectionListener l : listeners) {
            l.folderSelected(getSelectedFile());
        }
    }
    
    public File getSelectedFile() {
        return fileChooser.getSelectedFile();
    }
    
    public void addFolderSelectionListener(FolderSelectionListener l) {
        listeners.add(l);
    }
    
    public void removeFolderSelectionListener(FolderSelectionListener l) {
        listeners.remove(l);
    }
    
}
