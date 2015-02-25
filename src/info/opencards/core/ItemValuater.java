package info.opencards.core;

/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public interface ItemValuater {


    /**
     * Generates a feedback score given an item. Usually the user will score the item after the question about the item
     * was presented to her.
     */
    void score(Item item);


    void prepareLearnSession();


    void finishedLearnSession();


    void startFileSession(CardFile cardFile, ItemCollection cardItemCollection);


    void finishedFileSession(CardFile cardFile);


    void addScoringListener(ScoringListener l);


    void removeScoringListener(ScoringListener l);


    void prepareFileSession(CardFile cardFile);
}
