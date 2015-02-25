package info.opencards.learnstrats.ltm.eval;

import info.opencards.core.FlashCard;
import info.opencards.learnstrats.ltm.LTMCollection;
import info.opencards.learnstrats.ltm.LTMItem;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class Eval2 {


    public static void main(String[] args) {
        LTMCollection items = new LTMCollection();
        LTMItem ltmItem = new LTMItem(new FlashCard(1, "A", 1));
        items.add(ltmItem);

        for (int i = 0; i < 10; i++) {
            ltmItem.updateEFactor(4);
            System.out.println("\n after i=" + i + " the item was: " + LTMItem.exhaustiveStringDump(ltmItem));
        }
    }

}
