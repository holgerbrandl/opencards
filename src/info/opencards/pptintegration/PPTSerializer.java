package info.opencards.pptintegration;

import com.thoughtworks.xstream.XStream;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.CardFileSerializer;
import info.opencards.core.FlashCard;
import info.opencards.core.FlashCardCollection;
import info.opencards.util.InvalidCardFileFormatException;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import java.io.*;


/**
 * Allows to load ppt-files and create flashcard collections from the slides. These collections are serialized along
 * with ppt files in a hidden folder named .opencards
 *
 * @author Holger Brandl
 */
public class PPTSerializer implements CardFileSerializer {


    public FlashCardCollection readFlashcardsFromFile(CardFile cardFile) {
        Utils.log("extracting  flashcards from file '" + cardFile + "'...");

        FlashCardCollection fc = new FlashCardCollection();
        try {
            FileInputStream is = new FileInputStream(cardFile.getFileLocation());
            SlideShow ppt = new SlideShow(is);

            for (Slide xslfSlide : ppt.getSlides()) {
                String slideTitle = xslfSlide.getTitle();
                if (slideTitle == null)
                    continue;

                // old OC1.x approach to create a unique card-id
//                int cardID = Utils.getRandGen().nextInt(Integer.MAX_VALUE);

                fc.add(new FlashCard(slideTitle.hashCode(), slideTitle, xslfSlide.getSlideNumber()));
            }


        } catch (IOException e) {
            // rephrase IO problem into something more specific
            throw new InvalidCardFileFormatException();
        }

        return fc;
    }


    public void serializeFileCards(CardFile cardFile, FlashCardCollection fileItems) {
        File metaDataFile = getMetaDataFile(cardFile);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(metaDataFile));
//            String serialItCo = StringCompressUtils.compress2(new XStream().toXML(fileItems));

            out.write(new XStream().toXML(fileItems));
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public FlashCardCollection deserializeFileCards(CardFile cardFile) {
        File metaDataFile = getMetaDataFile(cardFile);

        if (metaDataFile == null || !metaDataFile.isFile())
            return null;

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(metaDataFile));
            return (FlashCardCollection) new XStream().fromXML(bis);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }


    public static File getMetaDataFile(CardFile cardFile) {
        File directory = cardFile.getFileLocation().getParentFile();
        if (!directory.isDirectory()) {
            return null;
        }

        File metadataDirectory = Utils.getOrCreateHiddenOCDirectory(directory);

        return new File(metadataDirectory, cardFile.getFileLocation().getName() + ".xml");
    }
}
