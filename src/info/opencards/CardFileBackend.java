package info.opencards;

import info.opencards.core.CardFile;
import info.opencards.core.LearnStatusSerializer;
import info.opencards.core.SlideManager;
import info.opencards.md.MdSlideManager;
import info.opencards.pptintegration.PPTSerializer;
import info.opencards.pptintegration.PPTSlideManager;


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


    public SlideManager getSlideManager(CardFile cardFile) {
        if (presProxy != null) return presProxy; // to support mocking

        return cardFile.getFileLocation().getName().endsWith(".ppt") ? new PPTSlideManager() : new MdSlideManager();
    }


    public LearnStatusSerializer getSerializer() {
        return cfSerializer;
    }
}
