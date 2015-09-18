package info.opencards.ui.lastmin;

import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.core.ItemCollection;
import info.opencards.learnstrats.leitner.LeitnerSystem;
import info.opencards.ui.AbstractLearnDialog;
import info.opencards.ui.actions.HelpAction;

import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class LeitnerLearnDialog extends AbstractLearnDialog {


    private LeitnerStatePanel boxPanel;


    public LeitnerLearnDialog() {
        scoreButtonsContainer.setLayout(new GridLayout(1, 2));

        // rename here directly if because with two buttons it should be always leitner
        scoreButtonsContainer.add(fiveButton);
        scoreButtonsContainer.add(oneButton);

        boxPanel = new LeitnerStatePanel();
        boxPanel.setPreferredSize(new Dimension(100, 140));
        boxPanel.setMinimumSize(new Dimension(0, 0));
        learnGraphContainer.add(boxPanel);

        invalidate();
    }


    @Override
    protected HelpAction getHelpAction() {
        return new HelpAction("last_minute.html");
    }


    public boolean postProcessKeyEvent(KeyEvent e) {
        if (super.postProcessKeyEvent(e))
            return true;

        if (!e.paramString().startsWith("KEY_RELEASED"))
            return false;

        if (isShowingComplete()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    fiveButton.getAction().actionPerformed(null);
                    break;
                case KeyEvent.VK_RIGHT:
                    oneButton.getAction().actionPerformed(null);
                    break;
            }

            return true;
        }

        return false;
    }


    @Override
    public void startFileSession(CardFile cardFile, ItemCollection cardItemCollection) {
        super.startFileSession(cardFile, cardItemCollection);

        setLeitnerSystem(cardFile.getFlashCards().getLeitnerItems());
    }


    protected void showCompleteCardButtonActionPerformed() {
        super.showCompleteCardButtonActionPerformed();
        fiveButton.requestFocusInWindow();
    }


    public void score(Item item) {
        super.score(item);

        // update the box-panel
        boxPanel.higlightItem(item);
    }


    public void setLeitnerSystem(LeitnerSystem leitnerSystem) {
        boxPanel.setLeitnerSystem(leitnerSystem);
    }
}
