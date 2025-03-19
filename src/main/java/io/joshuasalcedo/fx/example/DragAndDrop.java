package io.joshuasalcedo.fx.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class DragAndDrop extends Application {

    public static void main (String[] args) {
        launch(args);
    }

    public void start (Stage primaryStage) {

        Circle circle = createCircle("#ff00ff", "#ff88ff", 100);

        circle.setOnDragDetected((MouseEvent event) -> {
            System.out.println("Circle 1 drag detected");

            Dragboard db = circle.startDragAndDrop(TransferMode.MOVE); // Be more specific about transfer mode

            ClipboardContent content = new ClipboardContent();
            content.putString("Circle source text");
            db.setContent(content);

            event.consume(); // Add event consumption
        });

        // Add drag done event handler
        circle.setOnDragDone((DragEvent event) -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                System.out.println("Drag completed");
            }
            event.consume();
        });

        Circle circle2 = createCircle("#00ffff", "#88ffff", 300);

        circle2.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != circle2 && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        circle2.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != circle2 && event.getDragboard().hasString()) {
                circle2.setStroke(Color.GREEN);
            }
            event.consume();
        });

        circle2.setOnDragExited((DragEvent event) -> {
            circle2.setStroke(Color.valueOf("#00ffff"));
            event.consume();
        });

        circle2.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                System.out.println("Dropped: " + db.getString());
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        Pane pane = new Pane();
        pane.getChildren().addAll(circle, circle2);

        Scene scene = new Scene(pane, 1024, 800, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Drag and Drop Example");

        primaryStage.show();
    }

    private Circle createCircle (String strokeColor, String fillColor, double x) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(200);
        circle.setRadius(50);
        circle.setStroke(Color.valueOf(strokeColor));
        circle.setStrokeWidth(5);
        circle.setFill(Color.valueOf(fillColor));
        return circle;
    }
}