package info.opencards.ui;

import com.thoughtworks.xstream.XStream;
import info.opencards.Utils;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;


/**
 * Manages persistent window layouts over different sessions.
 *
 * @author Holger Brandl
 */
public class LayoutRestorer extends ComponentAdapter {


    private static LayoutRestorer instance;

    private final Map<Component, String> windows2Names = new HashMap<Component, String>();


    /**
     * Returns a new ConfigurationManager or a previously created one.
     */
    public synchronized static LayoutRestorer getInstance() {
        if (instance == null)
            instance = new LayoutRestorer();

        return instance;
    }


    /**
     * Request the window-layout of a window called <code>windowName</code>.
     */
    public Rectangle getBounds(String windowName, Component comp, Rectangle rect) {
        assert windowName != null;
        Rectangle winBounds = null;

        String xmlBound = Utils.getPrefs().get(windowName, null);
        if (xmlBound != null)
            winBounds = (Rectangle) new XStream().fromXML(xmlBound);

        if (winBounds == null || isOutOfScreen(winBounds)) {
            if (rect == null)
                rect = new Rectangle(100, 100, 200, 150);

            // create new instance, put it into the map an serialize it
            winBounds = new Rectangle(rect);
            updateLayout(windowName, winBounds);
        }

        assert comp != null;
        comp.addComponentListener(this);
        windows2Names.put(comp, windowName);

        return winBounds;
    }


    private boolean isOutOfScreen(Rectangle winBounds) {
        boolean isInScreen = false;
        for (GraphicsDevice graphDevice : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            if (graphDevice.getDefaultConfiguration().getBounds().contains(winBounds))
                isInScreen = true;

        }

        return !isInScreen;
    }


    private void updateLayout(String windowName, Rectangle winBounds) {
        Preferences preferences = Utils.getPrefs();
        preferences.put(windowName, new XStream().toXML(winBounds));

//        Utils.flushPrefs();
    }


    public void componentResized(ComponentEvent e) {
        compLayoutChanged(e);
    }


    public void componentMoved(ComponentEvent e) {
        compLayoutChanged(e);
    }


    private void compLayoutChanged(ComponentEvent e) {
        Component comp = e.getComponent();
        String curWinName = windows2Names.get(comp);

        Rectangle curBounds = comp.getBounds();
        updateLayout(curWinName, curBounds);
    }
}
