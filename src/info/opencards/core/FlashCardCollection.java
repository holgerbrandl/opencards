package info.opencards.core;

import info.opencards.Utils;
import info.opencards.learnstrats.leitner.LeitnerItem;
import info.opencards.learnstrats.leitner.LeitnerSystem;
import info.opencards.learnstrats.ltm.LTMCollection;
import info.opencards.learnstrats.ltm.LTMItem;
import info.opencards.ui.preferences.LeitnerSettings;

import java.util.*;


/**
 * A collection of flashcard along with some properties. Each flashcard may contain learn-items of differnt kind
 * according to different learn-modes applied to this card.
 * <p/>
 * Instances of this class will be serialized to the flash-card-file. It is therefore necessary that it does not contain
 * any non-transient references to the outer OpenCards/OpenOffice-ecosystem.
 *
 * @author Holger Brandl
 */
public class FlashCardCollection extends ArrayList<FlashCard> {


    public static final String REVERSE_POLICY = "reversePolicy";

    private HashMap<String, Object> properties = new HashMap<String, Object>();

    private final Map<Class<? extends Item>, ItemCollection> itColls = new HashMap<Class<? extends Item>, ItemCollection>();

    /**
     * The sync state of all <code>ItemCollection</code>s maintained by this flashcard collection.
     */
    private final Map<Class<? extends Item>, Boolean> syncStates = new HashMap<Class<? extends Item>, Boolean>();


    public ItemCollection getItems(Class<? extends Item> itemType) {

        ItemCollection items = itColls.get(itemType);
        if (items == null) {
            if (itemType.equals(LTMItem.class)) {
                items = new LTMCollection();

            } else if (itemType.equals(LeitnerItem.class)) {
                int numBoxes = Utils.getPrefs().getInt(LeitnerSettings.NUM_LEITNER_BOXES, LeitnerSettings.NUM_LEITNER_BOXES_DEFAULT);
                int initBox = Utils.getPrefs().getInt(LeitnerSettings.INIT_LEITNER_BOXES, LeitnerSettings.INIT_LEITNER_BOXES_DEFAULT);

                items = new LeitnerSystem(numBoxes, initBox - 1);
            }

            itColls.put(itemType, items);
            syncStates.put(itemType, false);
        }

        ItemCollection itemCollection = itColls.get(itemType);

        //update the item collection to contain all current flashcards
        if (!syncStates.containsKey(itemType))
            syncStates.put(itemType, false);

        if (!syncStates.get(itemType)) {
            syncStates.put(itemType, true);

            for (FlashCard flashCard : this) {
                if (itemCollection.findItem(flashCard) == null) {
                    itemCollection.addItem(flashCard);
                }
            }
        }

        return itemCollection;
    }


    /**
     * @throw IllegalArgumentException if this collection already contains a flashcard with the same ID
     */
    public boolean add(FlashCard flashCard) {
//        if (getByID(flashCard.getCardID()) != null)
//            throw new IllegalArgumentException("flashcard addition failed because flashcard ID is already in use.");
        if (getByID(flashCard.getCardID()) != null)
            return false;

        for (Class<? extends Item> aClass : itColls.keySet()) {
            itColls.get(aClass).addItem(flashCard);
        }

        flagUnsyncAl();

        // set the common reversing policy for the new flashcard
        ReversePolicy filePolicy = (ReversePolicy) getProperty(FlashCardCollection.REVERSE_POLICY, ReversePolicy.NORMAL);
        flashCard.setRevPolicy(filePolicy);

        return super.add(flashCard);
    }


    public FlashCard remove(int index) {
        FlashCard removeCard = get(index);
        remove(removeCard);

        return removeCard;
    }


    public boolean remove(FlashCard flashCard) {
        ArrayList<Class<? extends Item>> arrayList = new ArrayList<Class<? extends Item>>(itColls.keySet());
        for (int i = 0; i < arrayList.size(); i++) { // for each is not applicable here because of remoove-operation
            Class<? extends Item> aClass = arrayList.get(i);
            itColls.get(aClass).removeItem(flashCard);
        }

        flagUnsyncAl();
        return super.remove(flashCard);
    }


    private void flagUnsyncAl() {
        for (Class<? extends Item> aClass : syncStates.keySet()) {
            syncStates.put(aClass, false);
        }
    }


    /**
     * Returns all items which belong to a certain flashcard.
     *
     * @return An empty collection if the argument card is not part of
     */

    public Collection<Item> getCardItems(FlashCard card) {
        assert contains(card) : "card not contained in collection";

        Collection<Item> cardItems = new ArrayList<Item>();
        for (ItemCollection itemCollection : itColls.values()) {
            Item cardItem = itemCollection.findItem(card);
            if (cardItem != null)
                cardItems.add(cardItem);
        }

        return cardItems;
    }


    /**
     * A simple shortcut for <code>(LTMCollection) getItems(LTMItem.class)</code>.
     */
    public LTMCollection getLTMItems() {
        return (LTMCollection) getItems(LTMItem.class);
    }


    /**
     * A simple shortcut for <code>(LeitnerSystem) getItems(LeitnerItem.class)</code>.
     */
    public LeitnerSystem getLeitnerItems() {
        return (LeitnerSystem) getItems(LeitnerItem.class);
    }


    public Object getProperty(String propName, Object defaultProperty) {
        if (getProps().containsKey(propName))
            return getProps().get(propName);
        else
            return defaultProperty;
    }


    public Object setProperty(String propName, Object propValue) {
        return getProps().put(propName, propValue);
    }


    /**
     * Returns the property-value for the specified <code>propName</code> or the given <code>defaultProperty </code> if
     * the <code>propName</code> was not a property-key. In the latter case the properties are not updated.
     */
    public HashMap<String, Object> getProps() {
        if (properties == null)
            properties = new HashMap<String, Object>();

        return properties;
    }


    public FlashCard getByIndex(int searchIndex) {
        for (FlashCard card : this) {
            if (card.getCardIndex() == searchIndex) {
                return card;
            }
        }

        return null;
    }


    public List<FlashCard> getByName(String searchTerm) {
        List<FlashCard> matchingCards = new ArrayList<FlashCard>();
        for (FlashCard card : this) {
            if (card.getCardTitle().contains(searchTerm)) {
                matchingCards.add(card);
            }
        }

        return matchingCards;
    }


    public Object clone() {
        FlashCardCollection cloneFCC = (FlashCardCollection) super.clone();
        cloneFCC.clear();

        for (FlashCard card : this) {
            try {
                cloneFCC.add((FlashCard) card.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        return cloneFCC;
    }


    FlashCard getByID(long slideID) {
        for (FlashCard card : this) {
            if (card.getCardID() == slideID)
                return card;
        }

        return null;
    }
}

///** An API which allows to process change events of a <code>FlashCardCollection</code>. */
//interface FlashCardCollChangeListner {
//
//    public void flashCardCollChanged();
//}

