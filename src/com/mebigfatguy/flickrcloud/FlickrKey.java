/*
 * flickrcloud - A file storage facility tool using flickr storage
 * Copyright 2013 MeBigFatGuy.com
 * Copyright 2013 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mebigfatguy.flickrcloud;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

public class FlickrKey extends JDialog {

    private static final long serialVersionUID = 1190300422775263643L;
    private static String KEY = null;
    private static String SECRET = null;
    
    private boolean ok = false;
    
    private FlickrKey() {  
    }
    
    public static String getKey() {
        if (KEY == null) {
            setInfo();
        }
        
        return KEY;
    }
    
    public static String getSecret() {
        if (SECRET == null) {
            setInfo();
        }
        
        return SECRET;
    }
    
    private static void setInfo() {
        final FlickrKey d = new FlickrKey();
        d.setTitle(FCBundle.getString(FCBundle.Keys.KEY_PROMPT));
        Container cp = d.getContentPane();
        cp.setLayout(new BorderLayout(4, 4));
        
        JPanel p = new JPanel();
        GroupLayout glayout = new GroupLayout(p);
        p.setLayout(glayout);
        glayout.setAutoCreateGaps(true);
        glayout.setAutoCreateContainerGaps(true);
        
        GroupLayout.SequentialGroup hGroup = glayout.createSequentialGroup();
        JLabel apiKeyLabel = new JLabel(FCBundle.getString(FCBundle.Keys.API_KEY));
        JLabel secretLabel = new JLabel(FCBundle.getString(FCBundle.Keys.SECRET));
        JTextField apiKey = new JTextField(25);
        JTextField secret = new JTextField(25);
        apiKeyLabel.setLabelFor(apiKey);
        secretLabel.setLabelFor(secret);
        hGroup.addGroup(glayout.createParallelGroup().addComponent(apiKeyLabel).addComponent(secretLabel));
        hGroup.addGroup(glayout.createParallelGroup().addComponent(apiKey).addComponent(secret));
        glayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = glayout.createSequentialGroup();
        vGroup.addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(apiKeyLabel).addComponent(apiKey));
        vGroup.addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(secretLabel).addComponent(secret));
        glayout.setVerticalGroup(vGroup);
        
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
        
        d.add(p, BorderLayout.CENTER);
        
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.X_AXIS));
        ctrlPanel.add(Box.createHorizontalStrut(10));
        JButton cancelButton = new JButton(FCBundle.getString(FCBundle.Keys.CANCEL));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                d.dispose();
            }
        });
        ctrlPanel.add(cancelButton);
        ctrlPanel.add(Box.createHorizontalGlue());
        JButton okButton = new JButton(FCBundle.getString(FCBundle.Keys.OK));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                d.ok = true;
                d.dispose();
            }
        });
        ctrlPanel.add(okButton);
        ctrlPanel.add(Box.createHorizontalStrut(10));
        ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        d.add(ctrlPanel, BorderLayout.SOUTH);
        
        d.pack();
        d.setLocationRelativeTo(null);
        d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        d.setModal(true);
        d.setVisible(true);
        
        if (d.isOK()) {
            KEY = apiKey.getText();
            SECRET = secret.getText();
        }
    }
    
    boolean isOK() {
        return ok;
    }
}
