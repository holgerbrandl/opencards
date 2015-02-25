/*
 * Created by JFormDesigner on Tue Aug 07 22:41:13 CEST 2007
 */

package info.opencards.ui.preferences;

import info.opencards.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class GlobLearnSettings extends AbstractSettingsPanel {


    public static final String USTM_DELAY = "learn.ltm.ustmDelay";
    public static final int USTM_DELAY_DEFAULT = 60;

    public static final String USE_SIMPLIFIED_INTERFACE = "learn.ltm.usesimpleui";
    private static final boolean USE_SIMPLIFIED_INTERFACE_DEFAULT = false;

    public static final String DO_SHOW_FEEBACK_TOTIPS = "learn.ltm.showFeedToTips";
    public static final boolean DO_SHOW_FEEBACK_TOTIPS_DEFAULT = false;

    public static final String DO_PRE_LTM_SYNC = "advncd.doPreLTMSync";
    public static final Boolean DO_PRE_LTM_SYNC_DEFAULT = false;

    public static final String NUM_ITEMS_PER_DAY = "learn.ltm.itemsperday";
    public static final int NUM_ITEMS_PER_DAY_DEFAULT = 30;


    public GlobLearnSettings() {
        initComponents();
    }


    void resetPanelSettings() {
        Utils.getPrefs().remove(USTM_DELAY);
        Utils.getPrefs().remove(USE_SIMPLIFIED_INTERFACE);
        Utils.getPrefs().remove(DO_SHOW_FEEBACK_TOTIPS);
        Utils.getPrefs().remove(NUM_ITEMS_PER_DAY);
        Utils.getPrefs().remove(DO_PRE_LTM_SYNC);

        loadDefaults();
    }


    void applySettingsChanges() {
        Utils.getPrefs().putInt(USTM_DELAY, (Integer) ustmDelaySpinner.getValue());
        Utils.getPrefs().putBoolean(USE_SIMPLIFIED_INTERFACE, useSimInterfaceCheckBox.isSelected());
        Utils.getPrefs().putBoolean(DO_SHOW_FEEBACK_TOTIPS, showFeedToTipsCheckBox.isSelected());
        Utils.getPrefs().putInt(NUM_ITEMS_PER_DAY, (Integer) itemsPerDaySpinner.getValue());
        Utils.getPrefs().putBoolean(DO_PRE_LTM_SYNC, doPreLTMSyncCheckBox.isSelected());
    }


    protected void loadDefaults() {
//         load the the settings of this dialog
        ustmDelaySpinner.setValue(Utils.getPrefs().getInt(USTM_DELAY, USTM_DELAY_DEFAULT));
        useSimInterfaceCheckBox.setSelected(Utils.getPrefs().getBoolean(USE_SIMPLIFIED_INTERFACE, USE_SIMPLIFIED_INTERFACE_DEFAULT));
        showFeedToTipsCheckBox.setSelected(Utils.getPrefs().getBoolean(DO_SHOW_FEEBACK_TOTIPS, DO_SHOW_FEEBACK_TOTIPS_DEFAULT));
        itemsPerDaySpinner.setValue(Utils.getPrefs().getInt(NUM_ITEMS_PER_DAY, NUM_ITEMS_PER_DAY_DEFAULT));
        doPreLTMSyncCheckBox.setSelected(Utils.getPrefs().getBoolean(DO_PRE_LTM_SYNC, DO_PRE_LTM_SYNC_DEFAULT));
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        panel2 = new JPanel();
        useSimInterfaceCheckBox = new JCheckBox();
        showFeedToTipsCheckBox = new JCheckBox();
        doPreLTMSyncCheckBox = new JCheckBox();
        schedulePanel = new JPanel();
        itemsPerDayLabel = new JLabel();
        hSpacer2 = new JPanel(null);
        itemsPerDaySpinner = new JSpinner();
        ustmPanel = new JPanel();
        ustmDelayTimeLabel = new JLabel();
        hSpacer1 = new JPanel(null);
        ustmDelaySpinner = new JSpinner();
        delaySecondsLabel = new JLabel();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0E-4};

        //======== panel2 ========
        {
            panel2.setBorder(new CompoundBorder(
                    new TitledBorder(null, bundle.getString("GlobLearnSettings.panel2.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{0, 15, 0, 27, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

            //---- useSimInterfaceCheckBox ----
            useSimInterfaceCheckBox.setText(bundle.getString("GlobLearnSettings.useSimInterfaceCheckBox.text"));
            panel2.add(useSimInterfaceCheckBox, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- showFeedToTipsCheckBox ----
            showFeedToTipsCheckBox.setText(bundle.getString("GlobLearnSettings.showFeedToTipsCheckBox.text"));
            panel2.add(showFeedToTipsCheckBox, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- doPreLTMSyncCheckBox ----
            doPreLTMSyncCheckBox.setText(bundle.getString("GlobLearnSettings.doPreLTMSyncCheckBox.text"));
            panel2.add(doPreLTMSyncCheckBox, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== schedulePanel ========
        {
            schedulePanel.setBorder(new CompoundBorder(
                    new TitledBorder(null, bundle.getString("GlobLearnSettings.schedulePanel.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            schedulePanel.setLayout(new GridBagLayout());
            ((GridBagLayout) schedulePanel.getLayout()).columnWidths = new int[]{0, 15, 0, 0, 0};
            ((GridBagLayout) schedulePanel.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) schedulePanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) schedulePanel.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

            //---- itemsPerDayLabel ----
            itemsPerDayLabel.setText(bundle.getString("GlobLearnSettings.itemsPerDayLabel.text"));
            schedulePanel.add(itemsPerDayLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
            schedulePanel.add(hSpacer2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

            //---- itemsPerDaySpinner ----
            itemsPerDaySpinner.setModel(new SpinnerNumberModel(10, 1, 9999, 1));
            itemsPerDaySpinner.setMinimumSize(null);
            itemsPerDaySpinner.setPreferredSize(null);
            schedulePanel.add(itemsPerDaySpinner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
        }
        add(schedulePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== ustmPanel ========
        {
            ustmPanel.setBorder(new CompoundBorder(
                    new TitledBorder(null, bundle.getString("GlobLearnSettings.ustmPanel.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            ustmPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) ustmPanel.getLayout()).columnWidths = new int[]{0, 15, 0, 0, 0};
            ((GridBagLayout) ustmPanel.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) ustmPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) ustmPanel.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

            //---- ustmDelayTimeLabel ----
            ustmDelayTimeLabel.setText(bundle.getString("GlobLearnSettings.ustmDelayTimeLabel.text"));
            ustmPanel.add(ustmDelayTimeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
            ustmPanel.add(hSpacer1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

            //---- ustmDelaySpinner ----
            ustmDelaySpinner.setModel(new SpinnerNumberModel(30, 10, 999, 5));
            ustmPanel.add(ustmDelaySpinner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

            //---- delaySecondsLabel ----
            delaySecondsLabel.setText(bundle.getString("GlobLearnSettings.delaySecondsLabel.text"));
            ustmPanel.add(delaySecondsLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(ustmPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        loadDefaults();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel2;
    private JCheckBox useSimInterfaceCheckBox;
    private JCheckBox showFeedToTipsCheckBox;
    private JCheckBox doPreLTMSyncCheckBox;
    private JPanel schedulePanel;
    private JLabel itemsPerDayLabel;
    private JPanel hSpacer2;
    private JSpinner itemsPerDaySpinner;
    private JPanel ustmPanel;
    private JLabel ustmDelayTimeLabel;
    private JPanel hSpacer1;
    private JSpinner ustmDelaySpinner;
    private JLabel delaySecondsLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
