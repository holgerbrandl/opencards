package info.opencards.learnstrats.leitner;

import info.opencards.core.FlashCard;
import info.opencards.core.Item;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class LeitnerItem extends Item {


    private int itemState;


    public LeitnerItem(FlashCard parentCard) {
        super(parentCard);

        itemState = LeitnerSystem.NEW;
    }


    public int getState() {
        return itemState;
    }


    public void setState(int state) {
        itemState = state;
    }
}
