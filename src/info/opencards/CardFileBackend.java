package info.opencards;

import info.opencards.core.CardFileSerializer;
import info.opencards.core.PresenterProxy;
import info.opencards.pptintegration.PPTImageProxy;
import info.opencards.pptintegration.PPTSerializer;


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


    private final PresenterProxy presProxy;
    private final CardFileSerializer cfSerializer;


    public CardFileBackend(PresenterProxy presenter, CardFileSerializer serializer) {
        this.presProxy = presenter;
        this.cfSerializer = serializer;
    }


    /**
     * Creates an PPT file presentation and serialization backend.
     */
    public static synchronized CardFileBackend getBackend() {
        PresenterProxy presenterProxy = new PPTImageProxy();
        return new CardFileBackend(presenterProxy, new PPTSerializer());
    }


    public PresenterProxy getPresProxy() {
        return presProxy;
    }


    public CardFileSerializer getSerializer() {
        return cfSerializer;
    }
}
