package info.opencards.util;

import info.opencards.Utils;

import javax.swing.*;
import java.awt.*;


public class ScaleableIcon implements Icon {


    private final int w, h;
    private final double scalex, scaley;
    private final Icon base;
    private static final int DEF_ICON_WIDTH = 18;
    private static final int DEF_ICON_HEIGHT = 18;


    public ScaleableIcon(Icon icon, int width, int height) {
        if (icon instanceof ScaleableIcon) base = ((ScaleableIcon) icon).base;
        else
            base = icon;

        w = width;
        h = height;
        scalex = w / (double) base.getIconWidth();
        scaley = h / (double) base.getIconHeight();
    }


    public ScaleableIcon(String iconResourcePath, int width, int height) {
        this(new ImageIcon(Utils.loadResource(iconResourcePath)), width, height);
    }


    public ScaleableIcon(String iconResourcePath) {
        this(new ImageIcon(Utils.loadResource(iconResourcePath)), DEF_ICON_WIDTH, DEF_ICON_HEIGHT);
    }


    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create(x, y, w, h);
        g2.scale(scalex, scaley);
        base.paintIcon(c, g2, 0, 0);
        g2.dispose();
    }


    public int getIconWidth() {
        return w;
    }


    public int getIconHeight() {
        return h;
    }
}
