package info.opencards.util.macos;

import com.apple.eawt.*;
import info.opencards.OpenCards;
import info.opencards.ui.actions.AboutAction;
import info.opencards.ui.actions.SettingsAction;


/**
 * https://developer.apple.com/library/mac/releasenotes/java/javasnowleopardupdate3leopardupdate8rn/NewandNoteworthy/NewandNoteworthy.html
 */


public class MacAppHandler implements AboutHandler, PreferencesHandler, QuitHandler, AppReOpenedListener {

    private final OpenCards oc;


    public MacAppHandler(OpenCards oc) {
        this.oc = oc;
    }


    @Override
    public void handleAbout(AppEvent.AboutEvent aboutEvent) {
        new AboutAction(oc).actionPerformed(null);
    }


    @Override
    public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
        new SettingsAction(oc).actionPerformed(null);
    }


    @Override
    public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
        OpenCards.saveCatTreeBeforeQuit(oc);
        System.exit(0);
    }


    @Override
    public void appReOpened(AppEvent.AppReOpenedEvent appReOpenedEvent) {
        oc.setVisible(true);
    }
}
