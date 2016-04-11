package info.opencards.learnstrats.ltm;

import info.opencards.Utils;
import info.opencards.core.*;
import info.opencards.ui.AbstractLearnDialog;

import java.util.*;
import java.util.logging.Level;


/**
 * A long-term learning (LTM) processing and scheduling controller.
 *
 * @author Holger Brandl
 */
public class LTMProcessManager extends LearnProcessManager {


    int numScheduled;
    private int numNewScheduled;

    int numProcessed;
    private int numNewProcessed;

    /**
     * If this flag is set all new items will be included into the schedule.
     */
    private boolean includeAllNew;

    private final Map<CardFile, List<Item>> skippedItems = new HashMap<CardFile, List<Item>>();
    private final Map<CardFile, List<Item>> newButNotSchedItems = new HashMap<CardFile, List<Item>>();


    public LTMProcessManager(ItemValuater itemValuater, LearnMethodFactory factory) {
        super(itemValuater, factory);
    }


    public void setupSchedule(Collection<CardFile> curFiles) {
        numScheduled = 0;
        numNewScheduled = 0;

        // note: we do NOT clear the skipped items here to make sure that session skipped items do not reapperar in
        //       in reused procManagers

        scheduler.clear();

//        Utils.log("preparing session");

        int maxNumNew = includeAllNew ? Integer.MAX_VALUE : ScheduleUtils.getMaxCardToBeLearntToday();
        int allNew = 0;

        Map<CardFile, Integer> newCounts = new HashMap<CardFile, Integer>();
        for (CardFile curFile : curFiles) {
            LTMCollection ltmCollection = curFile.getFlashCards().getLTMItems();
            ArrayList<Item> newItems = ScheduleUtils.getNewItems(ltmCollection);

            newCounts.put(curFile, newItems.size());
            allNew += newItems.size();
        }

        // now compute the new amount for each class based on the number maximal number of newItems
        double newRatio = maxNumNew / (double) allNew;
        if (newRatio < 1) {
            for (CardFile file : curFiles) {
                int adaptNumNew = (int) Math.round(newRatio * newCounts.get(file));
                newCounts.put(file, adaptNumNew);
            }
        }

        // now setup a schedule for today which includes all scheduled whereby the number of new cards may be restricted
        // The possible amount of new cards is thereby distributed equally between all files (relative to their size)
        final Map<CardFile, ArrayList<Item>> unsortScheduler = new HashMap<CardFile, ArrayList<Item>>();
        for (CardFile curFile : curFiles) {
            LTMCollection ltmCollection = curFile.getFlashCards().getLTMItems();
            ArrayList<Item> unfilteredItems = ltmCollection.getScheduledItems();

            int numNew = maxNumNew - newCounts.get(curFile) >= 0 ? newCounts.get(curFile) : maxNumNew;
            ArrayList<Item> scheduledFileItems = ScheduleUtils.getNewReducedItCo(unfilteredItems, numNew);

            // extract the not scheduled ones
            newButNotSchedItems.put(curFile, getNewButNotScheduledItems(unfilteredItems, scheduledFileItems));

            unsortScheduler.put(curFile, scheduledFileItems);

            ArrayList<Item> scheduledNewItems = ScheduleUtils.getNewItems(scheduledFileItems);
            maxNumNew -= scheduledNewItems.size();

            numScheduled += scheduledFileItems.size();
            numNewScheduled += scheduledNewItems.size();
        }

        // finally reorder files based on urgentness
        List<CardFile> presortFiles = new ArrayList<CardFile>(unsortScheduler.keySet());
        Collections.sort(presortFiles, new Comparator<CardFile>() {
            public int compare(CardFile o1, CardFile o2) {
                return (int) (ScheduleUtils.getUrgency(unsortScheduler.get(o1)) - ScheduleUtils.getUrgency(unsortScheduler.get(o2)));
            }
        });

        for (CardFile presortFile : presortFiles) {
            scheduler.put(presortFile, unsortScheduler.get(presortFile));
        }

        procIt = scheduler.keySet().iterator();

        numProcessed = 0;
        numNewProcessed = 0;
    }


    private List<Item> getNewButNotScheduledItems(ArrayList<Item> allItems, ArrayList<Item> scheduledFileItems) {

        ArrayList<Item> notScheduledNewItems = new ArrayList<Item>(allItems);
        notScheduledNewItems.removeAll(scheduledFileItems);

        for (int i = 0; i < notScheduledNewItems.size(); i++) {
            Item item = notScheduledNewItems.get(i);
            if (!((LTMItem) item).isNew())
                notScheduledNewItems.remove(item);
        }

        return notScheduledNewItems;
    }


    public Map<CardFile, List<Item>> getNewButNotScheduledItems() {
        return newButNotSchedItems;
    }


    protected ItemCollection getItemCollection(CardFile cardFile) {
        return cardFile.getFlashCards().getLTMItems();
    }


    public void processStatusInfo(String statusMsg, double completeness) {
        if (itemValuater instanceof AbstractLearnDialog) {
//            int completeness = (int) (100 * numProcessed / (double) numScheduled);
//            ((AbstractLearnDialog) itemValuater).updateStatus(completeness, (numScheduled - numProcessed) + " " + Utils.getRB().getString("AbstractLearnDialog.statusbar.text"));

            String msg = (numScheduled - numProcessed) + " " + Utils.getRB().getString("AbstractLearnDialog.statusbar.text");
            ((AbstractLearnDialog) itemValuater).updateStatus((int) (100 * numProcessed / (double) (numScheduled)), msg);
        }
    }


    public void itemChanged(Item item, boolean stillOnSchedule, Integer feedback) {
        Utils.log(Level.FINE, "itemChanged onschedule=" + stillOnSchedule);

        LTMItem ltmItem = (LTMItem) item;

        if (feedback.equals(LearnMethod.SKIP_UNTIL_NEXT) || feedback.equals(LearnMethod.SKIP_UNTIL_TOMORROW)) {
            if (!skippedItems.containsKey(curFile))
                skippedItems.put(curFile, new ArrayList<Item>());

            skippedItems.get(curFile).add(item);
        }

        if (ltmItem.isScheduledForToday() && stillOnSchedule)
            return;

        numProcessed++;

        if (ltmItem.getNumRepetition() == 1 && !ltmItem.isSkippedForToday()) { // was new item ?
            numNewProcessed++;
            ScheduleUtils.setNumLearntToday(ScheduleUtils.getNumLearntToday() + 1);
        }

        processStatusInfo(null, -1);
    }


    public boolean isScheduled(CardFile cardFile) {
        return scheduler.containsKey(cardFile);
    }


    public void setIncludeAllNew(boolean includeAllNew) {
        this.includeAllNew = includeAllNew;
    }


    public Map<CardFile, List<Item>> getSkippedItems() {
        return skippedItems;
    }


    public void unschdeduleItem(CardFile cardFile, Item item) {
        List<Item> scheduledItems = getScheduledItems(cardFile);
        if (scheduledItems == null || scheduledItems.isEmpty())
            return;

        if (scheduledItems.contains(item)) {
            scheduledItems.remove(item);
            numScheduled--;

            if (((LTMItem) item).isNew())
                numNewScheduled--;
        }
    }
}
