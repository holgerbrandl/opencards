package info.opencards.ui.actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class URLAction extends AbstractAction {


    private final String url;


    public URLAction(String name, Icon icon, String url) {
        super(name, icon);
        this.url = url;
    }


    public URLAction(String name, String url) {
        super(name);

        this.url = url;
    }


    public void actionPerformed(ActionEvent e) {
        // commented out to make compatible with java5
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URL(url).toURI());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        } else {
            JOptionPane.showConfirmDialog(null, "Can not determine the default web browser.\n" + url);
        }
    }
}
