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

import java.util.ResourceBundle;

public class FCBundle {
    enum Keys {
        TITLE,
        KEY_PROMPT,
        API_KEY,
        SECRET,
        ENTER_VERIFICATION_CODE,
        OK,
        CANCEL,
        SAVE;
    }
    
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("strings");
    
    private FCBundle() {
    }
    
    public static String getString(Keys key) {
        return BUNDLE.getString(key.name().toLowerCase());
    }

}
