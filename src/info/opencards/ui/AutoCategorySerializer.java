package info.opencards.ui;

import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryChangeAdapter;
import info.opencards.core.categories.CategoryUtils;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
class AutoCategorySerializer extends CategoryChangeAdapter {


    private final Category myRootCat;


    public AutoCategorySerializer(Category rootCategory) {
        myRootCat = rootCategory;
    }


    private void serializeCatetoryTree() {
        CategoryUtils.serializeCategoryModel(myRootCat);
    }


    public void removedChild(Category child) {
        serializeCatetoryTree();
    }


    public void addedChildCategory(Category category) {
        serializeCatetoryTree();
    }


    public void registeredCardset(Category category, CardFile cardSet) {
        serializeCatetoryTree();
    }


    public void unregisteredCardset(Category category, CardFile cardSet) {
        serializeCatetoryTree();
    }


    public void categoryRelocated(Category category, Category oldParent, Category newParent) {
        serializeCatetoryTree();
    }


    public void categoryChanged(Category category) {
        serializeCatetoryTree();
    }


    public void categoryDetached(Category category, Category oldParent) {
//        serializeCatetoryTree();
    }
}
