package info.opencards.ui;

import info.opencards.core.CardFileCache;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;


/**
 * Enables dropping of file into the OC window, which will register them to the currently selected category.
 *
 * @author Holger Brandl
 */
public class CardSetDndHandler extends DropTargetAdapter {


    DropTarget dt;


    public static void main(String[] args) {
//        dt = new DropTarget(ta, this);
    }


    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();

            for (DataFlavor flavor : flavors) {
                // Check for file lists specifically
                if (flavor.isFlavorJavaFileListType()) {
                    // Great!  Accept copy drops...
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    // And add the list of file names to our text area
                    List<File> droppedFiles = (List<File>) tr.getTransferData(flavor);
                    for (File file : droppedFiles) {
                        System.err.println("dropped a " + file);

                        Category curCat = CategoryUtils.getSelectedCategory();
                        curCat.registerCardSet(CardFileCache.getCardFile(file));
                    }

                    // If we made it this far, everything worked.
                    dtde.dropComplete(true);
                    return;
                }
            }
            // Hmm, the user must not have dropped a file list
            System.out.println("Drop failed: " + dtde);
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }
}
