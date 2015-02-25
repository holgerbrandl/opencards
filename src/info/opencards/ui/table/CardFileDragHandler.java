package info.opencards.ui.table;

import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class CardFileDragHandler extends TransferHandler {


    public final static DataFlavor CARDFILE_FLAVOR = new DataFlavor(CardFile.class, "CardFile");
    private final CardSetTable cardSetTable;


    public CardFileDragHandler(CardSetTable cardSetTable) {
        this.cardSetTable = cardSetTable;
    }


    CardTableModel getModel() {
        return (CardTableModel) cardSetTable.getModel();
    }


    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }


    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return false;
    }


    public boolean importData(JComponent comp, Transferable t) {
        return false;
    }


    protected Transferable createTransferable(JComponent c) {
        CardTableModel model = getModel();

        if (c instanceof CardSetTable && !model.getCurFiles().isEmpty()) {
            Collection<CardFile> curSelectionFiles = new ArrayList<CardFile>();

            for (int selRowIndex : ((CardSetTable) c).getSelectedRows()) {
                curSelectionFiles.add(cardSetTable.getSortedRowFile(selRowIndex));
            }

            return new CardFilesTransferable(curSelectionFiles, model.getCurCatgories());
        }

        return null;
    }


    protected void exportDone(JComponent source, Transferable data, int action) {
        try {
            if (data == null)
                return;

            if (!(data instanceof CardFilesTransferable)) {
                return;
            }

            Collection<CardFile> transferFiles = (Collection<CardFile>) data.getTransferData(CARDFILE_FLAVOR);
            if (!data.isDataFlavorSupported(CARDFILE_FLAVOR)) {
                return;
            }

            if (action == MOVE) {
                List<Category> fileCatsBeforeDrag = ((CardFilesTransferable) data).getCurCatgories();

                HashSet<Category> incList = new HashSet<Category>();
                for (Category category : fileCatsBeforeDrag) {
                    incList.addAll(CategoryUtils.recursiveCatCollect(category));
                }

                CardTableModel model = getModel();

                assert model.getCurCatgories().size() == 1;
                Category targetCategory = model.getCurCatgories().get(0);
                // now iterate over all flashcards and remove the files if necessary
                for (CardFile transferFile : transferFiles) {
                    for (Category category : new ArrayList<Category>(transferFile.belongsTo())) { // wrapped because of ConcurrentModifactionException
                        if (incList.contains(category) && !category.equals(targetCategory))
                            category.unregisterCardSet(transferFile);
                    }
                }
            } else if (action == COPY) {
                // do nothing yet
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class CardFilesTransferable implements Transferable {


        private final Collection<CardFile> transferFiles;
        private final List<Category> curCatgories;


        public CardFilesTransferable(Collection<CardFile> transferFiles, List<Category> curCatgories) {
            this.transferFiles = transferFiles;
            this.curCatgories = curCatgories;
        }


        public List<Category> getCurCatgories() {
            return curCatgories;
        }


        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return transferFiles;
        }


        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{CARDFILE_FLAVOR};
        }


        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return CARDFILE_FLAVOR.equals(flavor);
        }
    }
}
