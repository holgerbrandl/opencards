package info.opencards.core;

import java.util.HashMap;


/**
 * A piece of knowlege to be learned. Implementations will depend on the specific flash-card
 * presentation/creation-backend and are referenced by a small set of utitlity methods only.
 *
 * @author Holger Brandl
 */
public class Item implements Cloneable {


    /**
     * The card to which this item corresponds to.
     */
    private final FlashCard parentCard;


    private HashMap<String, Object> itemProps = new HashMap<String, Object>();


    public Item(FlashCard parentCard) {
        this.parentCard = parentCard;
    }


    public Object clone() throws CloneNotSupportedException {

        return super.clone();
    }


    public String toString() {
        return "item for '" + parentCard.toString() + "'";
    }


    public FlashCard getFlashCard() {
        return parentCard;
    }


    public void reset() {
    }


    /**
     * Returns the property-value for the specified <code>propName</code> or the given <code>defaultProperty </code> if
     * the <code>propName</code> was not a property-key. In the latter case the properties are not updated.
     */
    public Object getProperty(String propName, Object defaultProperty) {
        if (getItemProps().containsKey(propName))
            return getItemProps().get(propName);
        else
            return defaultProperty;
    }


    public Object setProperty(String propName, Object propValue) {
        return getItemProps().put(propName, propValue);
    }


    public HashMap<String, Object> getItemProps() {
        if (itemProps == null)  // necessary to ensure backward compatibility to ( remove OC 1.0 )
            itemProps = new HashMap<String, Object>();

        return itemProps;
    }
}
