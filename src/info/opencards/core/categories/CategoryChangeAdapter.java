package info.opencards.core.categories;

import info.opencards.core.CardFile;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class CategoryChangeAdapter implements CategoryChangeListener {


    public void removedChild(Category child) {
    }


    public void addedChildCategory(Category category) {
    }


    public void registeredCardset(Category category, CardFile cardSet) {
    }


    public void unregisteredCardset(Category category, CardFile cardSet) {
    }


    public void categoryRelocated(Category category, Category oldParent, Category newParent) {
    }


    public void categoryChanged(Category category) {
    }


    public void categoryDetached(Category category, Category oldParent) {
    }
}
