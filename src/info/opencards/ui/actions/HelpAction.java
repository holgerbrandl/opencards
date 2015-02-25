package info.opencards.ui.actions;

import info.opencards.Utils;
import info.opencards.util.AboutDialog;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class HelpAction extends URLAction {


    public HelpAction() {
        this("help");
    }


    public HelpAction(String helpSection) {
        this(Utils.getRB().getString("General.help"), helpSection);
    }


    private HelpAction(String actionName, String helpItem) {
        super(actionName, AboutDialog.OC_WEBSITE + helpItem);
    }
}
