package io.joshuasalcedo.fx.utils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A utility class for rendering Markdown content in JavaFX
 */
public class MarkdownRenderer {

    /**
     * Renders markdown content in a WebView using CSS for styling
     * @param markdown The markdown content to render
     * @return WebView containing the rendered markdown
     */
    public static WebView renderMarkdownWebView(String markdown) {
        WebView webView = new WebView();
        
        // Convert markdown to styled HTML
        String html = convertMarkdownToStyledHtml(markdown);
        
        // Load the HTML content
        webView.getEngine().loadContent(html);
        
        return webView;
    }
    
    /**
     * Loads markdown content from a resource file
     * @param resourcePath The path to the markdown resource
     * @return The markdown content as a string
     * @throws IOException If the resource cannot be read
     */
    public static String loadMarkdownFromResources(String resourcePath) throws IOException {
        try (InputStream inputStream = MarkdownRenderer.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Converts markdown to styled HTML with CSS for better rendering in WebView
     * @param markdown The markdown content
     * @return HTML with CSS styling
     */
    private static String convertMarkdownToStyledHtml(String markdown) {
        StringBuilder html = new StringBuilder();
        
        // Add HTML structure and CSS styling
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; ");
        html.append("       line-height: 1.6; padding: 20px; max-width: 800px; margin: 0 auto; color: #333; }");
        html.append("h1 { font-size: 2em; margin-top: 1.5em; margin-bottom: 0.5em; color: #2c3e50; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }");
        html.append("h2 { font-size: 1.5em; margin-top: 1.5em; margin-bottom: 0.5em; color: #2c3e50; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }");
        html.append("h3 { font-size: 1.25em; margin-top: 1.5em; margin-bottom: 0.5em; color: #2c3e50; }");
        html.append("p { margin-top: 0; margin-bottom: 16px; }");
        html.append("code { font-family: SFMono-Regular, Consolas, 'Liberation Mono', Menlo, monospace; ");
        html.append("       background-color: rgba(27, 31, 35, 0.05); padding: 0.2em 0.4em; border-radius: 3px; font-size: 85%; }");
        html.append("pre { background-color: #f6f8fa; border-radius: 3px; padding: 16px; overflow: auto; line-height: 1.45; }");
        html.append("pre code { background-color: transparent; padding: 0; font-size: 100%; }");
        html.append("ul, ol { padding-left: 2em; margin-bottom: 16px; }");
        html.append("li { margin-bottom: 0.25em; }");
        html.append("a { color: #0366d6; text-decoration: none; }");
        html.append("a:hover { text-decoration: underline; }");
        html.append("blockquote { margin: 0; padding: 0 1em; color: #6a737d; border-left: 0.25em solid #dfe2e5; }");
        html.append("hr { height: 0.25em; padding: 0; margin: 24px 0; background-color: #e1e4e8; border: 0; }");
        html.append("</style>");
        html.append("</head><body>");
        
        // Process the markdown content
        String processedContent = markdown;
        
        // Headers - needs to be processed first for multi-line patterns
        processedContent = processedContent.replaceAll("(?m)^# (.*?)$", "<h1>$1</h1>");
        processedContent = processedContent.replaceAll("(?m)^## (.*?)$", "<h2>$1</h2>");
        processedContent = processedContent.replaceAll("(?m)^### (.*?)$", "<h3>$1</h3>");
        
        // Code blocks with language specification
        Pattern codeBlockPattern = Pattern.compile("(?ms)```(\\w*)\\s*\\n(.*?)```");
        Matcher codeBlockMatcher = codeBlockPattern.matcher(processedContent);
        StringBuffer sb = new StringBuffer();
        
        while (codeBlockMatcher.find()) {
            String language = codeBlockMatcher.group(1);
            String code = codeBlockMatcher.group(2);
            
            // Escape HTML in code blocks
            code = code.replace("&", "&amp;")
                      .replace("<", "&lt;")
                      .replace(">", "&gt;");
            
            String replacement = "<pre><code class=\"language-" + language + "\">" + code + "</code></pre>";
            codeBlockMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        codeBlockMatcher.appendTail(sb);
        processedContent = sb.toString();
        
        // Inline code
        processedContent = processedContent.replaceAll("`([^`]*?)`", "<code>$1</code>");
        
        // Bold and italic
        processedContent = processedContent.replaceAll("\\*\\*([^*]*?)\\*\\*", "<strong>$1</strong>");
        processedContent = processedContent.replaceAll("\\*([^*]*?)\\*", "<em>$1</em>");
        
        // Lists - needs special handling for nested lists
        // This is a simplified approach that doesn't handle nesting
        StringBuilder listProcessed = new StringBuilder();
        boolean inOrderedList = false;
        boolean inUnorderedList = false;
        
        for (String line : processedContent.split("\n")) {
            if (line.matches("^\\d+\\. .*")) {
                if (!inOrderedList) {
                    if (inUnorderedList) {
                        listProcessed.append("</ul>\n");
                        inUnorderedList = false;
                    }
                    listProcessed.append("<ol>\n");
                    inOrderedList = true;
                }
                listProcessed.append("<li>").append(line.replaceAll("^\\d+\\. (.*?)$", "$1")).append("</li>\n");
            } else if (line.matches("^- .*")) {
                if (!inUnorderedList) {
                    if (inOrderedList) {
                        listProcessed.append("</ol>\n");
                        inOrderedList = false;
                    }
                    listProcessed.append("<ul>\n");
                    inUnorderedList = true;
                }
                listProcessed.append("<li>").append(line.replaceAll("^- (.*?)$", "$1")).append("</li>\n");
            } else {
                if (inOrderedList) {
                    listProcessed.append("</ol>\n");
                    inOrderedList = false;
                }
                if (inUnorderedList) {
                    listProcessed.append("</ul>\n");
                    inUnorderedList = false;
                }
                listProcessed.append(line).append("\n");
            }
        }
        
        if (inOrderedList) {
            listProcessed.append("</ol>\n");
        }
        if (inUnorderedList) {
            listProcessed.append("</ul>\n");
        }
        
        processedContent = listProcessed.toString();
        
        // Links
        processedContent = processedContent.replaceAll("\\[([^\\]]*?)\\]\\(([^\\)]*?)\\)", "<a href=\"$2\">$1</a>");
        
        // Paragraphs - needs to be last to avoid interfering with other elements
        // Skip lines that already have HTML tags
        processedContent = processedContent.replaceAll("(?m)^(?!\\s*<[hpo])(.+?)$", "<p>$1</p>");
        
        html.append(processedContent);
        html.append("</body></html>");
        
        return html.toString();
    }
}