package io.joshuasalcedo.fx.utils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * A compact UI component that displays clipboard history
 */
public class ClipboardHistoryView extends BorderPane {

    private final ClipBoardListener clipboardListener;
    private final ListView<ClipboardItem> historyListView;

    public ClipboardHistoryView(ClipBoardListener clipboardListener) {
        this.clipboardListener = clipboardListener;
        this.getStyleClass().addAll("panel", "panel-success");

        // Create header
        Label titleLabel = new Label("Clipboard History");
        titleLabel.getStyleClass().addAll("panel-title", "h4");
        
        HBox headerBox = new HBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(8, 10, 8, 10));
        headerBox.getChildren().add(titleLabel);
        
        // Create history list view with compact styling
        historyListView = new ListView<>(clipboardListener.getHistoryItems());
        historyListView.setPrefHeight(300);
        setupListView(historyListView);
        
        // Main container
        VBox mainBox = new VBox(0);
        VBox.setVgrow(historyListView, Priority.ALWAYS);
        mainBox.getChildren().add(historyListView);

        // Add components to the border pane
        this.setTop(headerBox);
        this.setCenter(mainBox);
        this.setPadding(new Insets(0));
    }

    private void setupListView(ListView<ClipboardItem> listView) {
        // Set cell factory for compact display
        listView.setCellFactory(new Callback<ListView<ClipboardItem>, ListCell<ClipboardItem>>() {
            @Override
            public ListCell<ClipboardItem> call(ListView<ClipboardItem> param) {
                return new CompactClipboardItemCell();
            }
        });

        // Add click handlers
        listView.setOnMouseClicked(event -> {
            if (!listView.getSelectionModel().isEmpty()) {
                ClipboardItem selectedItem = listView.getSelectionModel().getSelectedItem();
                
                // Left click - copy to clipboard
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    clipboardListener.copyToClipboard(selectedItem.getContent());
                }
                
                // Right click - show context menu
                else if (event.getButton() == MouseButton.SECONDARY) {
                    // Will implement pin feature later
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem pinItem = new MenuItem("Pin (coming soon)");
                    contextMenu.getItems().add(pinItem);
                    
                    listView.setContextMenu(contextMenu);
                }
            }
        });
    }

    /**
     * Compact cell for displaying clipboard items
     */
    private static class CompactClipboardItemCell extends ListCell<ClipboardItem> {
        
        public CompactClipboardItemCell() {
            // Style the cell to be compact
            this.setPadding(new Insets(2, 8, 2, 8));
            this.setAlignment(Pos.CENTER_LEFT);
        }

        @Override
        protected void updateItem(ClipboardItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setTooltip(null);
                return;
            }

            // Truncate text for display
            String content = item.getContent();
            String displayText = content;
            
            // Truncate long text
            if (content.length() > 40) {
                displayText = content.substring(0, 37) + "...";
            }
            
            // Remove newlines for cleaner display
            displayText = displayText.replace("\n", " ").replace("\r", "");
            
            // Set the cell text
            setText(displayText);
            
            // Add tooltip with full content
            Tooltip tooltip = new Tooltip(content);
            tooltip.setMaxWidth(400);
            tooltip.setWrapText(true);
            setTooltip(tooltip);
            
            // Style based on selection
            updateStyles();
        }
        
        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
            updateStyles();
        }
        
        private void updateStyles() {
            if (isSelected()) {
                getStyleClass().add("list-cell-selected");
            } else {
                getStyleClass().removeAll("list-cell-selected");
            }
        }
    }
}