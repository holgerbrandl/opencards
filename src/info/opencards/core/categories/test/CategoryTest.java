package info.opencards.core.categories.test;


import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class CategoryTest {


    @Test
    public void testCatMoveAndFlattening() {
        Category root = new Category("All");
        Category math = new Category("Math");
        root.addChildCategory(math);

        Category music = new Category("Music");
        music.registerCardSet(new CardFile(new File("test0.odp")));
        music.addChildCategory(new Category("black"));
        music.addChildCategory(new Category("r&b"));

        Category pop = new Category("pop");
        pop.registerCardSet(new CardFile(new File("test1.odp")));
        pop.registerCardSet(new CardFile(new File("test2.odp")));
        music.addChildCategory(pop);
        root.addChildCategory(music);

        System.out.println("root is " + root);

        System.out.println("moving pop to math...");

        pop.relocate(math);

        Assert.assertTrue(math.getChildCategories().size() == 1);
        Assert.assertTrue(music.getChildCategories().size() == 2);

        System.out.println("root is " + root);

        root.flattenChild(math);
        root.flattenChild(pop);

        Assert.assertTrue(root.getChildCategories().size() == 1);
        Assert.assertTrue(root.getCardSets().size() == 2);

        System.out.println("root after flattening math is " + root);
    }
}
