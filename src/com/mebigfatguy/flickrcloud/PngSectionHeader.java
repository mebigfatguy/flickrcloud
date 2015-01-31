/*
 * flickrcloud - A file storage facility tool using flickr storage
 * Copyright 2013-2015 MeBigFatGuy.com
 * Copyright 2013-2015 Dave Brosius
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

import java.util.Arrays;

public class PngSectionHeader {

    private int length;
    byte[] type;
    
    public PngSectionHeader(int len, byte[] pngType) {
        length = len;
        type = pngType;
    }
    
    public int getLength() {
        return length;
    }
    
    public byte[] getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "Length: " + length + " Type: " + Arrays.toString(type);
    }
}
