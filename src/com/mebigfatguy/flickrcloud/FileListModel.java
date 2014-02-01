/*
 * flickrcloud - A file storage facility tool using flickr storage
 * Copyright 2013-2014 MeBigFatGuy.com
 * Copyright 2013-2014 Dave Brosius
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
import com.mebigfatguy.flickrcloud.FileListModel.PhotoWrapper;

public class FileListModel extends AbstractListModel<PhotoWrapper> {

    private static final long serialVersionUID = 557430370553467263L;
    
    private static Comparator<PhotoWrapper> PHOTO_COMPARATOR = new Comparator<PhotoWrapper>() {
        public int compare(PhotoWrapper p1, PhotoWrapper p2) {
            return p1.photo.getId().compareTo(p2.photo.getId());
        }
    };
    
    private List<PhotoWrapper> files = new ArrayList<>();
    
    public void clear() {
        files.clear();
    }
    
    public void addPhoto(Photo photo) {
        files.add(new PhotoWrapper(photo));
        Collections.<PhotoWrapper>sort(files, PHOTO_COMPARATOR);
        fireContentsChanged(this, 0, files.size());
    }
    
    @Override
    public int getSize() {
        return files.size();
    }

    @Override
    public PhotoWrapper getElementAt(int index) {
        return files.get(index);
    }
    
    static class PhotoWrapper {
        Photo photo;
        
        public PhotoWrapper(Photo ph) {
            photo = ph;
        }
        
        @Override
        public String toString() {
            return photo.getTitle();
        }
    }
}
