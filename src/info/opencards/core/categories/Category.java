package info.opencards.core.categories;


import info.opencards.core.CardFile;
import info.opencards.pptintegration.PPTSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A thematic category for sets of flashcard. Addtionally falshcard can have attached sub-categories.
 *
 * @author Holger Brandl
 */
public class Category implements Cloneable {


    private String catName;

    private Category parent;

    private List<CardFile> cardSets = new ArrayList<CardFile>();
    private List<Category> childCategories = new ArrayList<Category>();

    private transient List<CategoryChangeListener> categoryListeners;


    public Category(String name) {
        catName = name;
    }


    public String getName() {
        return catName;
    }


    public void setName(String newName) {
        catName = newName == null ? "" : newName;
    }


    public Category getParent() {
        return parent;
    }


    public void setParent(Category newParent) {
        Category oldParent = getParent();
        this.parent = newParent;

        assert oldParent != null || newParent != null;

        if (newParent != null && oldParent != null) {
            for (CategoryChangeListener changeListener : oldParent.getListeners()) {
                changeListener.categoryDetached(this, oldParent);
                changeListener.addedChildCategory(this);
            }
        } else if (newParent == null) {
            for (CategoryChangeListener changeListener : oldParent.getListeners()) {
                changeListener.categoryDetached(this, oldParent);
            }
        } else {
            for (CategoryChangeListener changeListener : getListeners()) {
                changeListener.addedChildCategory(this);
            }
        }
    }


    public String toString() {
        return getName();
    }


    public String toUserString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName() + "\n");
        for (CardFile cardSet : cardSets) {
            sb.append("- " + cardSet + "\n");
        }

        for (Category category : getChildCategories()) {
            sb.append("+ " + category + "\n");
        }

        sb.append("---");
        return sb.toString();
    }


    public List<CardFile> getCardSets() {
        return cardSets;
    }


    public void unregisterCardSet(CardFile cardSet) {
        assert cardSets.contains(cardSet);

        cardSet.removeFromCategory(this);
        cardSets.remove(cardSet);

        // remove the learn-model file
        if (cardSet.belongsTo().isEmpty()) {
            final File metaDataFile = PPTSerializer.getMetaDataFile(cardSet);
            if (metaDataFile != null) // will be null if the parent directory has been removed
                metaDataFile.delete();
        }

        for (CategoryChangeListener categoryListener : getListeners()) {
            categoryListener.unregisteredCardset(this, cardSet);
        }
    }


    public void registerCardSet(CardFile cardSet) {
        if (getCardSets().contains(cardSet))
            return;

        cardSet.addToCategory(this);
        cardSets.add(cardSet);

        for (CategoryChangeListener categoryListener : getListeners()) {
            categoryListener.registeredCardset(this, cardSet);
        }
    }


    public List<Category> getChildCategories() {
        return childCategories;
    }


    public Category getRootCategory() {
        return getParent() != null ? getParent().getRootCategory() : this;
    }


    public boolean isChild(Category targetCategory) {
        return (getParent() == targetCategory) || (getParent() != null && getParent().isChild(targetCategory));
    }

    //
    //  Some category algebra methods
    //


    /**
     * tt.
     */
    public void addChildCategory(Category category) {
        childCategories.add(category);
//        category.setParent(this);
        category.parent = this;

        for (CategoryChangeListener categoryListener : getListeners()) {
            categoryListener.addedChildCategory(category);
        }
    }


    /**
     * removes this category but inherits all subcategories and registered cardsets to the parent category.
     */
    public void flattenChild(Category child) {
        for (CardFile cardFile : child.getCardSets()) {
            cardFile.removeFromCategory(child);
            registerCardSet(cardFile);
        }

        while (!child.getChildCategories().isEmpty()) {
            child.getChildCategories().get(0).relocate(this);
        }

        removeChildren(child);
    }


    public void remove() {
        this.getParent().removeChildren(this);
    }


    /**
     * Removes a category. This involves the deletion of all sub-categories and the unregistration of all affected
     * flash-card files.
     */
    void removeChildren(Category child) {
        assert childCategories.contains(child);

        for (CardFile cardFile : child.getCardSets()) {
            cardFile.removeFromCategory(child);
        }

        while (child.getChildCategories().size() > 0) {
            Category category = child.getChildCategories().get(0);
            child.removeChildren(category);
        }

        childCategories.remove(child);

        for (CategoryChangeListener categoryListener : getListeners()) {
            categoryListener.removedChild(child);
        }
    }


    public synchronized List<CategoryChangeListener> getListeners() {
        if (getRootCategory().categoryListeners == null)
            getRootCategory().categoryListeners = new ArrayList<CategoryChangeListener>();

        return getRootCategory().categoryListeners;
    }


    Category cutChildren(Category child) {
        assert childCategories.contains(child);
        childCategories.remove(child);

        child.setParent(null);

        return child;
    }


    Set<CardFile> getRecurseCardSets() {
        Set<CardFile> allSubFiles = new HashSet<CardFile>();

        allSubFiles.addAll(getCardSets());

        for (Category category : getChildCategories()) {
            allSubFiles.addAll(category.getRecurseCardSets());
        }

        return allSubFiles;
    }


    public void relocate(Category newRoot) {
        getParent().cutChildren(this);
        newRoot.addChildCategory(this);
    }


    /**
     * Two categories are equal if they have the same parental structure (up to the root node)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Category))
            return false;

        Category cat = (Category) o;

        if (getName() == null && cat.getName() != null) // this name null but o's isn't
            return false;

        if (getParent() == null) { // this has no parent
            return cat.getParent() == null && cat.getName().equals(getName()); // o has also no parent and names are equal
        }

        return cat.getName().equals(getName()) && getParent().equals(cat.getParent());
    }


    public Object clone() throws CloneNotSupportedException {
        Category c = (Category) super.clone();
        c.parent = null;
        c.cardSets = new ArrayList<CardFile>();

        // register the new category for all dircect child nodes
        for (CardFile cardFile : getCardSets()) {
            c.registerCardSet(cardFile);
        }

        // make deep copy of cardtrees
        c.childCategories = new ArrayList<Category>();
        for (Category category : getChildCategories()) {
            Category chilCat = (Category) category.clone();
            c.childCategories.add(chilCat);
            chilCat.parent = c;
        }

        c.catName = getName();

        return c;
    }


    /**
     * Adds a new listener.
     */
    public void addCategoryChangeListener(CategoryChangeListener l) {
        if (l == null || getListeners().contains(l))
            return;

        getListeners().add(l);
    }


    /**
     * Removes a listener.
     */
    public void removeCategoryChangeListener(CategoryChangeListener l) {
        if (l == null)
            return;

        getListeners().remove(l);
    }


    public boolean hasChildNamed(String name) {
        for (Category targetChild : this.getChildCategories()) {
            if (targetChild.getName().equals(name))
                return true;
        }

        return false;
    }


    /**
     * Returns a child category with a given name.
     */
    Category getChildCategoryByName(String name) {
        for (Category category : getChildCategories()) {
            if (category.getName().equals(name))
                return category;
        }

        // there is no category names 'name'
        return null;
    }


    /**
     * Returns a child category with a given name. If there is no such category it will be created.
     */
    public Category getOrCreateChildCategoryByName(String name) {
        Category childCategory = getChildCategoryByName(name);

        if (childCategory == null) {
            childCategory = new Category(name);
            addChildCategory(childCategory);
        }

        return childCategory;
    }
}
