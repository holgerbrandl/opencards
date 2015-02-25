package info.opencards.learnstrats.ltm;

import info.opencards.Utils;
import info.opencards.core.*;
import info.opencards.ui.AbstractLearnDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * An adapted SM type II implementation. This implementation is based on http://www.supermemo.com/english/ol/sm2.htm and
 * some ideas implemented within the mnemosyne-project ( http://mnemosyne-proj.sourceforge.net )
 *
 * @author Holger Brandl
 */
public class SM2 extends LearnMethod {


    List<Item> scheduledItems = new ArrayList<Item>();


    SM2(ItemValuater itemValuater) {
        super(itemValuater);
    }


    /**
     * @return <code>true</code> if the item is still on schedule in the current session
     */
    protected boolean processItemFeedBack(Item item, Integer feedback) {
        if (feedback.equals(AbstractLearnDialog.INVALID_ITEM)) {
            unscheduleItem(item);
            return false;
        }

        if (feedback.equals(LearnMethod.SKIP_UNTIL_NEXT)) {
            ((LTMItem) item).skipUntil(ScheduleUtils.getIncDate(ScheduleUtils.getToday(), 0));
            unscheduleItem(item);
            return false;
        }

        if (feedback.equals(LearnMethod.SKIP_UNTIL_TOMORROW)) {
            ((LTMItem) item).skipUntil(ScheduleUtils.getIncDate(ScheduleUtils.getToday(), 1));
            unscheduleItem(item);
            return false;
        }

        return !applyLTMFeedback(item, feedback);
    }


    /**
     * @return <code>true</code> if hte item is no longer scheduled for today.
     */
    boolean applyLTMFeedback(Item item, Integer feedback) {
        assert item instanceof LTMItem;
        LTMItem ltmItem = (LTMItem) item;
        ltmItem.updateEFactor(feedback);

        if (!ltmItem.isScheduledForToday()) {
            unscheduleItem(item);
            return true;
        }

        return false;
    }


    private void unscheduleItem(Item item) {
        //reset the random reverse state if necessary because the item is finished for now
        item.getFlashCard().resetRndRevPolicy();

        scheduledItems.remove(item);
    }


    public void fireStatusInfo() {
        for (LearnMethodListener procListener : procListeners) {
            procListener.processStatusInfo("test", 0.75);
        }
    }


    protected boolean isFinished() {
        return scheduledItems.isEmpty();
    }


    public void run(ItemCollection parentCollection, List<Item> scheduledFileItems) {
        // oc 0.12 and prior:
//        scheduledItems = ScheduleUtils.getScheduledItems(scheduledFileItems);  // why? the items were already chosen with the same method

        // note: using the reference here will allow to sucessfully unschedule the file within a session
        scheduledItems = scheduledFileItems;

        // compare this to the old outdated approach
//        scheduledItems = new ArrayList<Item>(scheduledFileItems);

        scoreNextItem();
    }


    protected void scoreNextItem() {
        valuater.score(scheduledItems.get(Utils.getRandGen().nextInt(scheduledItems.size())));
    }


    /**
     * Creates a factory which is able to instantiate <code>SM2</code>-instances.
     */
    public static LearnMethodFactory getFactory() {
        return new LearnMethodFactory() {
            public LearnMethod createLearner(ItemValuater itemValuater) {
//                return new SM2(itemValuater);
                return new UltraShortSM2(itemValuater);
            }
        };
    }
}
