package info.opencards.ui.actions;

import info.opencards.core.CardFile;
import info.opencards.ui.CardFilePropsDialog;
import info.opencards.util.ScaleableIcon;
import info.opencards.util.UIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;


/**
 * Given a cardfile this action opens a dialog which lets the user to reconfigure the item-set-properties of the file.
 *
 * @author Holger Brandl
 */
public class CardFilePropsAction extends AbstractAction {


    private List<CardFile> cardFiles;
    private final JComponent awtOwner;
    private final boolean isLTMProps;


    public CardFilePropsAction(JComponent awtOwner, boolean isLTMProps, String actionName) {
        this.awtOwner = awtOwner;
        this.isLTMProps = isLTMProps;

        putValue(NAME, actionName);

        putValue(SMALL_ICON, new ScaleableIcon("icons/globprops.png"));

//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
    }


    public void actionPerformed(ActionEvent e) {
        assert cardFiles != null && !cardFiles.isEmpty();

        new CardFilePropsDialog(UIUtils.getOwnerDialog(awtOwner), cardFiles, isLTMProps).setVisible(true);
    }


    public void setCardFile(List<CardFile> cardFiles) {
        this.cardFiles = cardFiles;

        setEnabled(cardFiles != null);
    }
}
