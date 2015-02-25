package info.opencards.ui.catui;


import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryChangeListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.Enumeration;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class CategoryModel extends DefaultTreeModel implements CategoryChangeListener {


    private final Category rootCategory;
    private final CategoryTree categoryTree;


    public CategoryModel(DefaultMutableTreeNode root, CategoryTree categoryTree) {
        super(root);
        this.categoryTree = categoryTree;

        rootCategory = (Category) root.getUserObject();
        rootCategory.addCategoryChangeListener(this);

        for (Category category : rootCategory.getChildCategories()) {
            addedChildCategory(category);
        }
    }


    public void valueForPathChanged(TreePath path, Object newValue) {
//        super.valueForPathChanged(path, newValue);
        Category category = (Category) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        category.setName((String) newValue);
    }


    public static Category getDefaultCategory() {
        Category rootCategory = new Category("All");

        Category mathCategory = new Category("Math");
        mathCategory.addChildCategory(new Category("Statistics"));
        rootCategory.addChildCategory(mathCategory);

        rootCategory.addChildCategory(new Category("Computer"));

        Category bioCategory = new Category("Biology");
        bioCategory.addChildCategory(new Category("Neuroscience"));
        bioCategory.addChildCategory(new Category("Genetics"));
        rootCategory.addChildCategory(bioCategory);

        rootCategory.addChildCategory(new Category("Arts"));

        return rootCategory;
    }


    DefaultMutableTreeNode getNode(Object userValue) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();

        for (Enumeration e = root.depthFirstEnumeration(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

            if (node.getUserObject() == userValue) {
                return node;
            }
        }

        return null;
    }


    public void removedChild(Category child) {
        DefaultMutableTreeNode node = getNode(child);
        if (node != null) {
            node.removeFromParent();
        }
//            getNode(child.getParent()).remove(getNode(child));

        reload();
        categoryTree.expandTree();
    }


    public void addedChildCategory(Category category) {
        getNode(category.getParent()).add(new DefaultMutableTreeNode(category));

        //recurse into all sub-categories of <code>category</code>
        for (Category childCat : category.getChildCategories()) {
            addedChildCategory(childCat);
        }

        //expand row
        reload();
        categoryTree.expandTree();
    }


    public void registeredCardset(Category category, CardFile cardSet) {
    }


    public void unregisteredCardset(Category category, CardFile cardSet) {
    }


    public void categoryChanged(Category category) {
//        getNode(category).setUserObject(category);
        reload();
        categoryTree.expandTree();
    }


    public void categoryDetached(Category category, Category oldParent) {
        DefaultMutableTreeNode catNode = getNode(category);
        removeNodeFromParent(catNode);

        reload();
        categoryTree.expandTree();
    }


    public Category getRootCategory() {
        return (Category) ((DefaultMutableTreeNode) getRoot()).getUserObject();
    }
}
