package info.opencards.learnstrats.leitner;

import info.opencards.Utils;
import info.opencards.core.*;

import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class TimeLimitLeitner extends LeitnerLearning {


    private long startTime;
    private long sessionTimeoutMs;

    private boolean hasTimouted;
    private Thread timerThread;


    private TimeLimitLeitner(ItemValuater itemValuater, int sessionTimeoutMin) {
        super(itemValuater);

        assert sessionTimeoutMin > 0;
        sessionTimeoutMs = sessionTimeoutMin * 60 * 1000;
    }


    protected boolean processItemFeedBack(Item item, Integer feedback) {
        return updateLeitnerSystem((LeitnerItem) item, feedback);
    }


    protected boolean isFinished() {
        boolean isFinished = super.isFinished() || hasTimouted;
        if (isFinished)
            timerThread.interrupt();

        return isFinished;
    }


    public void run(ItemCollection parentCollection, List<Item> scheduledFileItems) {
        super.run(parentCollection, scheduledFileItems);

        startTime = System.currentTimeMillis();
        timerThread = new Thread() {

            public void run() {
                super.run();

                while (!isInterrupted()) {
                    double progess = computeProgress();
                    if (progess > 1) {
                        hasTimouted = true;
                    }

                    fireStatusInfo();

                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        http://stackoverflow.com/questions/5008176/thread-interrupt-doesnt-work
                        Thread.currentThread().interrupt();
                    }
                }
                System.err.println("timer thread stopped");
            }
        };
        timerThread.start();
    }


    public void fireStatusInfo() {
        double progess = 100 * computeProgress();
        String msg;

        if (progess > 100) {
            hasTimouted = true;
            msg = Utils.getRB().getString("Leitner.timeout");
        } else {
            String remainingSecsMsg = Utils.getRB().getString("leitner.leftsecs");
            msg = (int) ((sessionTimeoutMs - (System.currentTimeMillis() - startTime)) / 1000) + " " + remainingSecsMsg;
        }

        for (LearnMethodListener procListener : procListeners) {
            procListener.processStatusInfo(msg, (int) (progess > 100 ? 100 : progess));
        }
    }


    double computeProgress() {
        long surSesTime = System.currentTimeMillis() - startTime;
        return surSesTime / (double) sessionTimeoutMs;
    }


    /**
     * Creates a factory which is able to instantiate <code>LeitnerLearning</code>-instances.
     */
    public static LeitnerLearnMethodFactory getFactory(final int sessionTimeoutMin) {
        return new LeitnerLearnMethodFactory() {
            public LearnMethod createLearner(ItemValuater itemValuater) {
                return new TimeLimitLeitner(itemValuater, sessionTimeoutMin);
            }

        };
    }
}
