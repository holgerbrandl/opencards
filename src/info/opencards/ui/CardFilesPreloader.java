package info.opencards.ui;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.LearnStatusSerializer;
import info.opencards.core.categories.Category;
import info.opencards.ui.catui.CategoryTreeSelectionListener;
import info.opencards.util.InvalidCardFileFormatException;
import info.opencards.util.UIUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class CardFilesPreloader implements CategoryTreeSelectionListener {


    private final Container awtOwner;
    private final LearnStatusSerializer serializer;


    public CardFilesPreloader(Container awtOwner, LearnStatusSerializer serializer) {
        this.awtOwner = awtOwner;
        this.serializer = serializer;
    }


    public void categorySelectionChanged(final java.util.List<CardFile> selectedFiles, Set<Category> selCategories) {
        int nonEmptyFiles = 0;
        for (CardFile curFile : selectedFiles) {
            if (!curFile.isDeserialized()) {
                nonEmptyFiles++;
            }
        }

        if (nonEmptyFiles == 0)
            return;

        final JProgressBar bar = new JProgressBar(1, 100);
        bar.setIndeterminate(true);
        bar.setStringPainted(true);
        bar.setFont(bar.getFont().deriveFont(bar.getFont().getStyle() | Font.BOLD));

//        final JButton cancelLoading = new JButton();
        final JDialog awtOwner = UIUtils.getOwnerDialog(this.awtOwner);
        final JDialog dialog = new JDialog(awtOwner);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(bar, BorderLayout.CENTER);
//        dialog.getContentPane().add(cancelLoading, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);

        dialog.setTitle(Utils.getRB().getString("CardFilesPreloader.loadingLearnStates") + "...");

        Dimension dim = new Dimension(300, 50);
        dialog.setPreferredSize(dim);
        dialog.setSize(dim);
        dialog.validate();

        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        new Thread() {

            public void run() {

                // collect new cardfiles in order to test the card-hash after loading
                List<CardFile> nonValidatedHashFiles = new ArrayList<CardFile>();

                for (CardFile curFile : selectedFiles) {
                    if (curFile.getFileLocation() == null)
                        continue;

                    bar.setString(curFile.getFileLocation().getName() + "");
                    if (curFile.getSerializer() == null) {
                        curFile.setSerializer(serializer);
                        nonValidatedHashFiles.add(curFile);
                    }

                    try {
                        curFile.getFlashCards();
                    } catch (OfficeXmlFileException e) {
                        Utils.log(e.toString());
                        showErrorMsgDialog(curFile, dialog, "Office2007 file format 'pptx' is not yet supported. Save file as proper ppt and try again");
                    } catch (InvalidCardFileFormatException e) {
                        Utils.log(e.toString());
                        showErrorMsgDialog(curFile, dialog, "File does not exist or is not a valid PowerPoint ppt-file.");
                    }
                }

                dialog.dispose();
            }
        }.start();

        dialog.setVisible(true);
    }


    private void showErrorMsgDialog(CardFile curFile, JDialog dialog, String cause) {
        String msg = "Failed to load '" + curFile + "' because:\n" + cause;
        JOptionPane.showConfirmDialog(dialog, msg, "Could not read flashcard file", JOptionPane.ERROR_MESSAGE);

        // remove file because it is invalid and inform the user
        Collection<Category> curFileCats = new ArrayList<Category>(curFile.belongsTo());
        for (Category curFileCat : curFileCats) {
            curFileCat.unregisterCardSet(curFile);
        }
    }

}
