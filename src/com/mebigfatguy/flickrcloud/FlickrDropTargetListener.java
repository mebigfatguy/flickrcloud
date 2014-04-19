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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;


public class FlickrDropTargetListener implements DropTargetListener {

    private FCDropTarget target;
    private FlickrPublisher publisher;
    
    public FlickrDropTargetListener(FCDropTarget component) {
        target = component;
        publisher = new FlickrPublisher();
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        target.setHilite();
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        target.setNormal();
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                PngGenerator generator = new PngGenerator();
                @SuppressWarnings("unchecked")
                Map<String, File> images = generator.generate((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
                publisher.publish(images);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(target.getDropOwner(), e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            target.setNormal();
        }
    }
}
