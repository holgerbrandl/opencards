package info.opencards.ui.actions;

import info.opencards.pptintegration.conversion.ImportManager;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class ImportFlashcardsAction extends AbstractAction {


    private final JFrame parentUI;


    public ImportFlashcardsAction(JFrame parentUI) {
        putValue(NAME, "Import Flashcards");
        this.parentUI = parentUI;
    }


    public void actionPerformed(ActionEvent e) {
        new ImportManager(parentUI);
    }
}
