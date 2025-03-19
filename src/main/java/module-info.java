module io.joshuasalcedo.fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.base;

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
    requires com.calendarfx.view;
    requires fr.brouillard.oss.cssfx;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;

    opens io.joshuasalcedo.fx to javafx.fxml;
    exports io.joshuasalcedo.fx;

    exports io.joshuasalcedo.fx.utils;
}