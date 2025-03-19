package io.joshuasalcedo.fx;


import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.MonthView;
import io.joshuasalcedo.fx.utils.ClipBoardListener;
import io.joshuasalcedo.fx.utils.ClipboardHistoryView;
import io.joshuasalcedo.fx.utils.ClockWidget;
import io.joshuasalcedo.fx.utils.MarkdownRenderer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class MainViewController {

    @FXML
    private TreeView<String> navigationTree;

    @FXML
    private TabPane contentTabPane;

    @FXML
    private VBox rightPanel;

    private ClipBoardListener clipboardListener;
    
    private Thread calendarUpdateThread;

    @FXML
    private void initialize() {
        // Get the clipboard listener instance from MainApplication
        this.clipboardListener = new ClipBoardListener();
        this.clipboardListener.start();

        // Add widgets to the right panel
        rightPanel.getChildren().clear();
        
        // Add clipboard history view with Bootstrap styling
        ClipboardHistoryView historyView = new ClipboardHistoryView(clipboardListener);
        historyView.getStyleClass().addAll("panel", "panel-success");
        
        // Add widgets to the right panel
        rightPanel.getChildren().add(historyView);
        
        // Initialize the navigation tree
        TreeItem<String> rootItem = new TreeItem<>("Modules");
        rootItem.setExpanded(true);

        // Create some sample tree items
        TreeItem<String> dashboardItem = new TreeItem<>("Dashboard");
        TreeItem<String> projectsItem = new TreeItem<>("Projects");
        TreeItem<String> reportsItem = new TreeItem<>("Reports");
        
        // Create calendar tree item
        TreeItem<String> calendarItem = new TreeItem<>("Calendar");
        
        // Create clock tree item
        TreeItem<String> clockItem = new TreeItem<>("World Clock");
        
        // Create docs tree item
        TreeItem<String> docsItem = new TreeItem<>("Documentation");
        
        // Add sub-items to docs from the docs directory
        docsItem.getChildren().addAll(
                new TreeItem<>("Directory View"),
                new TreeItem<>("React Integration")
        );
        
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
        rootItem.getChildren().addAll(dashboardItem, projectsItem, reportsItem, calendarItem, clockItem, docsItem);

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
        newTab.setClosable(true);
        
        // Handle different sections
        if (selectedItem.getParent() != null && selectedItem.getParent().getValue().equals("Documentation")) {
            // Handle documentation items with WebView
            createDocumentationTab(newTab, itemValue);
        } else if (itemValue.equals("Calendar")) {
            // Create calendar content
            createCalendarTab(newTab);
        } else if (itemValue.equals("World Clock")) {
            // Create world clock content
            createClockTab(newTab);
        } else {
            // Default content for other selections
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
        }

        // Add and select the new tab
        contentTabPane.getTabs().add(newTab);
        contentTabPane.getSelectionModel().select(newTab);
    }
    
    /**
     * Creates a calendar tab with full CalendarFX functionality
     */
    private void createCalendarTab(Tab tab) {
        // Create calendar view with time zone support
        CalendarView calendarView = new CalendarView();
        calendarView.setEnableTimeZoneSupport(true);
        
        // Create sample calendars
        Calendar workCalendar = new Calendar("Work");
        Calendar personalCalendar = new Calendar("Personal");
        Calendar familyCalendar = new Calendar("Family");
        Calendar holidaysCalendar = new Calendar("Holidays");
        
        // Set short names for calendars
        workCalendar.setShortName("W");
        personalCalendar.setShortName("P");
        familyCalendar.setShortName("F");
        holidaysCalendar.setShortName("H");
        
        // Set styles for calendars
        workCalendar.setStyle(Style.STYLE1);
        personalCalendar.setStyle(Style.STYLE2);
        familyCalendar.setStyle(Style.STYLE3);
        holidaysCalendar.setStyle(Style.STYLE5);
        
        // Create calendar sources and group calendars
        CalendarSource userCalendarSource = new CalendarSource("My Calendars");
        userCalendarSource.getCalendars().addAll(workCalendar, personalCalendar, familyCalendar);
        
        CalendarSource otherCalendarSource = new CalendarSource("Other Calendars");
        otherCalendarSource.getCalendars().add(holidaysCalendar);
        
        // Add calendar sources to the calendar view
        calendarView.getCalendarSources().setAll(userCalendarSource, otherCalendarSource);
        
        // Set to current time
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
        
        // Create thread for updating the time
        stopCalendarUpdateThreadIfRunning();
        
        calendarUpdateThread = new Thread("Calendar Update Thread") {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });
                    
                    try {
                        // Update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        };
        
        calendarUpdateThread.setPriority(Thread.MIN_PRIORITY);
        calendarUpdateThread.setDaemon(true);
        calendarUpdateThread.start();
        
        // Set the calendar view as the tab content
        tab.setContent(calendarView);
        
        // Stop the thread when tab is closed
        tab.setOnClosed(event -> stopCalendarUpdateThreadIfRunning());
    }
    
    private void stopCalendarUpdateThreadIfRunning() {
        if (calendarUpdateThread != null && calendarUpdateThread.isAlive()) {
            calendarUpdateThread.interrupt();
            calendarUpdateThread = null;
        }
    }
    
    /**
     * Creates a tab with multiple world clocks
     */
    private void createClockTab(Tab tab) {
        // Create a VBox to organize content
        VBox contentBox = new VBox(20);
        contentBox.getStyleClass().add("panel-body");
        contentBox.setPadding(new javafx.geometry.Insets(20));
        
        // Create title
        Label titleLabel = new Label("World Clocks");
        titleLabel.getStyleClass().addAll("h2");
        
        // Create description
        Label descriptionLabel = new Label("Current time across different time zones");
        descriptionLabel.getStyleClass().addAll("lead");
        
        // Create a VBox to hold multiple clocks
        VBox clocksContainer = new VBox(15);
        clocksContainer.setPadding(new javafx.geometry.Insets(10));
        clocksContainer.getStyleClass().addAll("panel", "panel-info");
        
        // Create clock widgets for different time zones
        ClockWidget manilaClockWidget = new ClockWidget();
        
        // Create an explanation
        Label explanationLabel = new Label("The clock above shows the current time in Manila, Philippines using the Logwork clock widget.");
        explanationLabel.setWrapText(true);
        explanationLabel.getStyleClass().add("text-muted");
        
        // Add clocks to the container
        clocksContainer.getChildren().addAll(manilaClockWidget, explanationLabel);
        
        // Add components to the content box
        contentBox.getChildren().addAll(titleLabel, descriptionLabel, clocksContainer);
        
        // Set the content of the tab
        tab.setContent(contentBox);
    }
    
    /**
     * Creates a documentation tab with WebView to render markdown content
     */
    private void createDocumentationTab(Tab tab, String title) {
        // Load markdown content based on the selected doc
        String fileName = "";
        
        if (title.equals("Directory View")) {
            fileName = "directory-view.md";
        } else if (title.equals("React Integration")) {
            fileName = "react-integration.md";
        }
        
        try {
            // Load the markdown content using our utility class
            String markdownContent = MarkdownRenderer.loadMarkdownFromResources("/io/joshuasalcedo/fx/docs/" + fileName);
            
            // Render markdown content using our utility
            WebView markdownView = MarkdownRenderer.renderMarkdownWebView(markdownContent);
            
            // Set the WebView as the tab content
            tab.setContent(markdownView);
            
        } catch (IOException e) {
            // If there's an error, show it in the tab
            Label errorLabel = new Label("Error loading documentation: " + e.getMessage());
            errorLabel.getStyleClass().add("error-label");
            tab.setContent(errorLabel);
        }
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
        // Clean up resources
        if (clipboardListener != null) {
            clipboardListener.stopListening();
        }
        stopCalendarUpdateThreadIfRunning();
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