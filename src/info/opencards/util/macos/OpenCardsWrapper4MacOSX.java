package info.opencards.util.macos;

import info.opencards.OpenCards;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.QuitStrategy;


/**
 * @author Holger Brandl
 */
public class OpenCardsWrapper4MacOSX {


    private final OpenCards oc;


    public static void main(String[] args) {
//        Utils.resetAllSettings();
        new OpenCardsWrapper4MacOSX();
    }


    public OpenCardsWrapper4MacOSX() {
        // set some mac-specific properties
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "OpenCards");

        oc = new OpenCards();

        MacAppHandler macAppHandler = new MacAppHandler(oc);

        // create an instance of the Mac Application class, so i can handle the
        // mac quit event with the Mac ApplicationAdapter
//        Application macApplication = Application.getApplication();

        Desktop macApplication = Desktop.getDesktop();


        // need to enable the preferences option manually
        macApplication.setPreferencesHandler(macAppHandler);
        macApplication.setAboutHandler(macAppHandler);
        macApplication.setQuitHandler(macAppHandler);
        macApplication.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);

        macApplication.addAppEventListener(macAppHandler);

        // display the jframe
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                oc.setVisible(true);
                oc.doAfterSetup();
            }
        });
    }
}
