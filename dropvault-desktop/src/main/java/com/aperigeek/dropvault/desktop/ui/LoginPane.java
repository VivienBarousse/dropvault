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

import com.aperigeek.dropvault.desktop.ui.event.LoginListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Vivien Barousse
 */
public class LoginPane extends JPanel {

    private JLabel messageLabel;
    
    private JPanel loginPane;
    
    private JLabel loginLabel;
    
    private JTextField loginField;
    
    private JLabel passwordLabel;
    
    private JPasswordField passwordField;
    
    private JPanel actionPane;
    
    private JButton okButton;
    
    private List<LoginListener> listeners;
    
    public LoginPane() {
        listeners = new ArrayList<LoginListener>();
        
        init();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        messageLabel = new JLabel("Please enter your credentials", JLabel.CENTER);
        
        loginPane = new JPanel(new GridLayout(2, 2));
        
        loginLabel = new JLabel("Login");
        
        loginField = new JTextField(10);
        
        passwordLabel = new JLabel("Password");
        
        passwordField = new JPasswordField(10);
        
        loginPane.add(loginLabel);
        loginPane.add(loginField);
        loginPane.add(passwordLabel);
        loginPane.add(passwordField);
        
        actionPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ok();
            }
            
        });
        
        actionPane.add(okButton);
        
        add(messageLabel, BorderLayout.NORTH);
        add(loginPane);
        add(actionPane, BorderLayout.SOUTH);
    }
    
    private void ok() {
        for (LoginListener l : listeners) {
            l.loggedIn(getUsername(), getPassword());
        }
    }
    
    public void addLoginListener(LoginListener l) {
        listeners.add(l);
    }
    
    public void removeLoginListener(LoginListener l) {
        listeners.remove(l);
    }
    
    public String getUsername() {
        return loginField.getText();
    }
    
    public void setUsername(String username) {
        loginField.setText(username);
    }
    
    public char[] getPassword() {
        return passwordField.getPassword();
    }
    
    public void setPassword(char[] password) {
        setPassword(new String(password));
    }
    
    public void setPassword(String password) {
        passwordField.setText(password);
    }
    
}
