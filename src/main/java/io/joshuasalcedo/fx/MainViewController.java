package io.joshuasalcedo.fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainViewController {

    @FXML
    private TreeView<String> navigationTree;

    @FXML
    private TabPane contentTabPane;

    @FXML
    private void initialize() {
        // Initialize the navigation tree
        TreeItem<String> rootItem = new TreeItem<>("Modules");
        rootItem.setExpanded(true);

        // Create some sample tree items
        TreeItem<String> dashboardItem = new TreeItem<>("Dashboard");
        TreeItem<String> projectsItem = new TreeItem<>("Projects");
        TreeItem<String> reportsItem = new TreeItem<>("Reports");

        // Add sub-items to Projects
        projectsItem.getChildren().addAll(
                new TreeItem<>("Active Projects"),
                new TreeItem<>("Completed Projects"),
                new TreeItem<>("Archived Projects")
        );

        // Add sub-items to Reports
        reportsItem.getChildren().addAll(
                new TreeItem<>("Weekly Reports"),
                new TreeItem<>("Monthly Reports"),
                new TreeItem<>("Annual Reports")
        );

        // Add main items to the root
        rootItem.getChildren().addAll(dashboardItem, projectsItem, reportsItem);

        // Set the root item in the TreeView
        navigationTree.setRoot(rootItem);

        // Add selection listener to the TreeView
        navigationTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleNavigation(newValue);
            }
        });
    }

    /**
     * Handle navigation when tree items are selected
     */
    private void handleNavigation(TreeItem<String> selectedItem) {
        String itemValue = selectedItem.getValue();

        // Check if a tab with this name already exists
        for (Tab tab : contentTabPane.getTabs()) {
            if (tab.getText().equals(itemValue)) {
                contentTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        // If no existing tab, create a new one
        Tab newTab = new Tab(itemValue);

        // Create content based on selection
        VBox content = new VBox(20);
        content.getStyleClass().addAll("panel", "panel-info");
        content.setPadding(new javafx.geometry.Insets(20));
        
        Label titleLabel = new Label("Content for " + itemValue);
        titleLabel.getStyleClass().addAll("h2");
        
        Label descriptionLabel = new Label("This is a dynamically created tab with BootstrapFX styling");
        descriptionLabel.getStyleClass().addAll("lead");
        
        Button actionButton = new Button("Action");
        actionButton.getStyleClass().addAll("btn", "btn-primary");
        
        content.getChildren().addAll(titleLabel, descriptionLabel, actionButton);

        newTab.setContent(content);
        newTab.setClosable(true);

        // Add and select the new tab
        contentTabPane.getTabs().add(newTab);
        contentTabPane.getSelectionModel().select(newTab);
    }

    // Menu action handlers

    @FXML
    private void handleNewAction() {
        showAlert("New", "Create a new document");
    }

    @FXML
    private void handleOpenAction() {
        showAlert("Open", "Open an existing document");
    }

    @FXML
    private void handleSaveAction() {
        showAlert("Save", "Document saved successfully");
    }

    @FXML
    private void handleExitAction() {
        Platform.exit();
    }

    @FXML
    private void handleAboutAction() {
        showAlert("About", "Simple JavaFX Application\nVersion 1.0");
    }

    /**
     * Helper method to show styled alerts using BootstrapFX
     */
    private void showAlert(String title, String message) {
        // Create an alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Get the dialog pane and apply bootstrap styles
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("panel-info");
        
        // Apply styles to the buttons
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().addAll("btn", "btn-primary");
        
        alert.showAndWait();
    }
}