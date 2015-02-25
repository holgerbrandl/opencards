package info.opencards.util;

import info.opencards.CardFileBackend;
import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.CardFileCache;
import info.opencards.core.CardFileSerializer;
import info.opencards.core.categories.Category;
import info.opencards.ui.CardFilesPreloader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class ExampleData {


    public static void installExampleData(OpenCards openCards) {

        File userHome = Utils.getUserHome();
        File exDataDir = new File(userHome, "OpenCards Examples");

        // do nothing if the directory is already present or it's not the first launch
        if (exDataDir.isDirectory() || Utils.getPrefs().getInt(Utils.PROP_STARTUP_COUNTER, 0) != 1)
            return;

        ResourceBundle rb = Utils.getRB();
        Object[] options = {rb.getString("ExampleData.addexdata"), rb.getString("UpdateChecker.discardupdate")};

        int status = JOptionPane.showOptionDialog(openCards,
                rb.getString("ExampleData.exampleswanted"),
                rb.getString("ExampleData.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                new ScaleableIcon("icons/oclogo.png", 100, 90),
                options,  //the titles of buttons
                options[0]);     //do not use a custom Icon

        if (status == JOptionPane.NO_OPTION)
            return;

        exDataDir.mkdir();

        File ocXMLDir = Utils.getOrCreateHiddenOCDirectory(exDataDir);

        try {
            final CardFileSerializer serializer = CardFileBackend.getBackend().getSerializer();
            Map<CardFile, Category> preloadBuffer = new HashMap<CardFile, Category>();

            // creat a new category for the example data
            final Category rootCategory = OpenCards.getCardSetManager().categoryPanel.getCatTree().getRootCategory();
            final Category exDataCategory = new Category(Utils.getRB().getString("ExampleData.exdataCategoryName"));
            rootCategory.addChildCategory(exDataCategory);


            // copy all example files from the bundle (eithe jar or classpath) to the new example data directory in the use home
            for (String fileName : Arrays.asList("English-German.ppt", "fruits.ppt", "math-basics.ppt")) {
                URL exDataFileURL = Utils.loadResource("examples/" + fileName);
                final File cardFile = new File(exDataDir, fileName);
                Utils.copyStreamIntoFile(cardFile, exDataFileURL.openStream());

                URL learnModelURL = Utils.loadResource("examples/" + fileName + ".xml");
                final File learnModelFile = new File(ocXMLDir, fileName + ".xml");
                Utils.copyStreamIntoFile(learnModelFile, learnModelURL.openStream());

                preloadBuffer.put(CardFileCache.getCardFile(cardFile), exDataCategory);
            }

            // now preload all files before registering them to the tree
            CardFilesPreloader preloader = new CardFilesPreloader(OpenCards.getInstance(), serializer);
            preloader.categorySelectionChanged(new ArrayList<CardFile>(preloadBuffer.keySet()), null);

            for (CardFile cardFile : preloadBuffer.keySet()) {
                // register the cardFile to the appropriate category
                preloadBuffer.get(cardFile).registerCardSet(cardFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
