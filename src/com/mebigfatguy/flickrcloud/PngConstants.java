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

import java.nio.charset.Charset;

public class PngConstants {

    static final byte[] PNG_HEADER = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    static final  byte[] IHDR;
    static final  byte[] FLCD;
    static final  byte[] IDAT;
    static final  byte[] IEND;
    
    static final int IHDR_SIZE = 4 + 4 + 1 + 1 + 1 + 1 + 1;
    
    static final int PNG_TYPE_SIZE = 4;
    
    static
    {
        Charset cs = Charset.forName("UTF-8");
        IHDR = "IHDR".getBytes(cs);
        FLCD = "flCD".getBytes(cs);
        IDAT = "IDAT".getBytes(cs);
        IEND = "IEND".getBytes(cs);
    }   
    
    private PngConstants() {
    }
}
