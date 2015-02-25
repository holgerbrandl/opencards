package info.opencards.core.categories;

import info.opencards.core.CardFile;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public interface CategoryChangeListener {


    void removedChild(Category child);


    void addedChildCategory(Category category);


    void registeredCardset(Category category, CardFile cardSet);


    void unregisteredCardset(Category category, CardFile cardSet);


    void categoryChanged(Category category);


    void categoryDetached(Category category, Category oldParent);
}
