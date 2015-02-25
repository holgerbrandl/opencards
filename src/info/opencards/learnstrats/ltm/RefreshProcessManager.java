package info.opencards.learnstrats.ltm;

import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.core.ItemValuater;
import info.opencards.core.LearnMethodFactory;

import java.util.*;


/**
 * A simple process-manager which just
 *
 * @author Holger Brandl
 */
public class RefreshProcessManager extends LTMProcessManager {


    public RefreshProcessManager(ItemValuater itemValuater, LearnMethodFactory factory) {
        super(itemValuater, factory);
    }


    public void setupSchedule(Collection<CardFile> curFiles) {
        scheduler.clear();

        // finally reorder files based on urgentness
        List<CardFile> presortFiles = new ArrayList<CardFile>(curFiles);
        Collections.sort(presortFiles, new Comparator<CardFile>() {
            public int compare(CardFile o1, CardFile o2) {
                return (int) (ScheduleUtils.getUrgency(o1.getFlashCards().getLTMItems()) -
                        ScheduleUtils.getUrgency(o2.getFlashCards().getLTMItems()));
            }
        });

        for (CardFile presortFile : presortFiles) {
            ArrayList<Item> fileItems = new ArrayList(presortFile.getFlashCards().getLTMItems());

            // remove new items (because this a refreshing scheduler)
            fileItems.removeAll(ScheduleUtils.getNewItems(fileItems));

            numScheduled += fileItems.size();

            scheduler.put(presortFile, fileItems);
        }

        procIt = scheduler.keySet().iterator();
    }


    public void itemChanged(Item item, boolean stillOnSchedule, Integer feedback) {
        LTMItem ltmItem = (LTMItem) item;

        if (ltmItem.isScheduledForToday() && stillOnSchedule)
            return;

        // reduce the number of reviews by one to keep spacing-model in place for numIt>2
        // note: we don't revert the e-factor here but this shouldn't worse the things too much
        if (ltmItem.getNumRepetition() > 2)
            ltmItem.setNumRepetition(ltmItem.getNumRepetition() - 1);

        numProcessed++;
        processStatusInfo(null, -1);
    }
}
