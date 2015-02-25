package info.opencards.ui.actions;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.learnstrats.leitner.LeitnerSystem;
import info.opencards.ui.lastmin.CramLernSettingsPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.prefs.Preferences;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class ResetLeiterSystemAction extends AbstractAction {


    private final List<CardFile> cardFiles;
    private CramLernSettingsPanel cramLernSettingsPanel;


    public ResetLeiterSystemAction(List<CardFile> cardFiles, CramLernSettingsPanel cramLernSettingsPanel) {
        this.cardFiles = cardFiles;
        this.cramLernSettingsPanel = cramLernSettingsPanel;

        putValue(NAME, Utils.getRB().getString("OpenCardsUI.resetStacksButton.text"));
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
    }


    public void actionPerformed(ActionEvent e) {
        Preferences prefs = Utils.getPrefs();

        String confirmMsg = Utils.getRB().getString("CardFileResetAction.resetReally");
        String title = Utils.getRB().getString("ResetStacksAction.resetReallyTitle");

        int status = JOptionPane.showConfirmDialog(null, confirmMsg, title, JOptionPane.YES_NO_OPTION);

        if (status == JOptionPane.OK_OPTION) {
            for (CardFile cardFile : cardFiles) {
                LeitnerSystem leitnerSystem = cardFile.getFlashCards().getLeitnerItems();
                leitnerSystem.reset();


                // this is not really necessary but should make the ui a little bit more snappy by awoiding to block
                // the awt-thread while serializing the reseted flashcard set to the odp-meta-data field
//            new Thread() {
//
//                public void run() {
                cardFile.flush();
//                }
//            }.start();
            }

            cramLernSettingsPanel.configure(cardFiles);

        }
    }
}
