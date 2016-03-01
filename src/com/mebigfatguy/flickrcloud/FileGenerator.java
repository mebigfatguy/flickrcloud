/*
 * flickrcloud - A file storage facility tool using flickr storage
 * Copyright 2013-2016 MeBigFatGuy.com
 * Copyright 2013-2016 Dave Brosius
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
import java.io.DataInput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mebigfatguy.flickrcloud.FileListModel.PhotoWrapper;

public class FileGenerator {

    public List<File> generate(List<PhotoWrapper> photos) throws MalformedURLException, IOException {
        
        List<File> files = new ArrayList<File>();
        for (PhotoWrapper wrapper : photos) {
            File pngFile = createFile(wrapper.photo.getUrl());
            files.add(readDatFile(pngFile));
            pngFile.delete();
        }
        
        return files;
    }

    private static File createFile(String url) throws MalformedURLException, IOException {
        URL u = new URL(url);
        File pngFile = File.createTempFile("png", ".png");
        pngFile.deleteOnExit();
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
    
    private static File readDatFile(File pngFile) throws IOException {
        File datFile = File.createTempFile("dat", ".dat");
        datFile.deleteOnExit();
        try (RandomAccessFile raf = new RandomAccessFile(pngFile, "r")) {
            byte[] type = null;
            do {
                PngSectionHeader hdr = readChunk(raf);
                type = hdr.getType();
                byte[] data = new byte[hdr.getLength()];
                raf.readFully(data);
                
                if (Arrays.equals(type, PngConstants.IHDR)) {
                    
                } else if (Arrays.equals(type, PngConstants.FLCD)) {
                    
                } else if (Arrays.equals(type, PngConstants.IDAT)) {
                    
                }
            } while (!Arrays.equals(type, PngConstants.IEND));
        }
        
        return datFile;
    }
    
    private static PngSectionHeader readChunk(DataInput din) throws IOException {
        int len = din.readInt();
        byte[] type = new byte[PngConstants.PNG_TYPE_SIZE];
        din.readFully(type);
        
        return new PngSectionHeader(len, type);
    }
    
    
}
