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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

import com.flickr4java.flickr.photos.Photo;

public class FileListModel extends AbstractListModel<Photo> {

    private static final long serialVersionUID = 557430370553467263L;
    
    private static Comparator<Photo> PHOTO_COMPARATOR = new Comparator<Photo>() {
        public int compare(Photo p1, Photo p2) {
            return p1.getId().compareTo(p2.getId());
        }
    };
    
    private List<Photo> files = new ArrayList<>();
    
    public void clear() {
        files.clear();
    }
    
    public void addPhoto(Photo photo) {
        files.add(photo);
        Collections.<Photo>sort(files, PHOTO_COMPARATOR);
        fireContentsChanged(this, 0, files.size());
    }
    
    @Override
    public int getSize() {
        return files.size();
    }

    @Override
    public Photo getElementAt(int index) {
        return files.get(index);
    }
}
