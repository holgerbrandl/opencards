package info.opencards.core;

import info.opencards.Utils;


/**
 * A single card in a card set. The card will keep a list of items of differnt learn-modes and provides mean for
 * synchronization with existing card-sets.
 *
 * @author Holger Brandl
 */
@SuppressWarnings({"ALL"})
public class FlashCard implements Cloneable {


    private String cardTitle;

    /**
     * This is assumed to be a unique id of the item relative to the flashcard-collection it belongs to.
     */
    private long cardID;

    private int cardIndex;

    private ReversePolicy revPolicy;


    public FlashCard(long cardID, String cardTitle, int cardIndex) {
        setCardTitle(cardTitle);
        this.cardID = cardID;
        this.cardIndex = cardIndex;
    }


    public void setRevPolicy(ReversePolicy revPolicy) {
        this.revPolicy = revPolicy;
    }


    /**
     * Returns the basic question mode policy without any session specific randomization applied.
     */
    public ReversePolicy getRevPolicy() {

        // if the reversing policy for a flashcard should be null, we use the default policy
        if (revPolicy == null) {
            Utils.log("fixed missing question mode for flashcard '" + this + "'");
            revPolicy = ReversePolicy.getDefault();
        }

        return revPolicy;
    }


    private transient ReversePolicy rndRevPolicy;


    /**
     * Returns the quesetion mode of this flashcard. Normally this will be the same as obtained by getRevPolicy. But if
     * this card uses a random-side question mode, a random card side (either slide title or its content) will be
     * selected, which will be kept for this session (or until resetRndPolicy() is called)
     */
    public ReversePolicy getTodaysRevPolicy() {
        ReversePolicy curQuestionMode = getRevPolicy();

        if (curQuestionMode.equals(ReversePolicy.RANDOM_REVERSE)) {
            if (rndRevPolicy == null)
                rndRevPolicy = Utils.getRandGen().nextDouble() < 0.5 ? ReversePolicy.NORMAL : ReversePolicy.REVERSE;

            return rndRevPolicy;
        }

        return curQuestionMode;
    }


    public void resetRndRevPolicy() {
        rndRevPolicy = null;
    }


    public String toString() {
        return getCardTitle() + " " + getCardIndex() + " id:" + getCardID();
    }


    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }


    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }


    public String getCardTitle() {
        if (cardTitle == null)
            return "";

        return cardTitle;
    }


    public int getCardIndex() {
        return cardIndex;
    }


    public long getCardID() {
        return cardID;
    }


    public int hashCode() {
        int result;
        result = (cardTitle != null ? cardTitle.hashCode() : 0);
        result = 31 * result + (int) (cardID ^ (cardID >>> 32));
        result = 31 * result + cardIndex;

        return result;
    }


    /**
     * Tests whether two falhcards (else if obj not instance of <code>FlashCards</code>) are equals based on the
     * cardID.
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FlashCard))
            return false;

        FlashCard compCard = (FlashCard) obj;
        return cardID == compCard.getCardID();
    }


    public Object clone() throws CloneNotSupportedException {
        FlashCard cloneCard = (FlashCard) super.clone();

        cloneCard.setCardTitle(cardTitle);
        cloneCard.cardID = cardID;
        cloneCard.cardIndex = cardIndex;

        return cloneCard;
    }
}
