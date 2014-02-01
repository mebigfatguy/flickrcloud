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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mebigfatguy.flickrcloud.FileListModel.PhotoWrapper;

public class FileGenerator {

    public List<File> generate(List<PhotoWrapper> photos) throws MalformedURLException, IOException {
        
        List<File> files = new ArrayList<File>();
        for (PhotoWrapper wrapper : photos) {
            files.add(createFile(wrapper.photo.getUrl()));
        }
        
        return files;
    }

    private File createFile(String url) throws MalformedURLException, IOException {
        URL u = new URL(url);
        File pngFile = File.createTempFile("png", ".png");
        System.out.println(u);
        try (BufferedInputStream bis = new BufferedInputStream(u.openStream());
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pngFile))) {
            System.out.println("File opened");
            byte[] data = new byte[2048];
            int len = bis.read(data);
            while (len >= 0) {
                bos.write(data, 0, len);
                len = bis.read(data);
            }
        }
        
        return pngFile;
    }
}
