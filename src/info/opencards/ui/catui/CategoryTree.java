package info.opencards.ui.catui;


import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryChangeAdapter;
import info.opencards.core.categories.CategoryChangeListener;
import info.opencards.core.categories.CategoryUtils;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;


public class CategoryTree extends JTree {


    private CategoryModel catTreeModel;

    private final List<CategoryTreeSelectionListener> cardSelectionListeners = new ArrayList<CategoryTreeSelectionListener>();


    public CategoryTree() {
        addMouseListener(new CatTreePopUp(this));

        setDragEnabled(true);
        setEditable(true);
        setCellEditor(new CellEditor(this, new DefaultTreeCellRenderer()));

        getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                expandTree();

            }


            public void treeNodesInserted(TreeModelEvent e) {
                //select and expand
//                expandPath(e.getTreePath());
                expandTree();
            }


            public void treeNodesRemoved(TreeModelEvent e) {
//                expandPath(e.getTreePath());
                expandTree();
            }


            public void treeStructureChanged(TreeModelEvent e) {
//                expandPath(e.getTreePath());
                expandTree();
            }
        });

        addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                informCardFileSelectionListeners();
            }
        });

        DefaultTreeCellRenderer treeCellRenderer = (DefaultTreeCellRenderer) getCellRenderer();
        treeCellRenderer.setLeafIcon(treeCellRenderer.getOpenIcon());

        expandTree();
    }


    /**
     * Sets the root cateogy for this tree. If <code>null</code> the current tree structure will be deleted if the tree
     * already exists, and the default set of categories will be loaded.
     */
    public void setRootCategory(Category rootCat) {

        // deserialze categories or create dummy category model

        if (rootCat == null) {
            rootCat = CategoryModel.getDefaultCategory();
        }

        rootCat.addCategoryChangeListener(new CategoryChangeAdapter() {

            public void addedChildCategory(Category category) {
                expandTree();
                informCardFileSelectionListeners();
            }


            public void removedChild(Category child) {
                expandTree();
                informCardFileSelectionListeners();
            }


            public void categoryRelocated(Category category, Category oldParent, Category newParent) {
                expandTree();
                informCardFileSelectionListeners();
            }


            public void registeredCardset(Category category, CardFile cardSet) {
                informCardFileSelectionListeners();
            }


            public void unregisteredCardset(Category category, CardFile cardSet) {
                informCardFileSelectionListeners();
            }
        });

        if (catTreeModel != null) {
            Collection<CategoryChangeListener> changeListeneres = new ArrayList<CategoryChangeListener>();
            changeListeneres.addAll(catTreeModel.getRootCategory().getListeners());

            for (CategoryChangeListener changeListener : changeListeneres) {
                rootCat.addCategoryChangeListener(changeListener);
            }
        }


        catTreeModel = new CategoryModel(new DefaultMutableTreeNode(rootCat), this);

//        catTreeModel.getRootCategory().addCategoryChangeListener(new TreeModifier());
        setModel(catTreeModel);

        setTransferHandler(new CardCategoryTransferHandler());
        setSelectionRow(0);

        expandTree();
    }


    public void expandTree() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }


    public Category getSelectedCategory() {
        TreePath path = getSelectionPath();
        if (path == null)
            return null;

        DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (selectedTreeNode == null)
            return null;

        return (Category) selectedTreeNode.getUserObject();
    }


    public Category getRootCategory() {
        return (Category) ((DefaultMutableTreeNode) getModel().getRoot()).getUserObject();
    }


    public void refire(TreePath pathForRow) {
        fireValueChanged(new TreeSelectionEvent(this, pathForRow, true, pathForRow, pathForRow));
    }


    /**
     * Adds a new listener.
     */
    public void addCardFilesSelectionListener(CategoryTreeSelectionListener l) {
        if (l == null)
            return;

        cardSelectionListeners.add(l);

        // update the new listener to the current selection
        informCardFileSelectionListeners(); //  a little hacky but it does the job
    }


    /**
     * Removes a listener.
     */
    public void removeCardFilesSelectionListener(CategoryTreeSelectionListener l) {
        if (l == null)
            return;

        cardSelectionListeners.remove(l);
    }


    public void informCardFileSelectionListeners() {
        Set<Category> selCategories = new HashSet<Category>();

        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths == null) {
//            JOptionPane.showConfirmDialog(this, "emtpy selection in  tree");
            return;
        }

        List<Category> allCats = CategoryUtils.recursiveCatCollect(catTreeModel.getRootCategory());
        for (TreePath selectionPath : selectionPaths) {
            Category category = (Category) ((DefaultMutableTreeNode) selectionPath.getLastPathComponent()).getUserObject();
            if (allCats.contains(category))
                selCategories.add(category);
        }

        // select the root-category if nothing is selected
        if (selCategories.isEmpty())
            selCategories.add(catTreeModel.getRootCategory());

//        Collection<Category> selectedCategories = ((CardTableModel) cardfileTable.getModel()).getCurCatgories();
        List<CardFile> selectedFiles = new ArrayList<CardFile>(CategoryUtils.extractSelectedFiles(selCategories));

        for (CategoryTreeSelectionListener listener : cardSelectionListeners) {
            listener.categorySelectionChanged(selectedFiles, selCategories);
        }
    }


    class CellEditor extends DefaultTreeCellEditor {


        public CellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
        }


        public boolean isCellEditable(EventObject event) {
            if (event != null) {
                MouseEvent e = (MouseEvent) event;
                TreePath path = getPathForLocation(e.getX(), e.getY());

                if (path != null) {
                    Category m_editedCategory = (Category) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

//                    if (m_editedCategory.getName().equals("All"))
                    if (m_editedCategory.getParent() == null)
                        return false;
                }
            }

            // make root not editable
            return super.isCellEditable(event);
        }


        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Category category = (Category) node.getUserObject();

            return super.getTreeCellEditorComponent(tree, category.getName(), isSelected, expanded, leaf, row);
        }
    }
}

