package info.opencards.ui;

import info.opencards.Utils;
import info.opencards.learnstrats.ltm.LTMItem;
import info.opencards.learnstrats.ltm.ScheduleUtils;
import info.opencards.ui.preferences.GlobLearnSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public class SMLearnDialog extends AbstractLearnDialog {


    private final Map<Integer, Boolean> keystates = new HashMap<Integer, Boolean>();


    public SMLearnDialog() {
        boolean useSimpleInterface = Utils.getPrefs().getBoolean(GlobLearnSettings.USE_SIMPLIFIED_INTERFACE, false);
        scoreButtonsContainer.setLayout(new GridLayout(1, useSimpleInterface ? 3 : 5));

        if (useSimpleInterface) {
            scoreButtonsContainer.add(fiveButton);
            scoreButtonsContainer.add(threeButton);
            scoreButtonsContainer.add(oneButton);
        } else {
            scoreButtonsContainer.add(fiveButton);
            scoreButtonsContainer.add(fourButton);
            scoreButtonsContainer.add(threeButton);
            scoreButtonsContainer.add(twoButton);
            scoreButtonsContainer.add(oneButton);
        }

        invalidate();

//        validateTree();
    }


    public boolean postProcessKeyEvent(KeyEvent e) {
//        super.postProcessKeyEvent(e);
        if (super.postProcessKeyEvent(e))
            return true;

        int keyCode = e.getKeyCode();

        if (!isShowingComplete())
            return false;

        if (e.paramString().startsWith("KEY_RELEASED")) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_RIGHT:
                    keystates.put(keyCode, false);
            }

            // test wether only one arrow-key was pressed and became released now
            Set<Integer> curKeys = keystates.keySet();
            if (keystates.size() == 1 && Utils.isAllFalse(keystates.values())) {
                switch (curKeys.iterator().next()) {
                    case KeyEvent.VK_LEFT:
                        fiveButton.getAction().actionPerformed(null);
                        keystates.clear();
                        break;
                    case KeyEvent.VK_DOWN:
                        threeButton.getAction().actionPerformed(null);
                        keystates.clear();
                        break;
                    case KeyEvent.VK_RIGHT:
                        oneButton.getAction().actionPerformed(null);
                        keystates.clear();
                        break;
                }

            } else if (keystates.size() == 2 && Utils.isAllFalse(keystates.values())) {
                if (!keystates.containsKey(KeyEvent.VK_DOWN)) {
                    keystates.clear();
                } else {
                    if (curKeys.contains(KeyEvent.VK_LEFT)) {
                        fourButton.getAction().actionPerformed(null);
                        keystates.clear();
                    } else if (curKeys.contains(KeyEvent.VK_RIGHT)) {
                        twoButton.getAction().actionPerformed(null);
                        keystates.clear();
                    }
                }
            }
        } else {
            // reset the keystate if some other keys were pressed before which didn't became
            if (Utils.isAllFalse(keystates.values()))
                keystates.clear();

            // update the current keystate
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_RIGHT:
                    keystates.put(keyCode, true);
            }
        }

        return false;
    }


    public double getSizeRatio() {
        return (double) getWidth() / (double) getHeight();
    }


    protected void showCompleteCardButtonActionPerformed() {
        super.showCompleteCardButtonActionPerformed();
        threeButton.requestFocusInWindow();
        keystates.clear();

        // set meaningful tooltips for the feedback-buttons if the option was selected in the OC-settings
        boolean showFeedbackToTips = Utils.getPrefs().getBoolean(GlobLearnSettings.DO_SHOW_FEEBACK_TOTIPS, GlobLearnSettings.DO_SHOW_FEEBACK_TOTIPS_DEFAULT);
        if (showFeedbackToTips) {
            List<JButton> fbButtons = Arrays.asList(oneButton, twoButton, threeButton, fourButton, fiveButton);
            LTMItem ltmItem = (LTMItem) curItem;

            Map<Integer, Integer> logIncDaysList = new LinkedHashMap<Integer, Integer>();

            for (JButton fbButton : fbButtons) {
                int actionScore = ((ScoreAction) fbButton.getAction()).getActionScore();
                LTMItem cloneItem = (LTMItem) ltmItem.clone();
                cloneItem.updateEFactor(actionScore);

                int days = ScheduleUtils.getDayDiff(cloneItem.getNextScheduledDate(), ScheduleUtils.getToday());
                logIncDaysList.put(actionScore, days);

                // set the appropriate tooltip
                String toolTipText;
                String nextTest = "--> next test in " + days + " days";
                ResourceBundle rb = Utils.getRB();

                switch (actionScore) {
                    case 1:
                        toolTipText = rb.getString("SMLearnDialog.feedback.notatall");
                        break;
                    case 2:
                        toolTipText = rb.getString("SMLearnDialog.feedback.hardly");
                        break;
                    case 3:
                        toolTipText = rb.getString("SMLearnDialog.feedback.soso") + nextTest;
                        break;
                    case 4:
                        toolTipText = rb.getString("SMLearnDialog.feedback.well") + nextTest;
                        break;
                    case 5:
                        toolTipText = rb.getString("SMLearnDialog.feedback.perfectly") + nextTest;
                        break;
                    default:
                        toolTipText = "!!!invalid feeback-code!!!";
                }

                fbButton.setToolTipText(toolTipText);
            }

            // print a debug message to the log-monitor
            StringBuilder sb = new StringBuilder();
            for (Integer actionCode : logIncDaysList.keySet()) {
                sb.append(actionCode + "->" + logIncDaysList.get(actionCode) + ";");
            }
            Utils.log(sb.toString());
        }
    }
}
