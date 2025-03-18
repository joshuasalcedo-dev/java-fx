module io.joshuasalcedo.fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jdk.httpserver;
    requires org.json;
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;

    opens io.joshuasalcedo.fx to javafx.fxml;
    exports io.joshuasalcedo.fx;
}