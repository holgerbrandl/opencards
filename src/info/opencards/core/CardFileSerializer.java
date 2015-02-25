package info.opencards.core;

/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public interface CardFileSerializer {


    /**
     * Returns the flashcards of the current cardFile.
     */
    public FlashCardCollection readFlashcardsFromFile(CardFile cardFile);


    /**
     * Writes a meta-data snippet to the given card-file.
     */
    public void serializeFileCards(CardFile cardFile, FlashCardCollection fileItems);


    /**
     * Reads a meta-data snippet from the given card-file.
     */
    public FlashCardCollection deserializeFileCards(CardFile cardFile);

}
