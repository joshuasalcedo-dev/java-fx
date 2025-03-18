package io.joshuasalcedo.fx.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClipBoardListener extends Thread implements ClipboardOwner {
    private static final Logger LOGGER = Logger.getLogger(ClipBoardListener.class.getName());
    private final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    @Override
    public void run() {
        Transferable trans = sysClip.getContents(this);
        takeOwnership(trans);
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        try {
            // Give the system some time to complete the clipboard operation
            ClipBoardListener.sleep(250);
        } catch(InterruptedException e) {
            LOGGER.log(Level.WARNING, "Clipboard listener interrupted", e);
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
        
        try {
            Transferable contents = sysClip.getContents(this);
            processClipboard(contents, c);
            takeOwnership(contents);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing clipboard", ex);
        }
    }

    private void takeOwnership(Transferable t) {
        sysClip.setContents(t, this);
    }

    public void processClipboard(Transferable t, Clipboard c) {
        if (t == null) {
            LOGGER.info("Clipboard content is null");
            return;
        }
        
        System.out.println(c.getName());

        // Try to get string content
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                System.out.println(text);
                // Process your clipboard text here
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not read string from clipboard", e);
            }
            return;
        }
        
        // Handle other data types if needed
        try {
            // Try to handle other data flavors that might be present
            DataFlavor[] flavors = t.getTransferDataFlavors();
            if (flavors != null && flavors.length > 0) {
                for (DataFlavor flavor : flavors) {
                    if (flavor.isRepresentationClassInputStream()) {
                        try {
                            InputStream is = (InputStream) t.getTransferData(flavor);
                            // Create a buffered copy to avoid mark/reset issues
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) > -1) {
                                baos.write(buffer, 0, len);
                            }
                            baos.flush();
                            
                            // Now we have a byte array that can be reset
                            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                            // Process your input stream here
                            
                            LOGGER.info("Processed clipboard input stream data");
                            break; // Successfully processed
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Could not read data from flavor: " + flavor, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not process clipboard data", e);
        }
    }
}