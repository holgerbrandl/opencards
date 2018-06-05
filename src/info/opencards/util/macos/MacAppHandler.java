package info.opencards.util.macos;

import info.opencards.OpenCards;
import info.opencards.ui.actions.AboutAction;
import info.opencards.ui.actions.SettingsAction;

import java.awt.desktop.*;


/**
 * https://developer.apple.com/library/mac/releasenotes/java/javasnowleopardupdate3leopardupdate8rn/NewandNoteworthy/NewandNoteworthy.html
 */


public class MacAppHandler implements AboutHandler, PreferencesHandler, QuitHandler, SystemEventListener {

    private final OpenCards oc;


    public MacAppHandler(OpenCards oc) {
        this.oc = oc;
    }



    @Override
    public void handleAbout(AboutEvent e) {
        new AboutAction(oc).actionPerformed(null);
    }


    @Override
    public void handlePreferences(PreferencesEvent e) {
        new SettingsAction(oc).actionPerformed(null);


    }


    @Override
    public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
        OpenCards.saveCatTreeBeforeQuit(oc);
        System.exit(0);
    }
}
