package info.opencards.util.macos;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.ui.actions.AboutAction;
import info.opencards.ui.actions.SettingsAction;

import javax.swing.*;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class OpenCardsWrapper4MacOSX {


    private final OpenCards oc;


    public static void main(String[] args) {
        Utils.resetAllSettings();
        new OpenCardsWrapper4MacOSX();
    }


    public OpenCardsWrapper4MacOSX() {
        // set some mac-specific properties
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "OpenCards");

        oc = new OpenCards();

        // create an instance of the Mac Application class, so i can handle the
        // mac quit event with the Mac ApplicationAdapter
        Application macApplication = Application.getApplication();
        MyApplicationAdapter macAdapter = new MyApplicationAdapter(oc);
        macApplication.addApplicationListener(macAdapter);

        // need to enable the preferences option manually
        macApplication.setEnabledPreferencesMenu(true);
        macApplication.setEnabledAboutMenu(true);

        // display the jframe
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                oc.setVisible(true);
                oc.doAfterSetup();
            }
        });
    }
}


class MyApplicationAdapter extends ApplicationAdapter {


    private final OpenCards oc;


    public MyApplicationAdapter(OpenCards handler) {
        this.oc = handler;
    }


    public void handleQuit(ApplicationEvent e) {
        OpenCards.saveCatTreeBeforeQuit(oc);
        System.exit(0);
    }


    public void handleAbout(ApplicationEvent e) {
        // tell the system we're handling this, so it won't display
        // the default system "about" dialog after ours is shown.
        e.setHandled(true);
        new AboutAction(oc).actionPerformed(null);
    }


    public void handlePreferences(ApplicationEvent e) {
        new SettingsAction(oc).actionPerformed(null);
    }


    public void handleReOpenApplication(ApplicationEvent event) {
        oc.setVisible(true);
    }
}
