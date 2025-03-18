package io.joshuasalcedo.fx.utils;


import io.joshuasalcedo.fx.utils.ClipBoardListener;
import io.joshuasalcedo.fx.utils.ClipboardItem;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * UI component that displays clipboard history and pinned items
 */
public class ClipboardHistoryView extends BorderPane {

    private final ClipBoardListener clipboardListener;
    private final ListView<ClipboardItem> historyListView;
    private final ListView<ClipboardItem> pinnedListView;

    public ClipboardHistoryView(ClipBoardListener clipboardListener) {
        this.clipboardListener = clipboardListener;
        this.getStyleClass().addAll("panel", "panel-success");

        // Create tab pane for history and pinned items
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create history list view
        historyListView = new ListView<>(clipboardListener.getHistoryItems());
        setupListView(historyListView, true);

        // Create pinned list view
        pinnedListView = new ListView<>(clipboardListener.getPinnedItems());
        setupListView(pinnedListView, false);

        // Create tabs
        Tab historyTab = new Tab("History");
        VBox historyBox = new VBox(5);
        historyBox.setPadding(new Insets(10));
        historyBox.getChildren().add(historyListView);
        historyTab.setContent(historyBox);

        Tab pinnedTab = new Tab("Pinned");
        VBox pinnedBox = new VBox(5);
        pinnedBox.setPadding(new Insets(10));
        pinnedBox.getChildren().add(pinnedListView);
        pinnedTab.setContent(pinnedBox);

        // Add tabs to tab pane
        tabPane.getTabs().addAll(historyTab, pinnedTab);

        // Set up header
        Label titleLabel = new Label("Clipboard History");
        titleLabel.getStyleClass().addAll("panel-title", "h4");

        // Add components to the border pane
        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(10));
        headerBox.getChildren().add(titleLabel);
        this.setTop(headerBox);
        this.setCenter(tabPane);
    }

    private void setupListView(ListView<ClipboardItem> listView, boolean isHistory) {
        // Set cell factory to customize display
        listView.setCellFactory(new Callback<ListView<ClipboardItem>, ListCell<ClipboardItem>>() {
            @Override
            public ListCell<ClipboardItem> call(ListView<ClipboardItem> param) {
                return new ClipboardItemCell(isHistory);
            }
        });

        // Add double-click handler to copy item to clipboard
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !listView.getSelectionModel().isEmpty()) {
                ClipboardItem selectedItem = listView.getSelectionModel().getSelectedItem();
                clipboardListener.copyToClipboard(selectedItem.getContent());
            }
        });
    }

    /**
     * Custom ListCell for displaying clipboard items
     */
    private class ClipboardItemCell extends ListCell<ClipboardItem> {
        private final boolean isHistory;

        public ClipboardItemCell(boolean isHistory) {
            this.isHistory = isHistory;
        }

        @Override
        protected void updateItem(ClipboardItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            // Create content display
            VBox content = new VBox(2);
            content.setPadding(new Insets(5));

            // Truncate long text
            String displayText = item.getContent();
            if (displayText.length() > 50) {
                displayText = displayText.substring(0, 47) + "...";
            }

            Label textLabel = new Label(displayText);
            content.getChildren().add(textLabel);

            // Create buttons
            HBox buttonsBox = new HBox(5);

            Button copyButton = new Button("Copy");
            copyButton.getStyleClass().addAll("btn", "btn-primary", "btn-xs");
            copyButton.setOnAction(e -> {
                clipboardListener.copyToClipboard(item.getContent());
                e.consume();
            });

            buttonsBox.getChildren().add(copyButton);

            if (isHistory) {
                Button pinButton = new Button("Pin");
                pinButton.getStyleClass().addAll("btn", "btn-info", "btn-xs");
                pinButton.setOnAction(e -> {
                    clipboardListener.pinItem(item.getContent());
                    e.consume();
                });
                buttonsBox.getChildren().add(pinButton);
            } else {
                Button unpinButton = new Button("Unpin");
                unpinButton.getStyleClass().addAll("btn", "btn-warning", "btn-xs");
                unpinButton.setOnAction(e -> {
                    clipboardListener.unpinItem(item.getContent());
                    e.consume();
                });
                buttonsBox.getChildren().add(unpinButton);
            }

            content.getChildren().add(buttonsBox);
            setGraphic(content);
        }
    }
}
