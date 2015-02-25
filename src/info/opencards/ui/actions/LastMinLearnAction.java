package info.opencards.ui.actions;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.ui.CardFileSelectionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class LastMinLearnAction extends AbstractAction implements CardFileSelectionListener {


    private CardFile cardFile;

    private java.util.List<CardFile> curSelCardFiles;


    public LastMinLearnAction() {

        putValue(NAME, Utils.getRB().getString("OpenCardsUI.learnCardsButton.text"));
//        putValue(SMALL_ICON, new ScaleableIcon("icons/5vorUhr.png"));

//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
    }


    public void actionPerformed(ActionEvent e) {
        actionPerformed();
    }


    void actionPerformed() {
        OpenCards.showLastMinConfigView(curSelCardFiles);

//        if (cardFile.getFlashCards().size() == 0) {
//            showOcSlideCompatibilityDialog(cramLearnPanel, Utils.getRB().getString("LastMinLearnAction.warnnoslides.title"));
//        }
    }


    public void cardFileSelectionChanged(List<CardFile> curSelCardFiles) {
        this.curSelCardFiles = curSelCardFiles;
        if (curSelCardFiles.size() > 0)
            cardFile = curSelCardFiles.get(0);
        else
            cardFile = null;
    }


    public void setCardFile(CardFile cardFile) {
        this.cardFile = cardFile;

        setEnabled(cardFile != null);
    }
}
