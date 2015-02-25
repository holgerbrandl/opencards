package info.opencards.ui.table;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;
import info.opencards.learnstrats.ltm.LTMItem;
import info.opencards.ui.actions.CardFilePropsAction;
import info.opencards.ui.actions.CardFileResetAction;
import info.opencards.ui.actions.CardFileSyncAction;
import info.opencards.util.ScaleableIcon;
import info.opencards.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class FilePopUp extends MouseAdapter {


    private CardSetTable parent;

    private JPopupMenu menu;
    private JMenu remMenu;
    private CardFilePropsAction propsAction;
    private CardFileResetAction resetAction;

    private CardFileSyncAction syncAction;


    public FilePopUp(CardSetTable parent) {
        assert parent != null;
        this.parent = parent;
        menu = new JPopupMenu();

        //setup the context menu
        ResourceBundle rb = Utils.getRB();
        propsAction = new CardFilePropsAction(parent, true, rb.getString("CardFileTable.cxtMenu.configureCards"));
        menu.add(new JMenuItem(propsAction));

        String syncActionName = rb.getString("CardFileTable.cxtMenu.syncCards");
        syncAction = new CardFileSyncAction(syncActionName);
        menu.add(new JMenuItem(syncAction));

        String resetActionName = rb.getString("OpenCardsUI.resetStacksButton.text");
        resetAction = new CardFileResetAction(UIUtils.getOwnerDialog(parent), parent, Arrays.<Class<? extends Item>>asList(LTMItem.class), resetActionName);
        menu.add(new JMenuItem(resetAction));

        menu.add(new JSeparator());

        remMenu = new JMenu(rb.getString("CardFileTable.cxtMenu.removeFromCategory"));
        remMenu.setIcon(new ScaleableIcon("icons/category_remove.png"));

        menu.add(remMenu);
    }


    public void mouseReleased(java.awt.event.MouseEvent e) {
        List<CardFile> selectedCardFiles = getSelectedCardFiles((CardSetTable) e.getSource());

        if (!e.isPopupTrigger()) {
            if (e.getClickCount() == 2 && selectedCardFiles.size() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                try {
                    Desktop.getDesktop().open(selectedCardFiles.get(0).getFileLocation());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            return;
        }


        propsAction.setCardFile(selectedCardFiles.isEmpty() ? null : selectedCardFiles);
        resetAction.setCardFiles(selectedCardFiles.isEmpty() ? null : selectedCardFiles);
        syncAction.setCardFiles(selectedCardFiles.isEmpty() ? null : selectedCardFiles);

        //add only common categories of the current (multiple) selection. (which is a intersection of all file-categories)
        List<Category> commonFileCats = new ArrayList<Category>(getCurCommonSelectionCategories(selectedCardFiles));

        //remove all categories which are not selected yet
        List<Category> selectedCats = ((CardTableModel) parent.getModel()).getCurCatgories();
        Set<Category> selIncChildCats = new HashSet<Category>();

        for (Category selectedCat : selectedCats) {
            selIncChildCats.addAll(CategoryUtils.recursiveCatCollect(selectedCat));
        }

        for (int i = 0; i < commonFileCats.size(); i++) {
            if (!selIncChildCats.contains(commonFileCats.get(i))) {
                commonFileCats.remove(commonFileCats.get(i));
                i--;
            }
        }

        remMenu.setEnabled(!commonFileCats.isEmpty());

        // create the temporary menu actions
        remMenu.removeAll();
        for (Category curCat : commonFileCats) {
            remMenu.add(new JMenuItem(new RemoveFromCategoryAction(UIUtils.getOwnerDialog(parent), curCat, selectedCardFiles)));
        }

        menu.show(parent, e.getX(), e.getY());  //Have the tree display the pop up menu here
    }


    private static List<CardFile> getSelectedCardFiles(CardSetTable cardSetTable) {
        int[] ints = cardSetTable.getSelectedRows();

        List<CardFile> selectedFiles = new ArrayList<CardFile>();
        for (int rowIndex : ints) {
            selectedFiles.add(cardSetTable.getSortedRowFile(rowIndex));
        }

        return selectedFiles;
    }


    private Set<Category> getCurCommonSelectionCategories(List<CardFile> curFiles) {
        Set<Category> commonCats = new HashSet<Category>();

        for (CardFile curFile : curFiles) {
            commonCats.addAll(new HashSet<Category>(curFile.belongsTo()));
        }

        for (CardFile curFile : curFiles) {
            commonCats.retainAll(new HashSet<Category>(curFile.belongsTo()));
        }

        return commonCats;
    }


    public void mouseClicked(MouseEvent e) {
//        if (SwingUtilities.isRightMouseButton(e)) {
//            return;
//        }
    }


    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        mouseReleased(e);
    }
}


class RemoveFromCategoryAction extends AbstractAction {


    private final Dialog ownerDialog;
    private final Category removeCategory;
    private final List<CardFile> selectedCardFiles;


    public RemoveFromCategoryAction(Dialog ownerDialog, Category removeCategory, List<CardFile> selectedCardFiles) {
        this.ownerDialog = ownerDialog;
        this.removeCategory = removeCategory;
        this.selectedCardFiles = selectedCardFiles;

        putValue(NAME, removeCategory.getName());
//        putValue(SMALL_ICON, null);
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
//        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        int status = JOptionPane.showConfirmDialog(ownerDialog, Utils.getRB().getString("CardFileTableModel.reallyRemove") + " '" + removeCategory + "'?");
        if (status != JOptionPane.YES_OPTION) {
            return;
        }

        for (CardFile selectedCardFile : selectedCardFiles) {
            removeCategory.unregisterCardSet(selectedCardFile);
        }
    }
}

