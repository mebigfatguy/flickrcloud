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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class FileListModel extends AbstractListModel {

    private static final long serialVersionUID = 557430370553467263L;
    private List<String> files = new ArrayList<String>();
    
    public void resetFiles(Collection<String> newFiles) {
        files.clear();
        addFiles(newFiles);
    }
    
    public void addFiles(Collection<String> newFiles) {
        files.addAll(newFiles);
        Collections.sort(files);
        fireContentsChanged(this, 0, files.size());
    }
    
    @Override
    public int getSize() {
        return files.size();
    }

    @Override
    public Object getElementAt(int index) {
        return files.get(index);
    }
}
