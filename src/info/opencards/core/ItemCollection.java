package info.opencards.core;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A collection of item which should be learned. Typically a card-file contains several <code>ItemCollection</code>s:
 * One for each learning mode (ltm, lastmin).
 *
 * @author Holger Brandl
 */
public abstract class ItemCollection extends ArrayList<Item> {


    protected HashMap<String, Object> props = new HashMap<String, Object>();


    public Object getProperty(String propName, Object defaultProperty) {
        if (props.containsKey(propName))
            return props.get(propName);
        else
            return defaultProperty;
    }


    public Object setProperty(String propName, Object propValue) {
        return getProps().put(propName, propValue);
    }


    public HashMap<String, Object> getProps() {
        return props;
    }


    public abstract void addItem(FlashCard flashCard);


    public void removeItem(FlashCard flashCard) {
        Item removeItem = findItem(flashCard);
        if (removeItem != null)
            remove(removeItem);
    }


    public Item findItem(FlashCard flashCard) {
        for (Item item : this) {
            if (item.getFlashCard().equals(flashCard))
                return item;
        }

        return null;
    }
}
