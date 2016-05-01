package info.opencards.pptintegration;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.*;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;

import javax.swing.*;
import java.util.List;


/**
 * Connects OC to a panel that allows to render powerpoint slides.
 *
 * @author Holger Brandl
 */
public class PPTSlideManager extends AbstractSlideManager {


    private SlideShow slideShow;
    private PPTSlideRenderPanel pptRenderPanel;


    public boolean showCardQuestion(Item item) {
        if (!doOptionalSync(item))
            return false;

        int cardIndex = item.getFlashCard().getCardIndex();
        ReversePolicy cardRevPolicy = item.getFlashCard().getTodaysRevPolicy();

        // note:
        // The random-reverse mode is encoded directly in the flashcard in order to make its policy temporary
        // persistent during the todaysltm-sessions
        Slide curSlide = ((HSLFSlideShow) slideShow).getSlides().get(cardIndex - 1);

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
        Slide curSlide = ((HSLFSlideShow) slideShow).getSlides().get(item.getFlashCard().getCardIndex() - 1);
        getSlidePanel().configure(curSlide, true, true);

        return true;
    }


    private PPTSlideRenderPanel getSlidePanel() {
        return pptRenderPanel;
    }


    public void startFileSession(ItemCollection cardItemCollection) {
    }


    public void openCardFile(CardFile cardFile) {
        slideShow = CardFile.getSlideShow(cardFile);

        // todo we might want to reuse an existing panel here if it's of the same type
        pptRenderPanel = new PPTSlideRenderPanel();
        pptRenderPanel.setBaseSize(slideShow.getPageSize());
        pptRenderPanel.setCardFile(cardFile);

        JPanel renderContainer = OpenCards.getInstance().getLearnPanel().getSlideRenderPanel();

        renderContainer.removeAll();
        renderContainer.add(pptRenderPanel);
        renderContainer.invalidate();

        curCardFile = cardFile;

        OpenCards.getInstance().setTitle("OpenCards: " + cardFile.getFileLocation().getName());
    }


    public void stopFileSession() {
        Utils.log("stopped file session");
        slideShow = null;
        getSlidePanel().setCardFile(null);
    }


    /**
     * Performs a sync operation on the current <code>CardFile</code> if the the item to be shown directs to an
     * XDrawPage which oc-cardID differs from the flashcard associated to the item.
     *
     * @return <code>true</code> if the syncing was sucessful. A non-sucessfull sync is always due to the fact that a
     * slide was deleted.
     */
    private boolean doOptionalSync(Item item) {
        if (item == null || getCurCardFile() == null) {
            Utils.log("Error: Synching not possible because either item (" + item + ") or card-file are null");
            return false;
        }

        if (isItemOutOfSync(item, slideShow))
            getCurCardFile().synchronize();


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
        final List<? extends Slide> allSlides = ((HSLFSlideShow) slideShow).getSlides();

        final int itemCardIndex = item.getFlashCard().getCardIndex();

        return itemCardIndex < 0 || (allSlides.size() < itemCardIndex - 1)
                || allSlides.get(itemCardIndex - 1).getTitle() == null
                || allSlides.get(itemCardIndex - 1).getTitle().hashCode() != item.getFlashCard().getCardID();
    }
}
