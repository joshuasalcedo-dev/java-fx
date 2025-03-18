package io.joshuasalcedo.fx.utils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A JavaFX-based clipboard listener that polls the clipboard for changes
 */
public class ClipBoardListener extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ClipBoardListener.class.getName());
    private static final long POLL_INTERVAL_MS = 250; // Poll every 250ms
    private static final int MAX_HISTORY_SIZE = 20;

    private final AtomicReference<String> lastContent = new AtomicReference<>("");
    private volatile boolean running = true;

    // Observable lists for the UI
    private final ObservableList<ClipboardItem> historyItems = FXCollections.observableArrayList();
    private final ObservableList<ClipboardItem> pinnedItems = FXCollections.observableArrayList();

    public ClipBoardListener() {
        setDaemon(true); // Make this a daemon thread so it doesn't prevent JVM shutdown
        setName("ClipboardMonitorThread");
    }

    @Override
    public void run() {
        // Initial clipboard content
        try {
            Platform.runLater(() -> {
                try {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    if (clipboard.hasString()) {
                        String initialContent = clipboard.getString();
                        if (initialContent != null && !initialContent.isEmpty()) {
                            lastContent.set(initialContent);
                            addToHistory(initialContent);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error reading initial clipboard content", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in initial clipboard access", e);
        }

        // Monitor loop
        while (running) {
            try {
                // Sleep to avoid high CPU usage
                Thread.sleep(POLL_INTERVAL_MS);

                // Check clipboard on JavaFX thread
                Platform.runLater(() -> {
                    try {
                        checkClipboard();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error checking clipboard", e);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.INFO, "Clipboard listener interrupted", e);
                break;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in clipboard listener thread", e);
            }
        }
    }

    private void checkClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            String content = clipboard.getString();

            if (content != null && !content.isEmpty() && !content.equals(lastContent.get())) {
                // Content has changed
                processClipboardContent(content);
                lastContent.set(content);
            }
        }
    }

    private void processClipboardContent(String content) {
        // Add to history
        addToHistory(content);

        // Log the content (for debugging)
        System.out.println("Clipboard content: " + content);
    }

    private void addToHistory(String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        // Check if this content is already in history
        for (ClipboardItem item : historyItems) {
            if (item.getContent().equals(content)) {
                // Move to top of history
                historyItems.remove(item);
                historyItems.add(0, item);
                return;
            }
        }

        // Add new history item
        ClipboardItem newItem = new ClipboardItem(content);
        historyItems.add(0, newItem);

        // Trim history if needed
        if (historyItems.size() > MAX_HISTORY_SIZE) {
            historyItems.remove(historyItems.size() - 1);
        }
    }

    /**
     * Pins an item to the pinned list
     */
    public void pinItem(String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        // Check if already pinned
        for (ClipboardItem item : pinnedItems) {
            if (item.getContent().equals(content)) {
                return; // Already pinned
            }
        }

        // Add to pinned items
        ClipboardItem pinnedItem = new ClipboardItem(content);
        pinnedItem.setPinned(true);
        pinnedItems.add(pinnedItem);
    }

    /**
     * Unpins an item from the pinned list
     */
    public void unpinItem(String content) {
        pinnedItems.removeIf(item -> item.getContent().equals(content));
    }

    /**
     * Copies content to the system clipboard
     */
    public void copyToClipboard(String content) {
        try {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(content);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error copying to clipboard", e);
        }
    }

    // Getters for the observable lists
    public ObservableList<ClipboardItem> getHistoryItems() {
        return historyItems;
    }

    public ObservableList<ClipboardItem> getPinnedItems() {
        return pinnedItems;
    }

    /**
     * Stops the clipboard listener thread
     */
    public void stopListening() {
        running = false;
        this.interrupt();
    }
}