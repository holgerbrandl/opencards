package info.opencards.learnstrats.leitner;

import info.opencards.core.Item;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public interface LeitnerListener {


    public void newCard(Item flashcard);


    public void boxingChanged(Item... movedCards);


    public void removedCard(Item item);
}
