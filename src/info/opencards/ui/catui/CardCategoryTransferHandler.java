/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2007 Riad Djemili
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package info.opencards.ui.catui;


import info.opencards.core.CardFile;
import info.opencards.core.CardFileCache;
import info.opencards.core.categories.Category;
import info.opencards.ui.table.CardFileDragHandler;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.Collection;


/**
 * Organizes datatransfers between the card table and the category tree.
 *
 * @author djemili
 */
class CardCategoryTransferHandler extends TransferHandler {


    private final static DataFlavor CATEGORY_FLAVOR = new DataFlavor(Category.class, "Category");


    public CardCategoryTransferHandler() {
    }


    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }


    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (comp instanceof CategoryTree) {
            for (DataFlavor transferFlavor : transferFlavors) {
                if (transferFlavor == CATEGORY_FLAVOR) {
                    return true;
                } else if (transferFlavor.equals(CardFileDragHandler.CARDFILE_FLAVOR)) {
                    return true;
                } else if (transferFlavor.equals(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean importData(JComponent comp, Transferable t) {
        Category targetCategory;
        if (comp instanceof CategoryTree) {
            CategoryTree tree = (CategoryTree) comp;
            targetCategory = tree.getSelectedCategory();
        } else {
            return false;
        }


        try {
            if (t.isDataFlavorSupported(CATEGORY_FLAVOR)) {
                Category category = (Category) t.getTransferData(CATEGORY_FLAVOR);
                // don't do anything if the target is a child of the drop-item
                if (targetCategory.isChild(category) || targetCategory.equals(category))
                    return false;

                // don't do anything if the target category has already a same named child
                if (targetCategory.hasChildNamed(category.getName())) {
                    return false;
                }

                targetCategory.addChildCategory((Category) category.clone());
                return true;

            } else if (t.isDataFlavorSupported(CardFileDragHandler.CARDFILE_FLAVOR)) {
                //register the dragged files to the drop-category
                Collection<CardFile> dragFiles = (Collection<CardFile>) t.getTransferData(CardFileDragHandler.CARDFILE_FLAVOR);

                for (CardFile dragFile : dragFiles) {
                    targetCategory.registerCardSet(dragFile);
                }

                return true;

            } else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                java.util.List<File> importList = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

                for (File file : importList) {
                    targetCategory.registerCardSet(CardFileCache.getCardFile(file));
                }

                return true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    protected Transferable createTransferable(JComponent c) {
        if (c instanceof CategoryTree) {
            Category category = ((CategoryTree) c).getSelectedCategory();

            // dont allow operations with root category
            return category != null && category.getParent() != null ? new CategoryTransferable(category) : null;
        }

        return null;
    }


    protected void exportDone(JComponent source, Transferable data, int action) {
        try {
            if (data == null)
                return;

            Category category = (Category) data.getTransferData(CATEGORY_FLAVOR);
            if (!data.isDataFlavorSupported(CATEGORY_FLAVOR)) {
                return;
            }

            if (action == MOVE) {
                category.remove();
            } else if (action == COPY) {
                // do nothing yet
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class CategoryTransferable implements Transferable {


        private final Category category;


        public CategoryTransferable(Category category) {
            this.category = category;
        }


        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }

            return category;
        }


        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{CATEGORY_FLAVOR};
        }


        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return CATEGORY_FLAVOR.equals(flavor);
        }
    }
}
