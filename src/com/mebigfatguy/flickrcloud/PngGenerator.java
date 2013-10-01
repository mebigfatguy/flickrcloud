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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PngGenerator {

    public Map<String, File> generate(List<File> transferData) throws IOException {
        Map<String, File> images = new HashMap<String, File>();
        for (File sourceFile : transferData) {
            File f;
            if (sourceFile.isDirectory()) {
                f = createDirectoryZip(sourceFile);
            } else {
                f = sourceFile;
            }
            
            f = createImageFile(f);
            
            images.put(f.getName(), f);
        }
        
        return images;
    }
    
    private File createDirectoryZip(File directory) throws IOException {
        File zipFile = File.createTempFile("zip", ".zip");
        zipFile.deleteOnExit();
        
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
            
            zos.setLevel(9);
            zos.setMethod(ZipOutputStream.DEFLATED);

            addChildFile(zos, directory, directory.getPath().length() + 1);              
        }
        
        return zipFile;
    }
    
    private void addChildFile(ZipOutputStream zos, File child, int rootPathLength) throws IOException {
        
        boolean isRoot = (rootPathLength >= child.getPath().length());
        if (!isRoot) {
            String relativePath = child.getPath().substring(rootPathLength);
            ZipEntry ze = new ZipEntry(relativePath);
            zos.putNextEntry(ze);
        }
        
        if (child.isDirectory()) {
            for (File subChild : child.listFiles()) {
                addChildFile(zos, subChild, rootPathLength);
            }
        } else {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(child))) {
                byte[] data = new byte[2048];
                int size = bis.read(data);
                while (size >= 0) {
                    zos.write(data, 0, size);
                    size = bis.read(data);
                }
            }
        }
        
        if (!isRoot) {
            zos.closeEntry();
        }
    }
    
    private File createImageFile(File file) {
        return file;
    }

}
