package io.joshuasalcedo.fx.utils;


import java.time.LocalDateTime;

/**
 * Represents a clipboard content item
 */
public class ClipboardItem {
    private String content;
    private LocalDateTime timestamp;
    private boolean pinned;

    public ClipboardItem(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.pinned = false;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @Override
    public String toString() {
        // For display in ListView
        return content;
    }
}