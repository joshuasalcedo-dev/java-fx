package io.joshuasalcedo.fx.example;



import com.calendarfx.view.MonthView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MonthViewExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        MonthView monthView = new MonthView();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(monthView); // introPane);

        Scene scene = new Scene(stackPane);
        primaryStage.setTitle("Month View");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}