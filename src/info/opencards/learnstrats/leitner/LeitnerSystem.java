package info.opencards.learnstrats.leitner;

import info.opencards.Utils;
import info.opencards.core.FlashCard;
import info.opencards.core.Item;
import info.opencards.core.ItemCollection;
import info.opencards.ui.preferences.LeitnerSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An implementation of the leitner flash-card system.
 *
 * @author Holger Brandl
 */
public class LeitnerSystem extends ItemCollection {


    private final List<List<LeitnerItem>> boxes;
    transient private List<LeitnerListener> leitnerListeners;

    private int initBox;

    /**
     * Properties of theis leitner system instance (eg. its leraning configuration)
     */
    public static final int LEARNT = 5;
    public static final int FAILED = -1;

    public static final int NEW = -100;


    public LeitnerSystem(Collection<LeitnerSystem> leitnerSystems) {
        this();

        for (LeitnerSystem ls : leitnerSystems) {
            addAll(ls);
            for (int i = 0; i < ls.getBoxes().size(); i++) {
                if (i < boxes.size())
                    boxes.get(i).addAll(ls.getBox(i));
                else
                    boxes.get(boxes.size() - 1).addAll(ls.getBox(i));
            }
        }
    }


    public LeitnerSystem() {
        this(Utils.getPrefs().getInt(LeitnerSettings.NUM_LEITNER_BOXES, LeitnerSettings.NUM_LEITNER_BOXES_DEFAULT),
                Utils.getPrefs().getInt(LeitnerSettings.INIT_LEITNER_BOXES, LeitnerSettings.INIT_LEITNER_BOXES_DEFAULT) - 1);
    }


    public LeitnerSystem(int numDecks, int initBox) {
        this.initBox = initBox;

        boxes = new ArrayList<List<LeitnerItem>>();
        for (int i = 0; i < numDecks; i++) {
            boxes.add(new ArrayList<LeitnerItem>());
        }
    }


    public void addItem(FlashCard flashCard) {
        LeitnerItem item = new LeitnerItem(flashCard);
        boxes.get(initBox).add(item);

        add(item);

        for (LeitnerListener leitnerListener : getLeitnerListeners()) {
            leitnerListener.newCard(item);
        }
    }


    public void removeItem(FlashCard flashCard) {
        LeitnerItem removeItem = (LeitnerItem) findItem(flashCard);
        List<LeitnerItem> box = getBoxOf(removeItem);

        assert box != null;
        box.remove(removeItem);

        for (LeitnerListener listener : getLeitnerListeners()) {
            listener.removedCard(removeItem);
        }

        super.removeItem(flashCard);
    }


    public void moveCard(LeitnerItem card, int newBox) {
        assert card != null;
        assert newBox >= 0 && newBox <= boxes.size();

        List<LeitnerItem> currentBox = getBoxOf(card);
        if (currentBox == null)
            throw new RuntimeException("can not move unregistered card");

        currentBox.remove(card);
        boxes.get(newBox).add(card);

        for (LeitnerListener listener : getLeitnerListeners()) {
            listener.boxingChanged(card);
        }
    }


    /**
     * Moves a given Item to next higher box (if possible).
     */
    public void moveUp(LeitnerItem card) {
        moveCard(card, Math.min(getBoxIndex(card) + 1, boxes.size() - 1));
    }


    /**
     * Moves a given Item to next lower box (if possible).
     */
    public void moveDown(LeitnerItem card) {
        moveCard(card, Math.max(getBoxIndex(card) - 1, 0));
    }


    public int getBoxIndex(LeitnerItem item) {
        return boxes.indexOf(getBoxOf(item));
    }


    List<LeitnerItem> getBoxOf(LeitnerItem item) {
        for (List<LeitnerItem> box : boxes) {
            if (box.contains(item))
                return box;
        }

        return null;
    }


    /**
     * Labels all cards as unlearnt and puts 'em back into the first box.
     */
    public void reset() {
        List<LeitnerItem> startBox = boxes.get(initBox);

        for (int i = 0; i < boxes.size(); i++) {
            if (i == initBox)
                continue;

            startBox.addAll(boxes.get(i));
            boxes.get(i).clear();
        }

        // tag all cards as unknown
        for (LeitnerItem item : startBox) {
            item.setState(NEW);
        }

        for (LeitnerListener listener : getLeitnerListeners()) {
            listener.boxingChanged(boxes.get(initBox).toArray(new Item[]{}));
        }
    }


    /**
     * Returns all currently registered cards wrapped lists according to their current box-status.
     */
    public List<List<LeitnerItem>> getBoxes() {
        return boxes;
    }


