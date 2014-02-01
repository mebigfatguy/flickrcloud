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

import java.io.File;
import java.util.Map;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;

public class FlickrPublisher {
    
    public FlickrPublisher() {
    }
    
    public void publish(Map<String, File> images) throws FlickrException {
        Flickr flickr = FlickrFactory.getFlickr();
        if (flickr == null) {
            return;
        }
        
        UploadMetaData meta = new UploadMetaData();
        meta.setAsync(false);
        meta.setContentType(Flickr.CONTENTTYPE_OTHER);
        meta.setFamilyFlag(false);
        meta.setFriendFlag(false);
        meta.setPublicFlag(false);
        meta.setSafetyLevel(Flickr.SAFETYLEVEL_SAFE);
        
        Uploader uploader = flickr.getUploader();
        for (Map.Entry<String, File> entry : images.entrySet()) {

            meta.setTitle(entry.getKey());
            uploader.upload(entry.getValue(), meta);
        }
    }
}
