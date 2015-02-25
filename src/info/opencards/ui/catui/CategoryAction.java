package info.opencards.ui.catui;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.CardFileCache;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;
import info.opencards.ui.CardFilesPreloader;
import info.opencards.util.ScaleableIcon;
import info.opencards.util.UIUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public abstract class CategoryAction extends AbstractAction {


    final CategoryTree categoryTree;


    CategoryAction(CategoryTree categoryTree) {
        this.categoryTree = categoryTree;
    }
}


class CreateCategoryAction extends CategoryAction {


    CreateCategoryAction(CategoryTree categoryTree) {
        super(categoryTree);

        putValue(NAME, Utils.getRB().getString("CatAction.newCategory")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_add.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        String newCatname = JOptionPane.showInputDialog(UIUtils.getOwnerDialog(categoryTree), Utils.getRB().getString("CategoryTree.addNewCategory.text"), Utils.getRB().getString("CategoryTree.addNewCategory.default"));
        if (newCatname == null || newCatname.trim().length() == 0) {
            return;
        }

        Category parentCategory = categoryTree.getSelectedCategory();

        Category newCategory = new Category(newCatname);
        parentCategory.addChildCategory(newCategory);
    }
}


class DeleteCategoryAction extends CategoryAction {


    public DeleteCategoryAction(CategoryTree categoryTree) {
        super(categoryTree);

        putValue(NAME, Utils.getRB().getString("CatAction.delete"));
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_remove.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        Category category = categoryTree.getSelectedCategory();

        if (category.getRootCategory().equals(category))
            return;

        // ensure that the user really want to remove this category
//        Set<CardFile> relatedCardfiles = CategoryUtils.recursiveCardFileCollect(category);
        ResourceBundle rb = Utils.getRB();

        String reallyDelete = rb.getString("CatAction.reallyDelete").replace("<<CATEGORY_NAME>>", "'" + category.getName() + "'");

        int status = JOptionPane.showConfirmDialog(UIUtils.getOwnerDialog(categoryTree),
                reallyDelete, rb.getString("OpenCards.reallyCleanAllTitle"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (status == JOptionPane.YES_OPTION)
            category.remove();
    }
}


class AddCardSetAction extends CategoryAction {


    private final PasteCategoryAction pasteAction;


    public AddCardSetAction(CategoryTree categoryTree, PasteCategoryAction pasteAction) {
        super(categoryTree);
        this.pasteAction = pasteAction;

        putValue(NAME, Utils.getRB().getString("CatAction.addCardSet"));
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_addcardset.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        Category category = categoryTree.getSelectedCategory();

        final String DEFAULT_DIR = "addcat.defdir";
        File defDir = new File(Utils.getPrefs().get(DEFAULT_DIR, System.getProperty("user.home")));
        if (!defDir.isDirectory())
            defDir = new File(".");

        JFileChooser jfc = new JFileChooser(defDir);
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                String fileName = f.getName();
//                return (fileName.endsWith(".odp") || fileName.endsWith(".ppt")) || f.isDirectory();
                return (fileName.endsWith(".ppt")) || f.isDirectory();
            }


            public String getDescription() {
//                return "Flashcard presentations (*.odp,  *.ppt)";
                return "PowerPoint Presentations (*.ppt)";
            }
        });

        int status = jfc.showOpenDialog(UIUtils.getOwnerDialog(categoryTree));
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = jfc.getSelectedFiles();

            // collect all files and create new files only if necessary
            Set<CardFile> allFiles = new HashSet<CardFile>();

            if (pasteAction.getPasteCategory() != null)
                allFiles.addAll(CategoryUtils.recursiveCardFileCollect(pasteAction.getPasteCategory()));

            allFiles.addAll(CategoryUtils.recursiveCardFileCollect(categoryTree.getRootCategory()));

            List<CardFile> cardFilesBuffer = new ArrayList<CardFile>();
            for (File file : selectedFiles) {
                if (file.isFile() && file.getName().endsWith(".ppt")) {
                    CardFile cardFile = getCard(allFiles, file);

                    if (cardFile == null) {
                        cardFile = CardFileCache.getCardFile(file);
                    }

                    cardFilesBuffer.add(cardFile);
                }
            }

            Dialog owner = UIUtils.getOwnerDialog(categoryTree);

