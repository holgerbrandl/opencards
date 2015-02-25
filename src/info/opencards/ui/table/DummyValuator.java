package info.opencards.ui.table;

import info.opencards.core.*;


/**
 * A dummy implmentation required only to create a dummy process manager in order to compute meaningful stats for the
 * cardfile-table view.
 *
 * @author Holger Brandl
 * @see info.opencards.ui.table.CardTableModel
 */
public class DummyValuator implements ItemValuater {


    public void score(Item item) {
    }


    public void prepareLearnSession() {
    }


    public void finishedLearnSession() {
    }


    public void startFileSession(CardFile cardFile, ItemCollection cardItemCollection) {
    }


    public void finishedFileSession(CardFile cardFile) {
    }


    public void addScoringListener(ScoringListener l) {
    }


    public void removeScoringListener(ScoringListener l) {
    }


    public void prepareFileSession(CardFile cardFile) {
    }
}
