package info.opencards.learnstrats.ltm.eval;

import info.opencards.core.FlashCard;
import info.opencards.learnstrats.ltm.LTMCollection;
import info.opencards.learnstrats.ltm.LTMItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class EvalSM {


    public static void main(String[] args) throws IOException {
        LTMCollection items = new LTMCollection();
        LTMItem ltmItem = new LTMItem(new FlashCard(1, "A", 1));
        items.add(ltmItem);

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String input;
        System.out.println("starting eval of " + ltmItem.getFlashCard());
        while (!(input = stdin.readLine()).equals("q")) {
            if (input.equals("r")) {
                ltmItem.reset();
                System.out.println("item reset");
                continue;
            }

            int feedback = -Integer.parseInt(input) + 6;
            ltmItem.updateEFactor(feedback);

            System.out.println("ltm after update with " + feedback + " was " + LTMItem.exhaustiveStringDump(ltmItem));
        }
    }
}
