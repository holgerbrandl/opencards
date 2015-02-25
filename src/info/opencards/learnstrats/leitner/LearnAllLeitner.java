package info.opencards.learnstrats.leitner;

import info.opencards.Utils;
import info.opencards.core.Item;
import info.opencards.core.ItemValuater;
import info.opencards.core.LearnMethod;
import info.opencards.core.LearnMethodListener;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class LearnAllLeitner extends LeitnerLearning {


    private LearnAllLeitner(ItemValuater itemValuater) {
        super(itemValuater);
    }


    protected boolean processItemFeedBack(Item item, Integer feedback) {
        return updateLeitnerSystem((LeitnerItem) item, feedback);
    }


    public void fireStatusInfo() {
        int progress = (int) computeProgress();
        for (LearnMethodListener procListener : procListeners) {
            procListener.processStatusInfo(progress + "% " + Utils.getRB().getString("AbstractLearnDialog.percentDone"), progress);
        }
    }


    double computeProgress() {
        int numReqLearnSteps = 0;
        int numBoxes = leitnerSystem.numBoxes();

        for (int i = 0; i < numBoxes; i++) {
            int remBoxes = numBoxes - (i + 1);
            numReqLearnSteps += remBoxes * leitnerSystem.getBox(i).size();
        }

        return 100 - 100 * numReqLearnSteps / (double) ((numBoxes - 1) * leitnerSystem.getAllCards().size());
    }


    /**
     * Creates a factory which is able to instantiate <code>LeitnerLearning</code>-instances.
     */
    public static LeitnerLearnMethodFactory getFactory() {
        return new LeitnerLearnMethodFactory() {
            public LearnMethod createLearner(ItemValuater itemValuater) {
                return new LearnAllLeitner(itemValuater);
            }
        };
    }
}