            // we cannot remove this one, because the preloader of the tree would process each file in a single run,
            // which wouldn't look nice because the the progress-bar would flicker for each file
            new CardFilesPreloader(owner, OpenCards.getCardSetManager().getBackend().getSerializer()).categorySelectionChanged(cardFilesBuffer, null);

            for (CardFile cardFile : cardFilesBuffer) {
                category.registerCardSet(cardFile);
            }

            // remember chosen directory as default for the next time
            if (selectedFiles.length > 0 && selectedFiles[0].getParentFile().isDirectory())
                Utils.getPrefs().put(DEFAULT_DIR, selectedFiles[0].getParentFile().getAbsolutePath());
        }
    }


    private CardFile getCard(Set<CardFile> allFiles, File file) {
        for (CardFile curFile : allFiles) {
            if (curFile.getFileLocation().equals(file))
                return curFile;
        }

        return null;
    }
}


class PopulateSubCatTreeFromDirectoryAction extends CategoryAction {


    public PopulateSubCatTreeFromDirectoryAction(CategoryTree categoryTree) {
        super(categoryTree);

        putValue(NAME, Utils.getRB().getString("CatAction.parseDirectory") + " ...");
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_add_dirtree.png"));
        putValue(SHORT_DESCRIPTION, Utils.getRB().getString("CatAction.parseDirDescription"));
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        Category baseCategory = categoryTree.getSelectedCategory();

        final String DEFAULT_DIR = "addcat.defdir";
        File defDir = new File(Utils.getPrefs().get(DEFAULT_DIR, System.getProperty("user.home")));
        if (!defDir.isDirectory())
            defDir = new File(".");

        JFileChooser jfc = new JFileChooser(defDir);
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        jfc.setFileFilter(new FileFilter() {
//
//            public boolean accept(File f) {
//                String fileName = f.getName();
////                return (fileName.endsWith(".odp") || fileName.endsWith(".ppt")) || f.isDirectory();
//                return f.isDirectory();
//            }
//
//
//            public String getDescription() {
////                return "Flashcard presentations (*.odp,  *.ppt)";
//                return "roots of your card-file directory strucutre";
//            }
//        });

        // do nothing if the user canceled the operation
        if (jfc.showOpenDialog(UIUtils.getOwnerDialog(categoryTree)) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // now collect all files
        File rootDirectory = jfc.getSelectedFile();
        assert rootDirectory.isDirectory() : "selected file must be a directory";

//        Category rootCategory = baseCategory.getOrCreateChildCategoryByName(rootDirectory.getName());

        List<File> allFilesInSubDirectory = Utils.findFiles(rootDirectory, ".odp");

        // collect all already registered cardfiles and create new files only if necessary
        Set<CardFile> allFiles = new HashSet<CardFile>();
        allFiles.addAll(CategoryUtils.recursiveCardFileCollect(categoryTree.getRootCategory()));


        List<CardFile> newCardFiles = new ArrayList<CardFile>();
        for (File file : allFilesInSubDirectory) {
            if (file.isFile() && file.getName().endsWith(".odp")) {
                CardFile cardFile = getCard(allFiles, file);

                if (cardFile == null) {
                    cardFile = CardFileCache.getCardFile(file);
                }

                newCardFiles.add(cardFile);
            }
        }

        Dialog owner = UIUtils.getOwnerDialog(categoryTree);

        // we cannot remove this one, because the preloader of the tree would process each file in a single run,
        // which wouldn't look nice because the the progress-bar would flicker for each file
        new CardFilesPreloader(owner, OpenCards.getCardSetManager().getBackend().getSerializer()).categorySelectionChanged(newCardFiles, null);


        // attach all files and create new subcategories if necessary
        for (CardFile cardFile : newCardFiles) {
            // get the category path to the add-category and add the file there
            File parentCat = cardFile.getFileLocation();

            List<File> parentCatDirs = new ArrayList<File>();
            while (parentCatDirs.isEmpty() || !parentCatDirs.get(0).equals(rootDirectory)) {
                parentCat = parentCat.getParentFile();
                parentCatDirs.add(0, parentCat);
            }

            // remove the first one in order to avoid the selected folder to be created as category
//            parentCatDirs.remove(0);

            // now recurse down the the appropriate category and attach the file
            Category addCategory = baseCategory;
            for (File parentCatDir : parentCatDirs) {
                addCategory = addCategory.getOrCreateChildCategoryByName(parentCatDir.getName());
            }

            // register only if not previously done
            if (!addCategory.getCardSets().contains(cardFile))
                addCategory.registerCardSet(cardFile);
        }

        // remember chosen directory as default for the next time
        if (rootDirectory.getParentFile().isDirectory())
            Utils.getPrefs().put(DEFAULT_DIR, rootDirectory.getParentFile().getAbsolutePath());
    }


    private CardFile getCard(Set<CardFile> allFiles, File file) {
        for (CardFile curFile : allFiles) {
            if (curFile.getFileLocation().equals(file))
                return curFile;
        }

        return null;
    }
}


class CutCategoryAction extends CategoryAction {


    private final PasteCategoryAction pasteAction;


    public CutCategoryAction(CategoryTree categoryTree, PasteCategoryAction pasteAction) {
        super(categoryTree);
        this.pasteAction = pasteAction;

        putValue(NAME, Utils.getRB().getString("CatAction.cut"));
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_cut.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        Category category = categoryTree.getSelectedCategory();

        if (category.getRootCategory().equals(category))
            return;

        pasteAction.setPasteCategory(category);

        category.setParent(null);
    }
}


class CopyCategoryAction extends CategoryAction {


    private final PasteCategoryAction pasteAction;


    public CopyCategoryAction(CategoryTree categoryTree, PasteCategoryAction pasteAction) {
        super(categoryTree);
        this.pasteAction = pasteAction;

        putValue(NAME, Utils.getRB().getString("CatAction.copy"));
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_copy.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        Category category = categoryTree.getSelectedCategory();

        try {
            pasteAction.setPasteCategory((Category) category.clone());
        } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
        }
    }
}


class PasteCategoryAction extends CategoryAction {


    private Category pasteCategory;


    public PasteCategoryAction(CategoryTree categoryTree) {
        super(categoryTree);

        putValue(NAME, Utils.getRB().getString("CatAction.paste"));
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_paste.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);

        setEnabled(false);
    }


    public void actionPerformed(ActionEvent e) {
        Category category = categoryTree.getSelectedCategory();

        // don't add the category if there's already a same named child
        if (category.hasChildNamed(pasteCategory.getName()))
            return;

        category.addChildCategory(pasteCategory);
        setEnabled(false);
    }


    public Category getPasteCategory() {
        return pasteCategory;
    }


    public void setPasteCategory(Category pasteCategory) {
        this.pasteCategory = pasteCategory;
        setEnabled(pasteCategory != null);
    }
}


class RenameCategoryAction extends CategoryAction {


