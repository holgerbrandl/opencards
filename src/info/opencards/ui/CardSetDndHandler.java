package info.opencards.ui;

import info.opencards.CardFileBackend;
import info.opencards.OpenCards;
import info.opencards.core.CardFileCache;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;

import javax.swing.*;
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

                    boolean invalidDrop = false;

                    List<File> droppedFiles = (List<File>) tr.getTransferData(flavor);
                    for (File file : droppedFiles) {
                        if (CardFileBackend.hasSupportedExtension(file)) {
                            Category curCat = CategoryUtils.getSelectedCategory();
                            curCat.registerCardSet(CardFileCache.getCardFile(file));
                        } else {
                            invalidDrop = true;
                        }
                    }

                    if (invalidDrop) {
                        Runnable task2 = () -> {
                            JOptionPane.showMessageDialog(OpenCards.getInstance(),
                                    "Just PowerPoint (ppt) and MarkDown (md) are supported as flashcard-sets by OpenCards",
                                    "Invalid file format of dropped file",
                                    JOptionPane.WARNING_MESSAGE)
                            ;
                        };

// start the thread
                        new Thread(task2).start();
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
