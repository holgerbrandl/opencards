/*
 * Created by JFormDesigner on Tue Dec 18 21:37:26 CET 2007
 */

package info.opencards.ui.ltmstats;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.ui.CardFileSelectionListener;
import info.opencards.ui.preferences.AdvancedSettings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class LongTermMemStatsPanel extends JTabbedPane implements CardFileSelectionListener {


    private static final String PREFERRED_STATS_TAB = "ltmmanager.prefTab";
    private AdvancedLtmStatsPanel advncdStatsPanel;


    public LongTermMemStatsPanel() {
        initComponents();

        if (Utils.getPrefs().getBoolean(AdvancedSettings.SHOW_ADVNCD_LTM_STATS, AdvancedSettings.SHOW_ADVNCD_LTM_STATS_DEFAULT)) {
            advncdStatsPanel = new AdvancedLtmStatsPanel();
            addTab("Advanced Statistics", advncdStatsPanel);

        }

        setSelectedIndex(Utils.getPrefs().getInt(PREFERRED_STATS_TAB, 0));

        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                tabChanged();
            }
        });
    }


    private void tabChanged() {
        // make the currenlty tab persistant (if it is not the advanced-tab)
        if (getSelectedIndex() < 1)
            Utils.getPrefs().putInt(PREFERRED_STATS_TAB, getSelectedIndex());
    }


    public void cardFileSelectionChanged(List<CardFile> curSelCardFiles) {
        lTMStateGraphPanel.selectionChanged(curSelCardFiles);
        schedulePlanningPanel.selectionChanged(curSelCardFiles);

        if (advncdStatsPanel != null)
            advncdStatsPanel.selectionChanged(curSelCardFiles);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        schedulePlanningPanel = new SchedulePlanningPanel();
        lTMStateGraphPanel = new LTMStateGraphPanel();

        //======== this ========
        addTab(bundle.getString("LongTermMemStatsPanel.schedulePlanningPanel.tab.title"), schedulePlanningPanel);

        addTab(bundle.getString("LongTermMemStatsPanel.learnsuccess.title"), lTMStateGraphPanel);

        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private SchedulePlanningPanel schedulePlanningPanel;
    private LTMStateGraphPanel lTMStateGraphPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
