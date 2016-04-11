package info.opencards.learnstrats.ltm;

import info.opencards.Utils;
import info.opencards.core.Item;
import info.opencards.core.ItemValuater;
import info.opencards.ui.preferences.GlobLearnSettings;

import java.util.ArrayList;
import java.util.List;


/**
 * An improved version of SM2 which also incoporates a kind of ultra-short-term history into the scheduling process.
 *
 * @author Holger Brandl
 */
public class UltraShortSM2 extends SM2 {


    /**
     * The in-session re-scheduler to be used to find an optimal next recall-point still unknown items.
     */
    private final USTMScheduler ustmScheduler;


    public UltraShortSM2(ItemValuater itemValuater) {
        super(itemValuater);

        long ustmDelay = 1000 * Utils.getPrefs().getInt(GlobLearnSettings.USTM_DELAY, GlobLearnSettings.USTM_DELAY_DEFAULT);
        ustmScheduler = new USTMScheduler(ustmDelay);
    }


    protected void scoreNextItem() {
        // try to recall the latest ustm-item (which timed out while the last shown item was evaluated).
        if (ustmScheduler.hasScheduledItems()) {
            Item item = ustmScheduler.getScheduledItem();
            assert scheduledItems.contains(item);

            valuater.score(item);
        } else {
            // handle the case that
            // test whether there are still some not-ustm-rescheduled items to be tested before interrupting test-threads

            //are all scheduled already ustm-rescheduled
            if (ustmScheduler.getAllCurrentItems().containsAll(scheduledItems)) {
                // all remaining cards are in rescheduled. --> select the one with lowest timeout-value
                valuater.score(ustmScheduler.cancelNextTask());
            } else {
                // score an arbitrary item which is not yet ustm-rescheduled (there is at least one because of the last 'if')
                List<Item> ustmReducedItems = new ArrayList<Item>(scheduledItems);
                ustmReducedItems.removeAll(ustmScheduler.getAllCurrentItems());
                assert ustmReducedItems.size() > 0;

//                Utils.log("ustm reduced items size" + ustmReducedItems.size());
                Item nextItem = ustmReducedItems.get(Utils.getRandGen().nextInt(ustmReducedItems.size()));
//                Utils.log("next item " + nextItem);

                valuater.score(nextItem);
            }
        }
    }


    protected boolean applyLTMFeedback(Item item, Integer feedback) {
        boolean isScheduledForToday = !super.applyLTMFeedback(item, feedback);

        if (isScheduledForToday) {
            // register the item to the ust-scheduler
            ustmScheduler.reschedule(item);
            return false;
        }

        return !isScheduledForToday;
    }


    protected boolean isFinished() {
        boolean isFinished = super.isFinished();
        if (isFinished)
            ustmScheduler.stop();
        return isFinished;
    }
}
