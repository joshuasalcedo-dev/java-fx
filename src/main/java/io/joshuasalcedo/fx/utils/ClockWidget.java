package io.joshuasalcedo.fx.utils;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

/**
 * A clock widget that embeds a Logwork text clock using WebView
 */
public class ClockWidget extends StackPane {
    
    private static final String CLOCK_HTML = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Logwork Clock</title>\n" +
            "    <style>\n" +
            "        body { margin: 0; padding: 20px; background: transparent; font-family: Arial, sans-serif; }\n" +
            "        .clock-container { text-align: center; }\n" +
            "        .clock-widget-text { font-size: 18px; text-decoration: none; }\n" +
            "    </style>\n" +
            "    <script src=\"https://cdn.logwork.com/widget/text.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"clock-container\">\n" +
            "        <a href=\"https://logwork.com/current-time-in-manila-philippines\" class=\"clock-widget-text\" data-timezone=\"Asia/Manila\" data-language=\"en\" data-textcolor=\"#000000\">Manila, Philippines</a>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    
    private WebView webView;
    
    /**
     * Constructs a new ClockWidget
     */
    public ClockWidget() {
        webView = new WebView();
        webView.setPrefSize(300, 100);
        
        // Disable context menu and other browser features
        webView.setContextMenuEnabled(false);
        
        // Load the HTML content with the clock widget
        webView.getEngine().loadContent(CLOCK_HTML);
        
        // Allow scripts to execute
        webView.getEngine().setJavaScriptEnabled(true);
        
        // Add the WebView to this StackPane
        getChildren().add(webView);
    }
}