    private final CategoryTree categoryTree;


    public RenameCategoryAction(CategoryTree categoryTree) {
        super(categoryTree);
        this.categoryTree = categoryTree;


        putValue(NAME, Utils.getRB().getString("CatAction.rename"));
        putValue(SMALL_ICON, new ScaleableIcon("icons/category_rename.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        Category category = categoryTree.getSelectedCategory();
        String catName = category.getName();

        if (category.getRootCategory().equals(category))
            return;

        String newName = JOptionPane.showInputDialog(UIUtils.getOwnerDialog(categoryTree), "New category name", catName);
        category.setName(newName);

        // unfortunately this does not work as expected
//        categoryTree.getModel().valueForPathChanged(categoryTree.getSelectionModel().getSelectionPath(), newName);
    }
}


class ResolveChildsAction extends CategoryAction {


    private final CategoryTree categoryTree;


    public ResolveChildsAction(CategoryTree categoryTree) {
        super(categoryTree);
        this.categoryTree = categoryTree;


        putValue(NAME, Utils.getRB().getString("CatAction.resolveChilds"));
//        putValue(SMALL_ICON, new ScaleableIcon("icons/category_rename.png"));
//        putValue(SHORT_DESCRIPTION, "cuts the selected category");
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        putValue(MNEMONIC_KEY, 2);
    }


    public void actionPerformed(ActionEvent e) {
        // write the new value of the chckbox to the system properties
        JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
        Utils.getPrefs().putBoolean(CategoryUtils.INCLUDE_CHILDS, source.isSelected());

        categoryTree.refire(categoryTree.getSelectionPath()); //this is hacky in case of multiple selections
        OpenCards.getCardSetManager().refreshFileViews();
    }
}


