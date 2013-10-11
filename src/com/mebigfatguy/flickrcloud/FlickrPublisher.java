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

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.Map;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import javax.swing.JOptionPane;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;

public class FlickrPublisher {
    
    private  Flickr flickr;
    private Token accessToken;
    
    public FlickrPublisher() {
        initFlickr();
    }
    
    public void publish(Map<String, File> images) throws FlickrException {
        initFlickr();
        if (flickr == null) {
            return;
        }
        
        AuthInterface auth = flickr.getAuthInterface();
        RequestContext.getRequestContext().setAuth(auth.checkToken(accessToken));
        
        UploadMetaData meta = new UploadMetaData();
        meta.setAsync(false);
        meta.setContentType(Flickr.CONTENTTYPE_OTHER);
        meta.setFamilyFlag(false);
        meta.setFriendFlag(false);
        meta.setPublicFlag(false);
        meta.setSafetyLevel(Flickr.SAFETYLEVEL_SAFE);
        
        Uploader uploader = flickr.getUploader();
        for (Map.Entry<String, File> entry : images.entrySet()) {

            meta.setTitle(entry.getKey());
            uploader.upload(entry.getValue(), meta);
        }
    }
    
    private void initFlickr() {
        if (flickr == null) {
            String apiKey = FlickrKey.getKey();
            if (apiKey != null) {
                String secret = FlickrKey.getSecret();
                if (secret != null) {
                    flickr = new Flickr(FlickrKey.getKey(), FlickrKey.getSecret(), new REST());
                    AuthInterface auth = flickr.getAuthInterface();
                    Token t = auth.getRequestToken();
                    String authURL = auth.getAuthorizationUrl(t, Permission.WRITE);
                    
                    String verifierString = getVerifierString(authURL);
                    
                    accessToken = auth.getAccessToken(t,  new Verifier(verifierString));
                }
            }
            
            if (accessToken == null) {
                flickr = null;
            }
        }
    }
    
    private String getVerifierString(String authURL) {
        try {
            Desktop.getDesktop().browse(new URI(authURL));
            
            return JOptionPane.showInputDialog(FCBundle.getString(FCBundle.Keys.ENTER_VERIFICATION_CODE));
        } catch (Exception ioe) {
            return null;
        }
    }
}
