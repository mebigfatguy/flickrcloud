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
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

public class PngGenerator {

    private static final int MAX_IDAT_SIZE = 1024 * 1024;
    
    public Map<String, File> generate(List<File> transferData) throws IOException {
        Map<String, File> images = new HashMap<>();
        for (File sourceFile : transferData) {
            File f;
            boolean isImage = false;
            if (sourceFile.isDirectory()) {
                f = createDirectoryZip(sourceFile);
            } else {
                f = sourceFile;
                int dotPos = f.getName().lastIndexOf('.');
                if (dotPos >= 0) {
                    Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix(f.getName().substring(dotPos+1));
                    isImage = it.hasNext();
                }
            }
            
            if (!isImage) {
                f = createImageFile(f, sourceFile);
            }
            
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
    
    private static File createImageFile(File file, File sourceFile) throws IOException {
        File pngFile = File.createTempFile(file.getName(), ".png");
        pngFile.deleteOnExit();
        
        long sourceLength = file.length();
        long numPixels = sourceLength / 3;
        int width = (int) Math.sqrt(numPixels);
        int height = (int) ((numPixels + width - 1) / width);
        
        try (RandomAccessFile raf = new RandomAccessFile(pngFile, "rw")) {
            raf.write(PngConstants.PNG_HEADER);

            writeIHDR(raf, width, height);
            
            writeFLCD(raf, file, sourceFile);
            
            writeIDATs(raf, file, width);
            
            writeIEND(raf);
        }
        
        return pngFile;
    }

    private static void writeIHDR(DataOutput out, int width, int height) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(PngConstants.IHDR_SIZE);
        dos.write(PngConstants.IHDR);
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
        
        out.write(baos.toByteArray());
    }
    
    private static void writeFLCD(DataOutput out, File file, File sourceFile) throws IOException {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        byte[] sourceFileName = sourceFile.getName().getBytes(StandardCharsets.UTF_8);
        
        dos.writeInt(8 + 4 + sourceFileName.length + 1);
        dos.write(PngConstants.FLCD);
        dos.writeLong(file.length());
        dos.writeInt(sourceFileName.length);
        dos.write(sourceFileName);
        dos.write(file.equals(sourceFile) ? 0 : -1);
        
        dos.flush();
        
        byte[] data = baos.toByteArray();

        CRC32 crc = new CRC32();
        crc.update(data, 4, data.length - 4);

        dos.writeInt((int) crc.getValue());
        dos.flush();
        
        out.write(baos.toByteArray());
    }
    
    private static void writeIDATs(DataOutput out, File file, int width) throws IOException {
        File scanFile = File.createTempFile(file.getName(), ".scan");
        scanFile.deleteOnExit();
        
        byte[] scanLine = new byte[width * 3 + 1];
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             DeflaterOutputStream dos = new DeflaterOutputStream(new BufferedOutputStream(new FileOutputStream(scanFile)))) {
            
            while (readScanLine(bis, scanLine)) {
                dos.write(scanLine);
            }
        }
        
        long length = scanFile.length();
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(scanFile))) {
            
            while (length > MAX_IDAT_SIZE) {
                writeIDAT(out, bis, MAX_IDAT_SIZE);
                length -= MAX_IDAT_SIZE;
            }
            
            if (length > 0) {
                writeIDAT(out, bis, length);
            }
        } finally {
            scanFile.delete();
        }
    }
    
    private static void writeIDAT(DataOutput out, InputStream in, long remaining) throws IOException {
        int idatSize = (int) Math.min(remaining, MAX_IDAT_SIZE);
        out.write(ByteBuffer.allocate(4).putInt(idatSize).array());
        out.write(PngConstants.IDAT);
        
        CRC32 crc = new CRC32();
        crc.update(PngConstants.IDAT);
        byte[] buffer = new byte[4096];
        
        int read = in.read(buffer, 0, Math.min(idatSize, 4096));
        while (read > 0) {
            crc.update(buffer, 0, read);
            out.write(buffer, 0, read);
            idatSize -= read;
            
            read = in.read(buffer, 0, Math.min(idatSize, 4096));
        }  

        out.write(ByteBuffer.allocate(4).putInt((int) crc.getValue()).array());
    }
    
    private static void writeIEND(DataOutput out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(0);
        dos.write(PngConstants.IEND);
        
        dos.flush();
        
        byte[] data = baos.toByteArray();

        CRC32 crc = new CRC32();
        crc.update(data, 4, data.length - 4);

        dos.writeInt((int) crc.getValue());
        dos.flush();
        
        out.write(baos.toByteArray());
    }
    
    /**
     * the first byte of the scanline is a flag, so we ignore this byte.
     */
    private static boolean readScanLine(InputStream is, byte[] scanLine) throws IOException {
        
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
