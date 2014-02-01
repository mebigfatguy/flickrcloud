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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class PngParser {

    public File parse(File pngFile) throws IOException {
        
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(pngFile)))) {
            
            byte[] pngHeader = new byte[PngConstants.PNG_HEADER.length];
            dis.readFully(pngHeader);
            
            if (!Arrays.equals(pngHeader,  PngConstants.PNG_HEADER)) {
                throw new IOException("Invalid PNG file, header was " + Arrays.toString(pngHeader) + " but should be " + Arrays.toString(PngConstants.PNG_HEADER));
            }
            
            PngSectionHeader psh = new PngSectionHeader();
            while (readSectionHeader(dis, psh)) {
                
                if (Arrays.equals(PngConstants.IHDR, psh.type)) {
                    if (psh.size != PngConstants.IHDR_SIZE) {
                        throw new IOException("Invalid PNG File, IHDR size was " + psh.size + " but should be " + PngConstants.IHDR_SIZE);
                    }
                    
                } else if (Arrays.equals(PngConstants.FLCD, psh.type)) {
                    
                } else if (Arrays.equals(PngConstants.IDAT, psh.type)) {
                    
                } else {
                    int skipLength = psh.size;
                    while (skipLength > 0) {
                        int skipped = dis.skipBytes(skipLength);
                        skipLength -= skipped;
                    }  
                }
            }
        }
        
        
        return null;
    }
    
    private boolean readSectionHeader(DataInputStream dis, PngSectionHeader header) throws IOException {
        header.size = dis.readInt();
        dis.readFully(header.type);
        
        return !Arrays.equals(PngConstants.IEND, header.type);
    }
    
    static class PngSectionHeader {
        int size;
        byte[] type = new byte[4];
    }
}