    /**
     * Returns a random card contained in a box between <code>minBox</code> and <code>maxBox</code> (both inclusive).
     */
    public Item getRandomCard(int minBox, int maxBox, List<Item> skipList) {
        List<Item> allCards = getAllCards(minBox, maxBox);

        if (skipList != null) {
            allCards.removeAll(skipList);
        }

        boolean doReweightBoxProbs = Utils.getPrefs().getBoolean(LeitnerSettings.DO_PREFER_UNLEARNT, LeitnerSettings.DO_PREFER_UNLEARNT_DEFAULT);
        if (!doReweightBoxProbs) {
            int nextCardIndex = Utils.getRandGen().nextInt(allCards.size());
            return allCards.get(nextCardIndex);

        } else {
            double weightFactor = Utils.getPrefs().getInt(LeitnerSettings.PREFER_UNLEARNT_AMOUNT, LeitnerSettings.PREFER_UNLEARNT_DEFAULT);
            weightFactor = 0.5 + 0.25 * weightFactor;

            // (1) compute box distributions based on box-fill and remap the distrubtion using rank and user-weight-factor
            double[] boxProbs = new double[numBoxes()];
            double sum = 0;
            for (int i = 0; i < boxProbs.length; i++) {
                boxProbs[i] = (getBox(i).size() / (double) allCards.size()) * (weightFactor * (numBoxes() - i));
                sum += boxProbs[i];
            }

            // normalize distribution
            for (int i = 0; i < boxProbs.length; i++) {
                boxProbs[i] /= sum;
            }

            // (2) created randomized box index based on the mapped distribution
            double r = Utils.getRandGen().nextDouble(), pdfValue = boxProbs[0];
            int randBoxIndex = 0;
            while (pdfValue < r && pdfValue < 0.9999) {
                randBoxIndex++;
                pdfValue += boxProbs[randBoxIndex];
            }

            // (2) now select card in selected box
            List<LeitnerItem> box = getBox(randBoxIndex);
            int nextCardIndex = Utils.getRandGen().nextInt(box.size());
            return box.get(nextCardIndex);
        }
    }


    /**
     * Returns all cards which are part of this <code>LeitnerSystem</code>
     */
    public List<Item> getAllCards() {
        return getAllCards(0, boxes.size() - 1);
    }


    /**
     * Returns all cards which are not in the last box.
     */
    public List<Item> getNotLastBoxCards() {
        return getAllCards(0, boxes.size() - 2);
    }


    /**
     * Selects all cards in a defined range of boxes.
     *
     * @param minBox collect cards starting with box (inclusive)
     * @param maxBox stop collecting cards at this box (inclusive)
     */
    public List<Item> getAllCards(int minBox, int maxBox) {
        assert minBox > 0 && maxBox <= boxes.size() : "invalid collecting range";

        List<Item> allCards = new ArrayList<Item>();

        for (int i = minBox; i <= maxBox; i++) {
            allCards.addAll(boxes.get(i));
        }

        return allCards;
    }


    /**
     * Adds a new listener.
     */
    public void addLearnSessionChangeListener(LeitnerListener l) {
        if (l == null)
            return;

        getLeitnerListeners().add(l);
    }


    /**
     * Removes a listener.
     */
    public void removeLearnSessionChangeListener(LeitnerListener l) {
        if (l == null)
            return;

        getLeitnerListeners().remove(l);
    }


    synchronized List<LeitnerListener> getLeitnerListeners() {
        if (leitnerListeners == null)
            leitnerListeners = new ArrayList<LeitnerListener>();

        return leitnerListeners;
    }


    /**
     * Returns the number of boxes of this leitner system instance.
     */
    public int numBoxes() {
        return boxes.size();
    }


    public List<LeitnerItem> getBox(int boxIndex) {
        assert boxIndex >= 0 && boxIndex < boxes.size();

        return boxes.get(boxIndex);
    }


    /**
     * If the new NumBoxes is smaller as the current one move all cards to the last box.
     */
    public void reconfigure(int newNumBoxes, int newInitBox) {

        while (numBoxes() > newNumBoxes) {
            while (!getBox(newNumBoxes).isEmpty()) {
                moveCard(getBox(newNumBoxes).get(0), newNumBoxes - 1);
            }

            assert getBox(newNumBoxes).isEmpty();
            boxes.remove(newNumBoxes);
        }

        while (numBoxes() < newNumBoxes) {
            boxes.add(new ArrayList<LeitnerItem>());
        }

        initBox = newInitBox - 1;
    }


    public String toString() {
        return "LeitnerSystem: numBoxes=" + numBoxes() + " initalBox=" + initBox + " curSize=" + getAllCards().size();
    }
}
