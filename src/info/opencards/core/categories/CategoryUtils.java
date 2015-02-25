package info.opencards.core.categories;

import com.thoughtworks.xstream.XStream;
import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.CardFileCache;
import info.opencards.ui.preferences.AdvancedSettings;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


/**
 * Some static utitilty methods which is the handling of categories
 *
 * @author Holger Brandl
 */
public class CategoryUtils {


    public static final String TREE_PROP = "serialCatTree";
    public static final String INCLUDE_CHILDS = "includeChilds";


    public static List<Category> recursiveCatCollect(Category rootCategory) {
        HashSet<Category> allCats = new HashSet<Category>();

        for (Category category : rootCategory.getChildCategories()) {
            allCats.add(category);
            allCats.addAll(recursiveCatCollect(category));
        }

        allCats.add(rootCategory);
        return new ArrayList<Category>(allCats);
    }


    public static Set<CardFile> recursiveCardFileCollect(Category rootCategory) {
        List<Category> categories = recursiveCatCollect(rootCategory);

        Set<CardFile> cardFiles = new HashSet<CardFile>();

        for (Category category : categories) {
            cardFiles.addAll(new HashSet<CardFile>(category.getCardSets()));
        }

        return cardFiles;
    }


    public static void serializeCategoryModel(Category rootCategory) {
        File confTreeFile = getCategoryTreeFile();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(confTreeFile));
            new XStream().toXML(rootCategory, writer);
            writer.close();
        } catch (Throwable e) {
            throw new RuntimeException("can not serialize categorytree to " + confTreeFile + ": " + e);
        }

//        String setCatTree = new XStream().toXML(rootCategory);
//        Utils.getPrefs().put(TREE_PROP, setCatTree);
    }


    private static File getCategoryTreeFile() {
        // use a custom location if defined
        return new File(Utils.getPrefs().get(AdvancedSettings.CUSTOM_CATTREE_LOCATION, AdvancedSettings.getDefaultCatTreeLocation().getAbsolutePath()));
    }


    public static Category deserializeCategoryModel(Frame awtParent) {
//        String serCatTree = Utils.getPrefs().get(TREE_PROP, null);

        Category rootCat = null;
        File categoryTreeFile = getCategoryTreeFile();

        if (categoryTreeFile.isFile()) {
            try {
                rootCat = (Category) new XStream().fromXML(new BufferedReader(new FileReader(categoryTreeFile)));
            } catch (Throwable e) {
                throw new RuntimeException("category-tree deserialization from " + categoryTreeFile + " failed: " + e);
            }
        }

        if (rootCat != null) {
            // iterate over all categorized files and remove files which are not existent and add remaining files to cache
            for (CardFile cardFile : CategoryUtils.recursiveCardFileCollect(rootCat)) {
                if (!cardFile.getFileLocation().isFile()) {
                    String msg = Utils.getRB().getString("CategoryUtils.fileNotFound").replaceAll("<<filename>>", "'" + cardFile.getFileLocation().getAbsolutePath() + "' ");
                    JOptionPane.showMessageDialog(awtParent, msg, Utils.getRB().getString("CategoryUtils.fileNotFound.title"), JOptionPane.ERROR_MESSAGE);

                    for (Category category : cardFile.belongsTo()) {
                        category.unregisterCardSet(cardFile);
                    }
                } else {
                    CardFileCache.register(cardFile);
                }
            }
        }

        return rootCat;
    }


    private static boolean isRooted(Category rootCat, Category category) {
        if (category.equals(rootCat))
            return true;

        while (category != null) {
            if (category.equals(rootCat))
                return true;

            category = category.getParent();
        }

        return false;
    }


    public static Collection<CardFile> extractSelectedFiles(Collection<Category> selectedCategories) {
        HashSet<CardFile> currentFiles = new HashSet<CardFile>();

        boolean includeChildFiles = Utils.getPrefs().getBoolean(INCLUDE_CHILDS, false);
        if (includeChildFiles) {
            for (Category selectedCategory : selectedCategories) {
                Set<CardFile> recursiveCollectCards = recursiveCardFileCollect(selectedCategory);
                for (CardFile recCollectCardFile : recursiveCollectCards) {
                    if (!currentFiles.contains(recCollectCardFile))
                        currentFiles.add(recCollectCardFile);
                }
            }
        } else {
            for (Category selectedCategory : selectedCategories) {
                List<CardFile> catFiles = selectedCategory.getCardSets();
                for (CardFile catFile : catFiles) {
                    if (!currentFiles.contains(catFile))
                        currentFiles.add(catFile);
                }
            }
        }

        return currentFiles;
    }


    public static Category getSelectedCategory() {
        return OpenCards.getInstance().getCategoryView().categoryPanel.getCatTree().getSelectedCategory();
    }
}
