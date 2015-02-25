package info.opencards.ui.actions;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.learnstrats.ltm.ScheduleUtils;
import info.opencards.ui.table.CardSetTable;
import info.opencards.ui.table.CardTableModel;
import info.opencards.util.ScaleableIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Resets the learning state of a card-file without changing the configuration of the learning settings (question-mode
 * etc.)
 *
 * @author Holger Brandl
 */
public class CardFileResetAction extends AbstractAction {


    private List<CardFile> cardFiles = new ArrayList<CardFile>();
    private CardSetTable cardSetTable;
    private JDialog awtOwner;
    private final List<Class<? extends Item>> resetTypes;


    public CardFileResetAction(List<Class<? extends Item>> resetTypes) {
        this.resetTypes = resetTypes;
    }


    public CardFileResetAction(JDialog awtOwner, CardSetTable cardTable, List<Class<? extends Item>> resetTypes, String actionName) {
        this.awtOwner = awtOwner;
        this.cardSetTable = cardTable;
        this.resetTypes = resetTypes;

        putValue(NAME, actionName);
        putValue(SMALL_ICON, new ScaleableIcon("icons/reset.png"));

//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
    }


    public void actionPerformed(ActionEvent e) {
        String confirmMsg = Utils.getRB().getString("CardFileResetAction.resetReally");
        String title = Utils.getRB().getString("ResetStacksAction.resetReallyTitle");

        int status = JOptionPane.showConfirmDialog(awtOwner, confirmMsg, title, JOptionPane.YES_NO_OPTION);

        if (status == JOptionPane.OK_OPTION) {
            // iterate over all cardfiles to be reseted
            for (CardFile cardFile : cardFiles) {
                resetCardFile(cardFile);
            }

            ((CardTableModel) cardSetTable.getModel()).fireTableDataChanged();

            // we also reset the number of new cards learn't today
            ScheduleUtils.setNumLearntToday(0);

            //trigger the update of the chart-table
            OpenCards.getCardSetManager().refreshFileViews();
        }
    }


    void resetCardFile(CardFile cardFile) {
        // iterate over all item-types  to be reseted
        for (Class<? extends Item> resetType : resetTypes) {

            // perform reset by reinitializing all items
            for (Item item : cardFile.getFlashCards().getItems(resetType)) {
                item.reset();
            }
        }

        cardFile.flush();
    }


    public void setCardFiles(List<CardFile> cardFiles) {
        this.cardFiles = cardFiles;

        setEnabled(cardFiles != null);
    }
}
