package info.opencards.core.test;

import info.opencards.CardFileBackend;
import info.opencards.core.*;

import javax.swing.*;
import java.util.Arrays;


/**
 * A test implementation of the <code>PresenterProxy</code>-interface, which is intended to allow development, testing
 * and debugging of new oc functionality without running the complete uno-deploy-loop each time.
 *
 * @author Holger Brandl
 */
public class MockSupplier implements SlideManager, LearnStatusSerializer {


    private static final FlashCardCollection mockItems = new FlashCardCollection();


    static {
        mockItems.addAll(Arrays.asList(createItem("rot"), createItem("gruen"), createItem("blau"),
                createItem("weiss"), createItem("schwarz"), createItem("gelb"), createItem("gelb")));
    }


    private Item curItem = mockItems.getItems(Item.class).get(0);


    /**
     * Creates a Mock-backend which is mainly used for testing purposes.
     */
    public static CardFileBackend getMockBackend(JFrame owner, MockSupplier mockSupplier) {
        return new CardFileBackend(mockSupplier, mockSupplier);
    }


    public boolean showCardQuestion(Item selectedCard) {
        assert selectedCard != null;

        curItem = selectedCard;
        System.out.println("showing card front of : " + selectedCard);
        return true;
    }


    public boolean showCompleteCard(Item selectedCard) {
        assert selectedCard != null;
        curItem = selectedCard;

        System.out.println("showing complete card  : " + selectedCard);
        return true;
    }


    /**
     * Used for testing purposes.
     */

    public Item getCurItem() {
        return curItem;
    }


    public FlashCardCollection readFlashcardsFromFile(CardFile cardFile) {
        throw new RuntimeException("not implemented yet");
    }


    public void serializeFileCards(CardFile cardFile, FlashCardCollection fileItems) {
        System.out.println("write meta-data");
    }


    public FlashCardCollection deserializeFileCards(CardFile cardFile) {
        System.out.println("read meta-data");
        return new FlashCardCollection();
    }


    public void startFileSession(ItemCollection cardItemCollection) {
        System.out.println("starting learn session....");
    }


    public void stopFileSession() {
        System.out.println("learning stopped");
    }


    public void openCardFile(CardFile cardFile) {
    }


    public void stopLearnSession() {
    }


    public CardFile getCurCardFile() {
        return null;
    }


    private static int counter = 0;


    private static FlashCard createItem(String title) {
        FlashCard card = new FlashCard(counter, title, counter++);
        new Item(card);

        return card;
    }
}
