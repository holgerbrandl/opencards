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
public class CardLimitLeitner extends LeitnerLearning {


    private int numLearnt = 0;
    private final int cardLimit;


    private CardLimitLeitner(ItemValuater itemValuater, int cardLimit) {
        super(itemValuater);
        this.cardLimit = cardLimit;
    }


    protected boolean processItemFeedBack(Item item, Integer feedback) {
        boolean didKnew = didKnew(feedback);

        if (didKnew)
            numLearnt++;

        return updateLeitnerSystem((LeitnerItem) item, feedback);
    }


    protected boolean isFinished() {
        return super.isFinished() || numLearnt >= cardLimit;
    }


    public void fireStatusInfo() {
        double progress = computeProgress();
        int numCardsLeft = cardLimit - numLearnt;
        String msg = numCardsLeft > 1 ?
                Utils.getRB().getString("AbstractLearnDialog.nCardsLeft") :
                Utils.getRB().getString("AbstractLearnDialog.oneCardLeft");

        for (LearnMethodListener procListener : procListeners) {
            procListener.processStatusInfo(numCardsLeft + " " + msg, progress);
        }
    }


    double computeProgress() {
        return 100 * (numLearnt / (double) cardLimit);
    }


    /**
     * Creates a factory which is able to instantiate <code>LeitnerLearning</code>-instances.
     */
    public static LeitnerLearnMethodFactory getFactory(final int cardLimit) {
        return new LeitnerLearnMethodFactory() {
            public LearnMethod createLearner(ItemValuater itemValuater) {
                return new CardLimitLeitner(itemValuater, cardLimit);
            }

        };
    }
}
