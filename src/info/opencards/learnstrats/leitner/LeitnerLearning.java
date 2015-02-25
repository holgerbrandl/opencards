package info.opencards.learnstrats.leitner;

import info.opencards.Utils;
import info.opencards.core.Item;
import info.opencards.core.ItemCollection;
import info.opencards.core.ItemValuater;
import info.opencards.core.LearnMethod;
import info.opencards.ui.AbstractLearnDialog;
import info.opencards.ui.preferences.LeitnerSettings;

import java.util.ArrayList;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public abstract class LeitnerLearning extends LearnMethod {


    LeitnerSystem leitnerSystem;

    private final List<Item> skipList = new ArrayList<Item>();

    private Item lastItem;


    LeitnerLearning(ItemValuater itemValuater) {
        super(itemValuater);
    }


    public void run(ItemCollection parentCollection, List<Item> scheduledFileItems) {
        leitnerSystem = (LeitnerSystem) parentCollection;

        scoreNextItem();
    }


    protected void scoreNextItem() {
        // do not show last card if possible
        if (getNonFinalCards().size() <= 1)
            valuater.score(leitnerSystem.getRandomCard(0, leitnerSystem.numBoxes() - 2, skipList));
        else {
            ArrayList<Item> tempSkipList = new ArrayList<Item>(skipList);

            // add the last-item to the skipped ones to avoid that it is immediatly rescheduled if:
            // a) the there are still other items left to be learned
            if (lastItem != null && getNonFinalCards().size() - skipList.size() != 1)
                tempSkipList.add(lastItem);
            valuater.score(leitnerSystem.getRandomCard(0, leitnerSystem.numBoxes() - 2, tempSkipList));
        }

    }


    /**
     * Returns all flashcards which are not in the last box yet.
     */
    private List<Item> getNonFinalCards() {
        return leitnerSystem.getAllCards(0, leitnerSystem.numBoxes() - 2);
    }


    protected boolean isFinished() {
        List<Item> queryCards = getNonFinalCards();
        return queryCards.isEmpty() || skipList.containsAll(queryCards);
    }


    /**
     * @return <code>true</code> if the item is still on schedule in the current session.
     */
    boolean updateLeitnerSystem(LeitnerItem item, Integer feedback) {
        lastItem = null;

        if (feedback.equals(AbstractLearnDialog.INVALID_ITEM)) {
            leitnerSystem.remove(item);
            return false;
        }

        if (feedback.equals(LearnMethod.SKIP_UNTIL_TOMORROW) || feedback.equals(LearnMethod.SKIP_UNTIL_NEXT)) {
            skipList.add(item);
            return false;
        }

        lastItem = item;

        item.getFlashCard().resetRndRevPolicy(); // to make items to change their reversing when moving from box to box

        if (didKnew(feedback)) {
            item.setState(LeitnerSystem.LEARNT);
            leitnerSystem.moveUp(item);

        } else {
            item.setState(LeitnerSystem.FAILED);

            // dependent on the properties move the card to the next lower or to the first leitner box
            if (Utils.getPrefs().getBoolean(LeitnerSettings.DO_MOVE2_FIRST, LeitnerSettings.DO_MOVE2_FIRST_DEFAULT)) {
                leitnerSystem.moveCard(item, 0);
            } else {
                leitnerSystem.moveDown(item);
            }
        }

        return leitnerSystem.getBoxIndex(item) == (leitnerSystem.numBoxes() - 1);
    }


    boolean didKnew(Integer feedback) {
        assert feedback == AbstractLearnDialog.SCORE_1 || feedback == AbstractLearnDialog.SCORE_5;

        return feedback == AbstractLearnDialog.SCORE_5;
    }
}

