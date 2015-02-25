package info.opencards.ui.actions;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.util.ScaleableIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Performs a deep synchronization for a set of <code>CardFile</code>s.
 *
 * @author Holger Brandl
 */
public class CardFileSyncAction extends AbstractAction {


    private List<CardFile> cardFiles = new ArrayList<CardFile>();
    private JDialog syncDialog;


    public CardFileSyncAction(String actionName) {
        putValue(NAME, actionName);
        putValue(SMALL_ICON, new ScaleableIcon("icons/synchronize.png"));

//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK));
    }


    public void actionPerformed(ActionEvent e) {
        final JProgressBar bar = new JProgressBar(1, 100);
        bar.setIndeterminate(true);
        bar.setStringPainted(true);

//        final JButton cancelLoading = new JButton();
        syncDialog = new JDialog(OpenCards.getInstance());
        syncDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        syncDialog.getContentPane().setLayout(new BorderLayout());
        syncDialog.getContentPane().add(bar, BorderLayout.CENTER);
//        dialog.getContentPane().add(cancelLoading, BorderLayout.SOUTH);

        syncDialog.setLocationRelativeTo(null);
        syncDialog.setTitle(Utils.getRB().getString("CardFileTable.cxtMenu.syncProgress"));

        Dimension dim = new Dimension(350, 50);
        syncDialog.setPreferredSize(dim);
        syncDialog.setSize(dim);
        syncDialog.validate();

        syncDialog.setLocationRelativeTo(null);
        syncDialog.setModal(true);

        new Thread() {

            public void run() {
                for (CardFile curFile : cardFiles) {
                    bar.setString(curFile.getFileLocation().getName() + "");
                    curFile.synchronize();
                    curFile.flush();
                }

                syncDialog.dispose();
            }
        }.start();

        syncDialog.setVisible(true);

        //trigger the update of the chart-table
        OpenCards.getCardSetManager().refreshFileViews();
    }


    /**
     * Returns the dialog of used to visualize the synchronization process. By checking whether the dialog is still
     * visible it is possible to determine whether the synchronization process has been finished. This allows to use
     * this class in also when a blocking behavior is required.
     */
    public JDialog getSyncDialog() {
        return syncDialog;
    }


    public void setCardFiles(List<CardFile> cardFiles) {
        this.cardFiles = cardFiles;

        setEnabled(cardFiles != null);
    }
}