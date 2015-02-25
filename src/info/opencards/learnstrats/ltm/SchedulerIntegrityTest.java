package info.opencards.learnstrats.ltm;

import info.opencards.core.FlashCard;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Date;


/**
 * Some unit-tests which aim to ensure that the scheduling algorithm somehow makes sense.
 *
 * @author Holger Brandl
 */
public class SchedulerIntegrityTest {


    private static final int mxRecursions = 9; // this will aleady take some time


    /**
     * Tests that given an item in an arbitrary state, the scheduled dates are monotically increasing with increasingly
     * positive feedback.
     */
    @Test
    public void testIntegrity() {
        LTMItem ltmItem = new LTMItem(new FlashCard(1, "A", 1));

        recurseFeedbacks(ltmItem, mxRecursions);
    }


    private void recurseFeedbacks(LTMItem ltmItem, int curRecursionDepth) {
        Date today = ltmItem.getNextScheduledDate();

//        System.out.println("Testing item: " + ltmItem.toString());

        for (int i = 1; i <= 4; i++) {
            String errMsg = "mismatch on recDepthLevel=" + (mxRecursions - curRecursionDepth) + " and feeback=" + i + "\n" + ltmItem.toString();

            // assume that we've reached <code>today</code>
            ScheduleUtils.testToday = today;

            LTMItem curItem = copy(ltmItem);
            curItem.updateEFactor(i);
            Date curItDate = curItem.getNextScheduledDate();

            LTMItem nextItem = copy(ltmItem);
            nextItem.updateEFactor(i + 1); // better feedback
            Date nextItDate = nextItem.getNextScheduledDate();

            // scheduling should never schedule something to the past
            if (!(ScheduleUtils.getDayDiff(curItDate, today) >= 0))
                Assert.fail(errMsg);

            // if the feedback is greater than 2 an item should always be scheduled for a future date
            if (i > 2 && ScheduleUtils.getDayDiff(curItDate, today) == 0) {
                Assert.fail(errMsg);
            }

            // make sure that the higher feedback gives at least an equal next scheduled review-date
            if (ScheduleUtils.getDayDiff(curItDate, nextItDate) > 0)
                Assert.fail(errMsg);
        }

        // stop further testing if the maximal recursion depth has been reached
        if (curRecursionDepth == 0)
            return;

        // now recurse into the next level
        for (int i = 1; i <= 5; i++) {
            LTMItem updatedItem = copy(ltmItem);
            updatedItem.updateEFactor(i);

            recurseFeedbacks(updatedItem, curRecursionDepth - 1);
        }
    }


    private static LTMItem copy(LTMItem ltmItem) {
        return (LTMItem) ltmItem.clone();
    }
}
