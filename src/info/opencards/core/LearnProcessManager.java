package info.opencards.core;

import info.opencards.Utils;

import java.util.*;
import java.util.logging.Level;


/**
 * An approach to create a common API for the processing of one or several card-sets using some kind of learning method.
 * Because not all learning methods (leitner, SM-type, etc.) can be generalized to use one single processing API, this
 * class intends to encapsulate the most common level, and shifting approach-dependent parts into some abstract methods
 * to be implemented by inheritors.
 * <p/>
 * The learning method used by the learning method, beomces factored by an instance of <code>LearnMethodFactoru</code>.
 * The big difference beween the method and the process is that learn-processes are able to process several files
 * whereas <code>LearnMethod</code>s are restricted to a single card-set only. The <code>LearnMethods</code> factored by
 * the factory selects which kind of <code>ItemCollection</code> should be learnt.
 * <p/>
 * This class is completely independent of the used serialization- and presetnation-backend. The serializers are
 * internal properties of <code>CardFile</code>s to be learnt. The presentation backend is provided in terms of an
 * <code>ItemValuater</code> which provides the necessary API to show cards (or parts of them to the user).
 *
 * @author Holger Brandl
 * @see CardFile
 * @see ItemValuater
 * @see LearnMethod
 * @see LearnMethodFactory
 */
public abstract class LearnProcessManager implements LearnMethodListener {


    protected final LinkedHashMap<CardFile, List<Item>> scheduler = new LinkedHashMap<CardFile, List<Item>>();
    protected Iterator<CardFile> procIt;

    protected ItemValuater itemValuater;
    protected CardFile curFile;

    private final List<LearnProcListener> learnProcListeners = new ArrayList<LearnProcListener>();

    protected LearnMethodFactory lmFactory;


    protected LearnProcessManager(ItemValuater itemValuater, LearnMethodFactory factory) {
        assert lmFactory != null : "learn method factory must not be null";
        assert itemValuater != null : "item valuter must not be null";

        this.lmFactory = factory;
        this.itemValuater = itemValuater;
    }


    public abstract void processStatusInfo(String statusMsg, double completeness);


    public abstract void setupSchedule(Collection<CardFile> curFiles);


    protected abstract ItemCollection getItemCollection(CardFile cardFile);


    public void startProcessing() {
        startProcessing(null);
    }


    private void startProcessing(CardFile cardFile) {
        Utils.log(Level.FINE, "processing next card-file: " + cardFile);

        assert procIt != null;

        if (cardFile == null) {
            Utils.log("card file is null");
            if (!procIt.hasNext()) {
                terminateProcess(false);
                return;
            }

            // this is should only be the case if the learn-process is started from outside
            cardFile = procIt.next();

            itemValuater.prepareLearnSession();
        }

        List<Item> scheduledFileItems = scheduler.get(cardFile);
        Utils.log(Level.FINEST, "scheduled items are : " + scheduledFileItems.toString());

        if (scheduledFileItems.size() > 0) {
            LearnMethod learnMethod = lmFactory.createLearner(itemValuater);
            learnMethod.addLearnProcessListener(this);

            curFile = cardFile;

            itemValuater.prepareFileSession(cardFile);
            cardFile.synchronize();

            ItemCollection cardItemCollection = getItemCollection(cardFile);
            itemValuater.startFileSession(cardFile, cardItemCollection);
            learnMethod.run(cardItemCollection, scheduledFileItems);
            learnMethod.fireStatusInfo();

//            cardFile.flush(CardFileBackend.getInstance().getSerializer(), true);
        } else {
            if (procIt.hasNext())
                startProcessing(procIt.next());
            else
                terminateProcess(false);
        }
    }


    public void cardFileProcessingFinished(boolean wasInterrupted) {
        Utils.log(Level.FINER, "cardFile processing finished");

        // flush to ensure that new sesstion state becomes persistent
        curFile.flush();

        itemValuater.finishedFileSession(curFile);

        if (wasInterrupted) {
            terminateProcess(true);
            return;
        }

        if (procIt.hasNext()) {
            startProcessing(procIt.next());
        } else {
            terminateProcess(wasInterrupted);
        }
    }


    private void terminateProcess(boolean wasInterrupted) {
        Utils.log(Level.FINER, "terminating session...");
        itemValuater.finishedLearnSession();

        for (LearnProcListener learnProcListener : learnProcListeners)
            learnProcListener.processFinished(wasInterrupted);
    }


    /**
     * Adds a new listener.
     */
    public void addLearnProcessManagerProcessListener(LearnProcListener l) {
        if (l == null || learnProcListeners.contains(l))
            return;

        learnProcListeners.add(l);
    }


    /**
     * Removes a listener.
     */
    public void removeLearnProcessManagerProcessListener(LearnProcListener l) {
        if (l == null)
            return;

        learnProcListeners.remove(l);
    }


    /**
     * Returns <code>true</code> if this schedules has scheduled some items for today.
     */
    public boolean hasScheduledItems() {

        int numScheduledCards = 0;
        for (List<Item> items : scheduler.values()) {
            numScheduledCards += items.size();
        }

        return numScheduledCards > 0;
    }


    public List<Item> getScheduledItems(CardFile file) {
        return scheduler.get(file);
    }
}
