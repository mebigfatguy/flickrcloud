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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;

public class FilesPopulator extends MouseAdapter {

    FileListModel model;
    public FilesPopulator(FileListModel fileModel) {
        model = fileModel;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        
        try {
            Flickr flickr = FlickrFactory.getFlickr();
            if (flickr == null) {
                return;
            }
            
            model.clear();
            
            User u = RequestContext.getRequestContext().getAuth().getUser();
            PhotoList<Photo> photos = flickr.getPeopleInterface().getPhotos(u.getId(), null, null, null, null, null, Flickr.CONTENTTYPE_OTHER, null, null, 0, 0);

            for (Photo photo : photos) {
                model.addPhoto(photo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getClass().getName() + ": " + e.getMessage());
        }
    }

}
