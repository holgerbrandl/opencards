package info.opencards.md;

import com.sun.javafx.application.PlatformImpl;
import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Nice general oveview about how to integrate javafx into swing app
 * http://what-when-how.com/javafx-2/embedding-javafx-scenes-in-swing-and-swt-applications-collections-and-concurrency/
 * <p>
 * Automatix rescaling?
 * http://java-no-makanaikata.blogspot.de/2012/10/javafx-webview-size-trick.html
 */
public class MdSlideManager extends AbstractSlideManager {
    private WebEngine webEngine;
    private JFXPanel jfxPanel;


    public List<MarkdownFlashcard> slides;


    @Override
    public boolean showCardQuestion(Item item) {
        // todo implemnt optional sync
        if (isItemOutOfSync(item)) return false;

        int cardIndex = item.getFlashCard().getCardIndex();
        ReversePolicy cardRevPolicy = item.getFlashCard().getTodaysRevPolicy();

        // note:
        // The random-reverse mode is encoded directly in the flashcard in order to make its policy temporary
        // persistent during the todaysltm-sessions
        MarkdownFlashcard curSlide = slides.get(cardIndex - 1);

        switch (cardRevPolicy) {
            case NORMAL:
                renderHtml(curSlide.getQuestion());
                break;
            case REVERSE:
                renderHtml(curSlide.getAnswer());
                break;
            default:
                throw new RuntimeException("unsupported reverse policy");
        }

        return true;
    }


    /**
     * Test if item is in sync with given slide-show instance
     */
    private boolean isItemOutOfSync(Item item) {

        final int itemCardIndex = item.getFlashCard().getCardIndex();

        return itemCardIndex < 0 || slides.size() < itemCardIndex - 1 || slides.isEmpty() || slides.get(itemCardIndex - 1).getQuestion().hashCode() != item.getFlashCard().getCardID();
    }


    private void renderHtml(String content) {
        String cssStyle = " ";

        // rescale too wide images to fit window
        cssStyle += MarkdownCssKt.getDownscaleImg();
        cssStyle += MarkdownCssKt.getBlockQuote();

//        String cssStyle = "  <style type='text/css'>\n" +
//                "            p {size: 33em;}\n" +
//                "            h1 {size: 33em;}\n" +
//                "            h2 {size: 33em;}\n" +
//                "            h3 {size: 33em;}\n" +
//                "        </style>";

        // http://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        String finalCssStyle = cssStyle;

        Platform.runLater(() -> {
            webEngine.loadContent("<body>" + finalCssStyle + content + "</body≈>");
                    redirectLinksToBrowser(webEngine);
                }
        );
    }


    // http://www.java2s.com/Code/Java/JavaFX/WebEngineLoadListener.htm
    // http://stackoverflow.com/questions/15555510/javafx-stop-opening-url-in-webview-open-in-browser-instead
    private static void redirectLinksToBrowser(WebEngine webEngine) {
        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // adjust link handling
                    if (webEngine.getDocument() == null)
                        return;

                    NodeList nodeList = webEngine.getDocument().getElementsByTagName("a");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        EventTarget eventTarget = (EventTarget) node;

                        eventTarget.addEventListener("click", evt -> {
                            EventTarget target = evt.getCurrentTarget();
                            HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                            String href = anchorElement.getHref();

                            //handle opening URL outside JavaFX WebView
                            try {
                                Desktop.getDesktop().browse(new URI(href));
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                            evt.preventDefault();
                        }, false);
                    }
                });
    }


    @Override
    public boolean showCompleteCard(Item item) {
        MarkdownFlashcard curSlide = slides.get(item.getFlashCard().getCardIndex() - 1);
        renderHtml(curSlide.getQuestion() + "\n" + curSlide.getAnswer());

        return true;
    }


    @Override
    public void startFileSession(ItemCollection cardItemCollection) {

    }


    @Override
    public void openCardFile(CardFile cardFile) {
        cardFile.synchronize();

        slides = MarkdownParserKt.parseMD(cardFile.getFileLocation(), cardFile.getProperties().useMarkdownSelector());


        jfxPanel = new JFXPanel();
        jfxPanel.setBackground(Color.BLACK);
        createScene();

        JPanel renderContainer = OpenCards.getInstance().getLearnPanel().getSlideRenderPanel();
        renderContainer.removeAll();
//        renderContainer.setLayout(new BorderLayout());
//        renderContainer.add(jfxPanel, BorderLayout.CENTER);
        renderContainer.add(jfxPanel);

//        renderContainer.setBackground(Color.BLACK);
        renderContainer.validate();
        renderContainer.repaint();

        renderContainer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                jfxPanel.setPreferredSize(renderContainer.getPreferredSize());
                jfxPanel.repaint();
            }


            @Override
            public void componentShown(ComponentEvent e) {
                jfxPanel.repaint();
            }
        });

        curCardFile = cardFile;

        OpenCards.getInstance().setTitle("OpenCards: " + cardFile.getFileLocation().getName());
    }


    @Override
    public void stopFileSession() {
//        Utils.log("stopped md-file session");
        slides = null;
    }


    class Browser extends StackPane {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();


        public Browser() {
            webEngine.load("www.oracle.com");
            getChildren().add(browser);
        }
    }

    private void createScene() {
        PlatformImpl.startup(() -> {
            Browser browser = new Browser();

            Scene scene = new Scene(browser, 80, 20);
//            scene.getStylesheets().add("equal_sizes.css");

            Stage stage = new Stage();

            stage.setTitle("Hello Java FX");
            stage.setResizable(true);
            stage.setScene(scene);

            webEngine = browser.webEngine;


            // Set up the embedded browser:
//            webEngine = browser.webEngine;
//                webEngine.load("http://heise.de");

            // https://stackoverflow.com/questions/38432698/webview-size-in-javafx-stage
            browser.prefHeightProperty().bind(stage.heightProperty());
            browser.prefWidthProperty().bind(stage.widthProperty());


//                ScrollPane scrollPane = new ScrollPane();
//                scrollPane.setContent(browser);
            webEngine.loadContent("<b>asdf</b>");
//            webEngine.setUserStyleSheetLocation("equal_sizes.css");


//                root.getChildren().addAll(scrollPane);
//                scene.setRoot(root);
//                stage.setScene(scene);


//            root.getChildren().add(browser);
//                children.add(browser);

            jfxPanel.setScene(scene);
        });

        while (webEngine == null) {
            Utils.sleep(25);
        }
    }
}
