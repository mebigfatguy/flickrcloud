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

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.dnd.DropTarget;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class FCFrame extends JFrame implements FCDropTarget  {
	
    private static final long serialVersionUID = 4415151392343515256L;
    
    private final DropLabel store;
	private final FileListModel model;

	public FCFrame() {
		super(FCBundle.getString(FCBundle.Keys.TITLE));

		Container cp = getContentPane();
		cp.setLayout(new GridLayout(1, 2));

		store = new DropLabel();

		DropTarget dropTarget = new DropTarget(store, new FlickrDropTargetListener(this));
		store.setDropTarget(dropTarget);
		
		model = new FileListModel();
		JList<String> files = new JList<String>(model);
		files.setPreferredSize(store.getPreferredSize());

		cp.add(store);
		cp.add(new JScrollPane(files));
		pack();
	}

    @Override
    public void setHilite() {
        store.setHilite();
        
    }

    @Override
    public void setNormal() {
        store.setNormal();
    }

    @Override
    public void add(Set<String> descriptors) {
        model.addFiles(descriptors);
    }

    @Override
    public Component getDropOwner() {
        return this;
    }
}
