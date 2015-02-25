package info.opencards.ui;

import info.opencards.core.CardFile;

import java.util.List;


/**
 * A small API which provides all methods required to process changes in the selection of <code>CardFile</code>s.
 *
 * @author Holger Brandl
 */
public interface CardFileSelectionListener {


    public void cardFileSelectionChanged(List<CardFile> curSelCardFiles);

}
