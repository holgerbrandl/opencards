package info.opencards.ui.actions;

import info.opencards.CardFileBackend;
import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.*;
import info.opencards.learnstrats.ltm.LTMProcessManager;
import info.opencards.learnstrats.ltm.RefreshProcessManager;
import info.opencards.learnstrats.ltm.SM2;
import info.opencards.ui.CardFileSelectionListener;
import info.opencards.ui.LearnManagerUI;
import info.opencards.ui.SMLearnDialog;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class StartLearningAction extends AbstractAction implements CardFileSelectionListener {


    private final LearnManagerUI learnManUI;

    private List<CardFile> curSelCardFiles;

    private boolean wasAltPressed;
    private boolean wasCtrlPressed;


    public StartLearningAction(LearnManagerUI learnManUI) {
        this.learnManUI = learnManUI;

        updateModKeyState();
    }


    public void updateModKeyState() {
        // update the name of the action
        ResourceBundle rb = Utils.getRB();

        if (learnManUI.isCtrlPressed()) {
            String newFlag = " +'" + rb.getString("CardTableModel.numNewCards").replace("#", "").trim() + "'";
            putValue(NAME, rb.getString("StartLearningAction.StartLearning") + newFlag);
        } else if (learnManUI.isShiftPressed()) {
            putValue(NAME, rb.getString("StartLearningAction.StartRefreshing"));
        } else {
            putValue(NAME, rb.getString("StartLearningAction.StartLearning"));
        }
    }


    public void actionPerformed(ActionEvent e) {
        wasAltPressed = learnManUI.isShiftPressed();
        wasCtrlPressed = learnManUI.isCtrlPressed();

        // start the learning session
        learnCardFiles(curSelCardFiles);
    }


    public void cardFileSelectionChanged(List<CardFile> curSelCardFiles) {
        this.curSelCardFiles = curSelCardFiles;
    }


    void learnCardFiles(final Collection<CardFile> curFiles) {
        Utils.log("initiating learning session for: " + curFiles.toString());

        //todo disabled for now, should be removed properly later
//        // first synchronize files if preLTM-Syncing is enabled in the OC-settings
//        boolean doPreLtmSync = Utils.getPrefs().getBoolean(GlobLearnSettings.DO_PRE_LTM_SYNC, GlobLearnSettings.DO_PRE_LTM_SYNC_DEFAULT);
//        if (doPreLtmSync) {
//            CardFileSyncAction syncAction = new CardFileSyncAction(null, null);
//            syncAction.setCardFiles(new ArrayList<CardFile>(curFiles));
//
//            Utils.log("running pre-session synchronization");
//            syncAction.actionPerformed(null);
//
//            // wait for the dialog to finish (this will block the ui but anyway
//            while (syncAction.getSyncDialog().isVisible()) {
//                Utils.sleep(100);
//            }
//        }

        // now learn the cards
        final ItemValuater itemValuater = new SMLearnDialog();
        final LearnMethodFactory factory = SM2.getFactory();

        // select between refreshing and real ltm-learning
        final LTMProcessManager processManager;
        if (wasAltPressed)
            processManager = new RefreshProcessManager(itemValuater, factory);
        else {
            processManager = new LTMProcessManager(itemValuater, factory);
            processManager.setIncludeAllNew(wasCtrlPressed);
        }

        OpenCards.getInstance().getLearnPanel().setControls(itemValuater);
        OpenCards.showLearnView();

        Utils.log("setuping schedule");
        processManager.setupSchedule(curFiles);

        processManager.addLearnProcessManagerProcessListener(new LearnProcListener() {
            public void processFinished(boolean wasInterrupted) {
                Utils.log("processFinished interrupt=" + wasInterrupted);

                if (!wasInterrupted) {
                    // check if there were any new cards detected (if this is the case ask whether these files should be
                    // learned now)

                    // Note: it is a known defect, that cards which were added
                    // a) while learning cards
                    // AND
                    // b) where added behind (in terms of slide index) after the slides queried slides after creation
                    // ... WON'T be detected. The cause is the only optional sync which is used to keep OC in sync
                    // with changes in the impress-presentation.
                    //  Although this is a minor bug, we probably won't fix it to avoid the necessary (and performance
                    //  decreasing) synchronize-operation.

//                    LTMProcessManager newCardsDummyScheduler = new LTMProcessManager(itemValuater, factory);
                    final Map<CardFile, List<Item>> skippedItems = new HashMap<CardFile, List<Item>>(processManager.getSkippedItems());

                    processManager.setupSchedule(curFiles);

                    // remove items from scheduler that were skipped for this session
                    for (CardFile fileWithSkippedItems : skippedItems.keySet()) {
                        List<Item> newFileItems = processManager.getScheduledItems(fileWithSkippedItems);

                        if (newFileItems == null)
                            continue;

                        for (Item skippedItem : skippedItems.get(fileWithSkippedItems)) {
                            processManager.unschdeduleItem(fileWithSkippedItems, skippedItem);
                        }
                    }

                    if (processManager.hasScheduledItems()) {
                        Utils.log("found new items after session: " + processManager.toString());

                        String newCardsMsg = Utils.getRB().getString("StartLearningAction.newCardsDetected.msg");
                        String newCardsTitle = Utils.getRB().getString("StartLearningAction.newCardsDetected.title");
                        int answer = JOptionPane.showConfirmDialog(null, newCardsMsg, newCardsTitle, JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            // we do the most straighforward thing here which is to use the same approach
                            // again: we just instantiate another StartLearningAction and trigger it immediately
//                            new StartLearningAction(learnManUI, backend).learnCardFiles(curFiles);
                            processManager.startProcessing();
                            return; // necessary because otherwise the learnMainUI would be made visible
                        }
                    }
                }

                // make the gui visible again
                OpenCards.showCategoryView();
            }
        });

        processManager.startProcessing();

        // this is (and needs to be!!) all non-blocking because we're in the awt-thread while running this method!
    }


    @Test
    public void testStartLearning() {
        CardFileBackend backend = CardFileBackend.getBackend();
        File tempFile = Utils.createTempCopy(new File("testdata/experimental design.ppt"));
        CardFile cardFile = new CardFile(tempFile);

        cardFile.setSerializer(backend.getSerializer());

        StartLearningAction learningAction = new StartLearningAction(null);
        learningAction.learnCardFiles(new ArrayList<CardFile>(Arrays.asList(cardFile)));
    }
}
