package info.opencards.util;

import info.opencards.ui.actions.HelpAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class UIUtils {


    public static JDialog getOwnerDialog(Container awtOwner) {
        while (awtOwner != null && !(awtOwner instanceof JDialog)) {
            awtOwner = awtOwner.getParent();
        }

        return (JDialog) awtOwner;
    }


    /**
     * closes a dialog when escape is pressed either by disposing (<code>dispose == true</code> )it or by making it
     * invisible.
     */
    public static void closeOnEsc(final JDialog dialog, final boolean doDispose) {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (doDispose)
                    dialog.dispose();
                else
                    dialog.setVisible(false);
            }
        };

        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        dialog.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }


    public static void actionOnEsc(final JDialog dialog, final Action action) {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(null);
            }
        };

        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        dialog.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }


    /**
     * @param dialog      The dialog for which f1 as help-trigger should be registered
     * @param helpSection (Optional) The help-section which will be attached to the base-help url
     */
    public static void helpOnF1(final JFrame dialog, String helpSection) {
        KeyStroke fOne = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);

        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(fOne, "F_ONE");
        dialog.getRootPane().getActionMap().put("F_ONE", new HelpAction(helpSection != null ? helpSection : "help"));
    }
}
