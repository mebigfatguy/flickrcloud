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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PngGenerator {

    private static byte[] PNG_HEADER = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static byte[] L13 = { 0, 0, 0, 0x0D };
    private static byte[] IHDR = "IHDR".getBytes();
    private static byte[] IDAT = "IDAT".getBytes();
    private static byte[] IEND = "IEND".getBytes();
    
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
    
    private File createImageFile(File file) throws IOException {
        File pngFile = File.createTempFile(file.getName(), ".png");
        pngFile.deleteOnExit();
        
        long sourceLength = file.length();
        long numPixels = sourceLength / 3;
        int width = (int) Math.sqrt(numPixels);
        int height = (int) ((numPixels + width - 1) / width);
        
        try (RandomAccessFile raf = new RandomAccessFile(pngFile, "rw")) {
            raf.write(PNG_HEADER);

            writeIHDR(raf, width, height);
            
            writeIDAT(raf, file, width, height);
            
            writeIEND(raf);
        }
        
        return pngFile;
    }

    private void writeIHDR(RandomAccessFile raf, int width, int height) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(13);
        dos.write(IHDR);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.write(8);
        dos.write(2);
        dos.write(0);
        dos.write(0);
        dos.write(0);
        
        dos.flush();
        
        byte[] data = baos.toByteArray();

        CRC32 crc = new CRC32();
        crc.update(data, 4, data.length - 4);

        dos.writeInt((int) crc.getValue());
        dos.flush();
        
        raf.write(baos.toByteArray());
    }
    
    
    private void writeIDAT(RandomAccessFile raf, File file, int width, int height) throws IOException {
        byte[] scanLine = new byte[width * 3 + 1];
        CRC32 crc = new CRC32();
        
        raf.write(ByteBuffer.allocate(4).putInt(width * 3 * height).array());
        raf.write(IDAT);
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            
            while (readScanLine(bis, scanLine)) {
                crc.update(scanLine);
                
                raf.write(scanLine);
            }
        }
        
        raf.write(ByteBuffer.allocate(4).putInt((int) crc.getValue()).array());
    }
    
    private void writeIEND(RandomAccessFile raf) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(0);
        dos.write(IEND);
        
        dos.flush();
        
        byte[] data = baos.toByteArray();

        CRC32 crc = new CRC32();
        crc.update(data, 4, data.length - 4);

        dos.writeInt((int) crc.getValue());
        dos.flush();
        
        raf.write(baos.toByteArray());
    }
    
    private boolean readScanLine(InputStream is, byte[] scanLine) throws IOException {
        
        int start = 1;
        int length = scanLine.length - 1;
        
        int read = is.read(scanLine, start, length);
        while ((length > 0) && (read >= 0)) {
            start += read;
            length -= read;
            read = is.read(scanLine, start, length);
        }
        
        return start > 1;
    }
}
