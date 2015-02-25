package info.opencards.ui;

import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.ui.catui.CategoryTreeSelectionListener;
import info.opencards.ui.table.CardSetTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Keeps track of the currently selected card-files and informs registered listeners on changes.
 *
 * @author Holger Brandl
 */
public class CurFileSelectionManager implements ListSelectionListener, CategoryTreeSelectionListener {


    private final CardSetTable cardFileTable;
    private final List<CardFileSelectionListener> selListeners = new ArrayList<CardFileSelectionListener>();

    private List<CardFile> curCategoryFiles;
    private List<CardFile> lastSelection;


    public CurFileSelectionManager(CardSetTable cardfileTable) {
        this.cardFileTable = cardfileTable;
    }


    // the listener for card-file selections within the current set of table-files
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) // because of hint on on http://www.chka.de/swing/table/faq.html
            return;

        List<CardFile> selectedFiles = new ArrayList<CardFile>();

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        if (lsm.isSelectionEmpty()) {
            // use the currently selected category files as fallback if the current table-selection is empty
            informListeners(curCategoryFiles);
        } else {
            // Find out which indices are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();

            // determine the set of selected files
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    selectedFiles.add(cardFileTable.getSortedRowFile(i));
                }
            }

            // inform all listeners about the changed selection
            informListeners(selectedFiles);
        }
    }


    private void informListeners(List<CardFile> selectedFiles) {
        lastSelection = selectedFiles;
        for (CardFileSelectionListener selListener : selListeners) {
            selListener.cardFileSelectionChanged(selectedFiles);
        }
    }


    /**
     * Adds a new listener.
     */
    public void addCardFileSelectionListener(CardFileSelectionListener l) {
        if (l == null)
            return;

        selListeners.add(l);
    }


    /**
     * Removes a listener.
     */
    public void removeCardFileSelectionListener(CardFileSelectionListener l) {
        if (l == null)
            return;

        selListeners.remove(l);
    }


    public void categorySelectionChanged(List<CardFile> selectedFiles, Set<Category> selCategories) {
        this.curCategoryFiles = selectedFiles;

        informListeners(selectedFiles);
    }


    public void refireLastSelection() {
        informListeners(lastSelection);
    }
}
