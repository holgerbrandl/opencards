package info.opencards;

import info.opencards.core.CardFile;
import info.opencards.core.LearnStatusSerializer;
import info.opencards.core.SlideManager;
import info.opencards.md.MdSlideManager;
import info.opencards.pptintegration.PPTSerializer;
import info.opencards.pptintegration.PPTSlideManager;

import java.io.File;


/**
 * A kind of hack which publishes the current OpenCards-components to interesed modules.
 * <p/>
 * In order to make the backend-API as lean as possible CardFileBackend only returns interfaces. It's idea is to provide
 * an abstract layer which provides the concrete presentation, serialization and learning implementations to the core
 * infrastructure. This allows to use OC in a variety of contexts (e.g. PPT files or Mock-setups for testing)
 *
 * @author Holger Brandl
 */
public class CardFileBackend {


    private final SlideManager presProxy;
    private final LearnStatusSerializer cfSerializer;


    public CardFileBackend(SlideManager presenter, LearnStatusSerializer serializer) {
        this.presProxy = presenter;
        this.cfSerializer = serializer;
    }


    /**
     * Creates an PPT file presentation and serialization backend.
     */
    public static synchronized CardFileBackend getBackend() {
        return new CardFileBackend(null, new PPTSerializer());
    }


    public static boolean hasSupportedExtension(File file) {
        return file.getName().endsWith(".ppt") || file.getName().endsWith(".md");
    }


    public SlideManager getSlideManager(CardFile cardFile) {
        if (presProxy != null) return presProxy; // to support mocking

        if (cardFile.getFileLocation().getName().endsWith(".ppt")) return new PPTSlideManager();
        if (cardFile.getFileLocation().getName().endsWith(".md")) return new MdSlideManager();

        throw new RuntimeException("Invalid Cardfile Extension: " + cardFile.getFileLocation());
    }


    public LearnStatusSerializer getSerializer() {
        return cfSerializer;
    }
}
