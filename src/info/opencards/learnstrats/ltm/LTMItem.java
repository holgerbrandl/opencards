package info.opencards.learnstrats.ltm;

import info.opencards.core.FlashCard;
import info.opencards.core.Item;

import java.util.Date;


/**
 * A spaced repetition model from the derived SM2-algorithm. For details cf. http://www.supermemo.com/english/ol/sm2.htm
 *
 * @author Holger Brandl
 */
public class LTMItem extends Item implements Cloneable {


    /**
     * Easiness factor reflecting the easiness of memorizing and retaining a given item in memory.
     */
    private double easiness;

    private int numRepetition;
    private Date lastQuery;

    /**
     * A date before the item can not be scheduled.
     */
    private Date skipUntil;

    public static final String DESIRED_RETENTION = "retPolicy";
    public static final int DESIRED_RETENTION_DEFAULT = 80;


    public LTMItem(FlashCard flashCard) {
        super(flashCard);

        reset();
    }


    public void reset() {
        easiness = 2.5;
        numRepetition = 0;
        lastQuery = new Date(System.currentTimeMillis());
        skipUntil = null;
    }


    /**
     * Updates the E-factor of this item [0...5]. Small values indicate that the item is (almost) not known. Note:
     * Anki's approach http://ankisrs.net/docs/FrequentlyAskedQuestions.html#_what_spaced_repetition_algorithm_does_anki_use
     */
    public void updateEFactor(double feedback) {
        if (feedback > 5 || feedback < 1)
            throw new RuntimeException("invalid feedback value: " + feedback);

        // This means that item which are learnt with OpenCards after midnight with
        // an OpenCards instantiated before midnight will be scheduled for the same day after the user has slept
        lastQuery = ScheduleUtils.getToday();

        numRepetition++;
        if (feedback < 3) {
            numRepetition = 0;
//            return; // this was used until v0.16.1 but it seems to be better to adjust the easiness for every possible feedbackvalue
        }

        if (feedback == 3)
            numRepetition = Math.min(2, numRepetition);

        easiness += (0.1 - (5 - feedback) * (0.08 + (5 - feedback) * 0.02));

        if (easiness < 1.3) {
            easiness = 1.3;
        }
    }


    /**
     * Returns the easiness score of this item. The higher the value the better is it memorized by the user. Low values
     * indicate bad retention in then past.
     */
    public double getEFactor() {
        return easiness;
    }


    public boolean isNew() {
        return numRepetition == 0 && easiness == 2.5; // not perfect but should work in most cases
    }


    public int getNumRepetition() {
        return numRepetition;
    }


    void setNumRepetition(int numRepetition) {
        this.numRepetition = numRepetition;
    }


    public Date getLastQuery() {
        return lastQuery;
    }


    public boolean isScheduledForToday() {
        Date date = getNextScheduledDate();
        return ScheduleUtils.isToday(date) || ScheduleUtils.isBeforeToday(date);
    }


    public Date getNextScheduledDate() {
        int dayInc = getInterRepetitionInterval(numRepetition);

        // apply the user-desired retention-factor here
        int desRetention = (Integer) getProperty(DESIRED_RETENTION, DESIRED_RETENTION_DEFAULT);

        if (dayInc > 2 && desRetention >= 50 && desRetention <= 100) {
            double incWeight = DESIRED_RETENTION_DEFAULT / (double) desRetention;
            dayInc = (int) Math.round(incWeight * dayInc);
        }

        // make all item to appear at least once a year
        if (dayInc > 365)
            dayInc = 365;

        Date nextDate = ScheduleUtils.getIncDate(lastQuery, dayInc);

        // change the nextDate to the skipDate if the latter lays in the future
        if (skipUntil != null) {
            nextDate = nextDate.compareTo(skipUntil) < 0 ? skipUntil : nextDate;
        }

        return nextDate;
    }


    private int getInterRepetitionInterval(int numRepetitions) {
        switch (numRepetitions) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 4;
            default:
                int interRepetitionInterval = getInterRepetitionInterval(numRepetitions - 1);
                return (int) Math.round(interRepetitionInterval * getEFactor() * 0.8);
        }
    }


    public void skipUntil(Date date) {
        this.skipUntil = date;
    }


    public boolean isSkippedForToday() {
        return skipUntil != null && ScheduleUtils.getDayDiff(skipUntil, ScheduleUtils.getToday()) > 0;
    }


    public Object clone() {
        try {
            LTMItem clonedItem = (LTMItem) super.clone();
            clonedItem.lastQuery = new Date(lastQuery.getTime());

            if (skipUntil != null)
                clonedItem.skipUntil = new Date(skipUntil.getTime());

            return clonedItem;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append(super.toString() + "; easiness=" + easiness + "; numRepetition=" + numRepetition + "; lastQuery=" + lastQuery);

        return s.toString();
    }


    public static String exhaustiveStringDump(LTMItem item) {
        StringBuilder s = new StringBuilder();

        s.append("\neasiness=" + item.easiness + "; numRepetition=" + item.numRepetition + "; lastQuery=" + item.lastQuery);
        s.append("\nnextOnScheduleFor=" + item.getNextScheduledDate() + "; interRepInterval=" + item.getInterRepetitionInterval(item.numRepetition));
        s.append("\n");
        for (int i = 1; i < 6; i++) {
            LTMItem cloneItem = (LTMItem) item.clone();
            cloneItem.updateEFactor(i);
            s.append(i + "-->" + cloneItem.getNextScheduledDate() + ";  ");
        }

        return s.toString();
    }
}
