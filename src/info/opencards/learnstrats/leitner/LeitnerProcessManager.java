package info.opencards.learnstrats.leitner;

import info.opencards.OpenCards;
import info.opencards.core.*;
import info.opencards.ui.AbstractLearnDialog;

import java.util.Collection;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class LeitnerProcessManager extends LearnProcessManager {


    public LeitnerProcessManager(List<CardFile> cardFiles, ItemValuater itemValuater, LeitnerLearnMethodFactory factory) {
        super(itemValuater, factory);

        this.lmFactory = factory;
        this.itemValuater = itemValuater;

        scheduler.clear();

        for (CardFile cardFile : cardFiles) {
            LeitnerSystem leitnerItems = cardFile.getFlashCards().getLeitnerItems();
            List<Item> scheduledCardFileItems = leitnerItems.getNotLastBoxCards();

            scheduler.put(cardFile, scheduledCardFileItems);
        }

        procIt = scheduler.keySet().iterator();

//        if (itemValuater instanceof LeitnerLearnDialog) {
//            ((LeitnerLearnDialog) itemValuater).setLeitnerSystem(leitnerSystem);
//        }

        addLearnProcessManagerProcessListener(new LearnProcListener() {
            public void processFinished(boolean wasInterrupted) {
                OpenCards.showLastMinConfigView(null);
            }
        });
    }


    public void itemChanged(Item item, boolean stillOnSchedule, Integer feedback) {
    }


    public void processStatusInfo(String statusMsg, double completeness) {
        if (itemValuater instanceof AbstractLearnDialog) {
//            int completeness = (int) (100 * numProcessed / (double) numScheduled);
//            ((AbstractLearnDialog) itemValuater).updateStatus(completeness, (numScheduled - numProcessed) + " " + Utils.getRB().getString("AbstractLearnDialog.statusbar.text"));
            ((AbstractLearnDialog) itemValuater).updateStatus((int) completeness, statusMsg);
        }
    }


    public void setupSchedule(Collection<CardFile> curFiles) {
        procIt = scheduler.keySet().iterator();
    }


    protected ItemCollection getItemCollection(CardFile cardFile) {
        return cardFile.getFlashCards().getLeitnerItems();
    }
}
