package info.opencards.md;

import com.sun.javafx.application.PlatformImpl;
import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.List;

public class MdSlideManager extends AbstractSlideManager {
    private WebEngine webEngine;
    private JFXPanel jfxPanel;


    public List<MarkdownFlashcard> slides;


    @Override
    public boolean showCardQuestion(Item item) {
        // todo implemnt optional sync

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


    private void renderHtml(String content) {
        // http://stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        Platform.runLater(() -> webEngine.loadContent("<body>" + content + "</body≈>"));
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
        slides = MarkdownParserKt.parseMD(cardFile.getFileLocation());

        jfxPanel = new JFXPanel();
        createScene();

        JPanel renderContainer = OpenCards.getInstance().getLearnPanel().getSlideRenderPanel();
        renderContainer.removeAll();
        renderContainer.add(jfxPanel);
        renderContainer.invalidate();
    }


    @Override
    public void stopFileSession() {
        Utils.log("stopped md-file session");
        slides = null;
    }


    private void createScene() {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                Stage stage;
                WebView browser;
                stage = new Stage();

                stage.setTitle("Hello Java FX");
                stage.setResizable(true);

                Group root = new Group();
                Scene scene = new Scene(root, 80, 20);
                stage.setScene(scene);

                // Set up the embedded browser:
                browser = new WebView();
                webEngine = browser.getEngine();
//                webEngine.load("http://heise.de");


//                ScrollPane scrollPane = new ScrollPane();
//                scrollPane.setContent(browser);
                webEngine.loadContent("<b>asdf</b>");

//                root.getChildren().addAll(scrollPane);
//                scene.setRoot(root);
//                stage.setScene(scene);


                ObservableList<Node> children = root.getChildren();
                children.add(browser);

                jfxPanel.setScene(scene);
            }
        });

        while (webEngine == null) {
            Utils.sleep(25);
        }
    }
}
