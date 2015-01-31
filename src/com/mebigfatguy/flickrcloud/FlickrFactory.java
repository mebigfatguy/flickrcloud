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

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;

public class FlickrFactory {

    private static Flickr FLICKR;
    private static Token ACCESS_TOKEN;
    
    static {
        initFlickr();
    }
    
    private FlickrFactory() {   
    }
    
    public static Flickr getFlickr() {
        try {
            initFlickr();
            
            if (FLICKR != null) {
                AuthInterface auth = FLICKR.getAuthInterface();
                RequestContext.getRequestContext().setAuth(auth.checkToken(ACCESS_TOKEN));
            }
            return FLICKR;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static void initFlickr() {
        if (FLICKR == null) {
            String apiKey = FlickrKey.getKey();
            if (apiKey != null) {
                String secret = FlickrKey.getSecret();
                if (secret != null) {
                    FLICKR = new Flickr(FlickrKey.getKey(), FlickrKey.getSecret(), new REST());
                    AuthInterface auth = FLICKR.getAuthInterface();
                    Token t = auth.getRequestToken();
                    String authURL = auth.getAuthorizationUrl(t, Permission.WRITE);
                    
                    String verifierString = getVerifierString(authURL);
                    
                    ACCESS_TOKEN = auth.getAccessToken(t,  new Verifier(verifierString));
                }
            }
            
            if (ACCESS_TOKEN == null) {
                FLICKR = null;
            }
        }
    }
    
    private static String getVerifierString(String authURL) {
        try {
            Desktop.getDesktop().browse(new URI(authURL));
            
            return JOptionPane.showInputDialog(FCBundle.getString(FCBundle.Keys.ENTER_VERIFICATION_CODE));
        } catch (Exception i) {
            return null;
        }
    }
}
