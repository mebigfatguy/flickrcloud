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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JList;

import com.mebigfatguy.flickrcloud.FileListModel.PhotoWrapper;

public class FlickrDragSourceListener implements DragSourceListener, DragGestureListener {

    private JList<PhotoWrapper> list;
    private DragSource source;
    DragGestureRecognizer recognizer;

    public FlickrDragSourceListener(JList<PhotoWrapper> l) {
        list = l;
        
        source = DragSource.getDefaultDragSource();
        recognizer = source.createDefaultDragGestureRecognizer(list, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }
    
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        
        Transferable transferable = new Transferable() {

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {DataFlavor.javaFileListFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.isFlavorJavaFileListType();
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                List<PhotoWrapper> photos = list.getSelectedValuesList();
                
                FileGenerator generator = new FileGenerator();
                return generator.generate(photos);
            }
            
        };
        dge.startDrag(null, transferable, this);
    }
}
