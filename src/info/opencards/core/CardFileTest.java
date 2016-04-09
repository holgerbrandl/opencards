package info.opencards.core;

import info.opencards.CardFileBackend;
import info.opencards.util.InvalidCardFileFormatException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class CardFileTest {


    @Test
    public void loadHugeFile() {
        LearnStatusSerializer serializer = CardFileBackend.getBackend().getSerializer();

        CardFile hugeCardFile = new CardFile(new File("testdata/Spanish1700.ppt"));
        FlashCardCollection flashCardCollection = serializer.readFlashcardsFromFile(hugeCardFile);
        System.err.println("done reading " + flashCardCollection.size() + "flashcards");
    }


    @Test
    public void readInvalidFile() {
        LearnStatusSerializer serializer = CardFileBackend.getBackend().getSerializer();
        try {
            CardFile hugeCardFile = new CardFile(new File("testdata/chinese1.odp"));
            serializer.readFlashcardsFromFile(hugeCardFile);

            Assert.fail();
        } catch (OfficeXmlFileException t) {
            System.err.println(t);
        } catch (Throwable t) {
            Assert.fail();
        }
    }


    @Test
    public void readInvalidFile2() {
        LearnStatusSerializer serializer = CardFileBackend.getBackend().getSerializer();

        try {
            CardFile cardFile = new CardFile(new File("testdata/import/flashcards_ansi.txt"));
            serializer.readFlashcardsFromFile(cardFile);

            Assert.fail();
        } catch (InvalidCardFileFormatException t) {
            System.err.println(t);
        } catch (Throwable t) {
            Assert.fail();
        }
    }


    @Test
    public void readPPTXInsteadOfPPT() {
        LearnStatusSerializer serializer = CardFileBackend.getBackend().getSerializer();

        try {
            CardFile cardFile = new CardFile(new File("testdata/pptx_with_ppt_suffix.ppt"));
            serializer.readFlashcardsFromFile(cardFile);

            Assert.fail();
        } catch (OfficeXmlFileException t) {
            System.err.println(t);
        } catch (Throwable t) {
            Assert.fail();
        }
    }

}
