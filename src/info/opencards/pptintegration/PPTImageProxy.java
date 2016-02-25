package info.opencards.pptintegration;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.*;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.util.StringUtil;

import java.io.File;
import java.io.IOException;


/**
 * Connects OC to a panel that allows to render powerpoint slides.
 *
 * @author Holger Brandl
 */
public class PPTImageProxy implements PresenterProxy {


    private CardFile curCardFile;
    private SlideShow slideShow;


    public boolean showCardQuestion(Item item) {
        if (!doOptionalSync(item))
            return false;

        int cardIndex = item.getFlashCard().getCardIndex();
        ReversePolicy cardRevPolicy = item.getFlashCard().getTodaysRevPolicy();

        // note:
        // The random-reverse mode is encoded directly in the flashcard in order to make its policy temporary
        // persistent during the todaysltm-sessions
        Slide curSlide = slideShow.getSlides()[cardIndex - 1];

        switch (cardRevPolicy) {
            case NORMAL:
                showCardQuestion(curSlide);
                break;
            case REVERSE:
                showCardContent(curSlide);
                break;
            default:
                throw new RuntimeException("unsupported reverse policy");
        }

        return true;
    }


    private void showCardContent(Slide curSlide) {
        getSlidePanel().configure(curSlide, false, true);
    }


    private void showCardQuestion(Slide curSlide) {
        getSlidePanel().configure(curSlide, true, false);
    }


    public boolean showCompleteCard(Item item) {
        Slide curSlide = slideShow.getSlides()[item.getFlashCard().getCardIndex() - 1];
        getSlidePanel().configure(curSlide, true, true);

        return true;
    }


    private PPTSlideRenderPanel getSlidePanel() {
        return OpenCards.getInstance().getLearnPanel().getSlideRenderPanel();
    }


    public void startFileSession(ItemCollection cardItemCollection) {
    }


    public void openCardFile(CardFile cardFile) {
        Utils.log("started file session");
        slideShow = CardFile.getSlideShow(cardFile);

        PPTSlideRenderPanel rPanel = getSlidePanel();
        rPanel.setBaseSize(slideShow.getPageSize());
        rPanel.setCardFile(cardFile);

        curCardFile = cardFile;

        OpenCards.getInstance().setTitle("OpenCards: " + cardFile.getFileLocation().getName());
    }


    public void stopFileSession() {
        Utils.log("stopped file session");
        slideShow = null;
        getSlidePanel().setCardFile(null);
    }


    public void stopLearnSession() {
        // set the current file to null
        curCardFile = null;

        // conceptually this should be done in stopFileSession but this would cause some flickering when changing between cardsets
        OpenCards.getInstance().resetWindowTitle();
    }


    public CardFile getCurCardFile() {
        return curCardFile;
    }


    /**
     * Performs a sync operation on the current <code>CardFile</code> if the the item to be shown directs to an
     * XDrawPage which oc-cardID differs from the flashcard associated to the item.
     *
     * @return <code>true</code> if the syncing was sucessful. A non-sucessfull sync is always due to the fact that a
     *         slide was deleted.
     */
    private boolean doOptionalSync(Item item) {
        if (item == null || curCardFile == null) {
            Utils.log("Error: Synching not possible because either item (" + item + ") or card-file are null");
            return false;
        }

        if (isItemOutOfSync(item, slideShow))
            curCardFile.synchronize();


//        XDrawPage itemPage = ImpressHelper.getDrawPageByIndex(getXComponent(), item.getFlashCard().getCardIndex());
//        =itemPage == null ? null : DrawPageHelper.readDrawPageCustomProperty(itemPage);
//        if (pageID == null || pageID != item.getFlashCard().getCardID())
//            curCardFile.synchronize();

        // now test whether syncing was sucessfull
        return !isItemOutOfSync(item, slideShow);

//        // now test whether syncing was sucessfull
//        itemPage = ImpressHelper.getDrawPageByIndex(getXComponent(), item.getFlashCard().getCardIndex());
//        pageID = itemPage == null ? null : DrawPageHelper.readDrawPageCustomProperty(itemPage);
//        return pageID != null && pageID == item.getFlashCard().getCardID();
    }


    /**
     * Test if item is in sync with given slide-show instance
     */
    private boolean isItemOutOfSync(Item item, SlideShow slideShow) {
        final Slide[] allSlides = slideShow.getSlides();
        final int itemCardIndex = item.getFlashCard().getCardIndex();

        return itemCardIndex < 0 || (allSlides.length < itemCardIndex - 1)
                || allSlides[itemCardIndex - 1].getTitle() == null
                || allSlides[itemCardIndex - 1].getTitle().hashCode() != item.getFlashCard().getCardID();
    }

//    http://apache-poi.1045710.n5.nabble.com/Extracting-alt-text-from-images-using-HWPF-td5665041.html


    public static void main(String[] args) throws IOException {
        CardFile cardFile = new CardFile(new File("/Users/holger/projects/opencards/oc2/testdata/test_praesi_with_alt_text.ppt"));
        SlideShow slideShow = CardFile.getSlideShow(cardFile);
        Slide slide = slideShow.getSlides()[0];
        Shape[] allShapes = slide.getShapes();
        System.out.println();

        allShapes[2].getEscherOptRecord().getEscherProperties();

        String[] myArgs = new String[]{"/Users/holger/projects/opencards/oc2/testdata/test_praesi_with_alt_text.ppt"};
        PowerPointExtractor.main(myArgs);
        for (EscherProperty property : allShapes[2].getEscherOptRecord().getEscherProperties()) {
            System.out.println(property.getId());
//            allShapes[2].getEscherChild(511)

//            if(EscherProperties.GROUPSHAPE__DESCRIPTION != property.getPropertyNumber())
//                return;

            if (!(property instanceof EscherComplexProperty))
                continue;


            byte[] complexData = ((EscherComplexProperty) property).getComplexData();
            String reformatData = StringUtil.getFromUnicodeLE(complexData);

//            byte[] altTextData = new byte[(complexData.length/2)-1];
//            for(int i = 0; i<altTextData.length; i++) {
//                altTextData[i] = complexData[i * 2];
//            }

            System.out.println("alt name is " + reformatData);

        }
//        for(EscherRecord escherRecord : allShapes[2].getEscherOptRecord){
//            if(escherRecord instanceof EscherOptRecord){
//                EscherOptRecord escherOptRecord =
//                        (EscherOptRecord) escherRecord;
//                for(EscherProperty property :
//                        escherOptRecord.getEscherProperties()){
//                    if(EscherProperties.
//                            GROUPSHAPE__DESCRIPTION == property.getPropertyNumber()){
//                        byte[] complexData =
//                                ((EscherComplexProperty)property).getComplexData();
//                        byte[] altTextData = new byte
//                                [(complexData.length/2)-1];
//                        for(int i=0; i<altTextData.length
//                                ;i++){
//                            altTextData[i] =
//                                    complexData[i*2];
//                        }
//
//                        return new String(altTextData);
//                    }
//                }
//            }
//        }

    }
}
