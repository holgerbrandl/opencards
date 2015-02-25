package info.opencards.ui.catui;

import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;

import java.util.List;
import java.util.Set;


/**
 * Specifies the functionality to process changes in the current set of a selected card-=files
 *
 * @author Holger Brandl
 */
public interface CategoryTreeSelectionListener {


    public void categorySelectionChanged(List<CardFile> selectedFiles, Set<Category> selCategories);

}
