package info.opencards.core;

import info.opencards.OpenCards;

public abstract class AbstractSlideManager implements SlideManager {
    protected CardFile curCardFile;


    public void stopLearnSession() {
        // set the current file to null
        curCardFile = null;

        // conceptually this should be done in stopFileSession but this would cause some flickering when changing between cardsets
        OpenCards.getInstance().resetWindowTitle();
    }


    public CardFile getCurCardFile() {
        return curCardFile;
    }
}
