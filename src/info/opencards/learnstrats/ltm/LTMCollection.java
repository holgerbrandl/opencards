package info.opencards.learnstrats.ltm;

import info.opencards.core.FlashCard;
import info.opencards.core.Item;
import info.opencards.core.ItemCollection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class LTMCollection extends ItemCollection {


    public ArrayList<Item> getScheduledItems() {
        ArrayList<Item> scheduledItems = new ArrayList<Item>();
        for (Item item : this) {
            if (((LTMItem) item).isScheduledForToday())
                scheduledItems.add(item);
        }

        return scheduledItems;
    }


    public void addItem(FlashCard flashCard) {
        add(new LTMItem(flashCard));
    }


    /**
     * Returns the items scheduled for a given date.
     *
     * @param includeUntil if <code>true</code> also all items until this date become included.
     */
    public List<LTMItem> predictItemsForDate(Date predictedSchedDate, boolean includeUntil) {
        List<LTMItem> predictedItems = new ArrayList<LTMItem>();

        for (Item item : this) {
            LTMItem ltmItem = (LTMItem) item;
            Date scheduledDate = ltmItem.getNextScheduledDate();

            if (ScheduleUtils.getDayDiff(scheduledDate, predictedSchedDate) == 0) {
                predictedItems.add(ltmItem);
            } else if (includeUntil && scheduledDate.compareTo(predictedSchedDate) < 0) {
                predictedItems.add(ltmItem);
            }
        }

        return predictedItems;
    }


    public Object clone() {
        LTMCollection cloneIC = (LTMCollection) super.clone();

        cloneIC.clear();

        try {
            for (Item item : this) {
                item = (Item) item.clone();
                cloneIC.add(item);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        cloneIC.props = new HashMap<String, Object>(props);

        return cloneIC;
    }
}
