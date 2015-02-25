/*
 * Created by JFormDesigner on Tue Aug 07 22:41:13 CEST 2007
 */

package info.opencards.ui.preferences;

import info.opencards.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class LeitnerSettings extends AbstractSettingsPanel {


    public static final String NUM_LEITNER_BOXES = "learn.leitner.numboxes";
    public static final String INIT_LEITNER_BOXES = "learn.leitner.initbox";
    public static final int NUM_LEITNER_BOXES_DEFAULT = 5;
    public static final int INIT_LEITNER_BOXES_DEFAULT = 1;

    public static final String DO_PREFER_UNLEARNT = "learn.leitner.preferlesslearnt";
    public static final boolean DO_PREFER_UNLEARNT_DEFAULT = false;
    public static final String PREFER_UNLEARNT_AMOUNT = "learn.leitner.preferamount";
    public static final int PREFER_UNLEARNT_DEFAULT = 1;

    public static final String DO_MOVE2_FIRST = "learn.leitner.domove1st";
    public static final boolean DO_MOVE2_FIRST_DEFAULT = false;


    public LeitnerSettings() {
        initComponents();
    }


    void resetPanelSettings() {
        Utils.getPrefs().remove(NUM_LEITNER_BOXES);
        Utils.getPrefs().remove(INIT_LEITNER_BOXES);
        Utils.getPrefs().remove(DO_PREFER_UNLEARNT);
        Utils.getPrefs().remove(DO_MOVE2_FIRST);
        Utils.getPrefs().remove(PREFER_UNLEARNT_AMOUNT);

        loadDefaults();
    }


    void applySettingsChanges() {
        Utils.getPrefs().putInt(NUM_LEITNER_BOXES, (Integer) numBoxesSpinner.getValue());
        Utils.getPrefs().putInt(INIT_LEITNER_BOXES, (Integer) startBoxSpinner.getValue());

        Utils.getPrefs().putBoolean(DO_PREFER_UNLEARNT, preferUnlearntBox.isSelected());
        Utils.getPrefs().putBoolean(DO_MOVE2_FIRST, move2FirstCheckBox.isSelected());
//        Utils.getPrefs().putInt(PREFER_UNLEARNT_AMOUNT, incProbAmountCombo.getSelectedIndex());
    }


    protected void loadDefaults() {
        // load the the settings of this dialog
        numBoxesSpinner.setValue(Utils.getPrefs().getInt(NUM_LEITNER_BOXES, NUM_LEITNER_BOXES_DEFAULT));
        startBoxSpinner.setValue(Utils.getPrefs().getInt(INIT_LEITNER_BOXES, INIT_LEITNER_BOXES_DEFAULT));
        numBoxesSpinnerStateChanged();

        preferUnlearntBox.setSelected(Utils.getPrefs().getBoolean(DO_PREFER_UNLEARNT, DO_PREFER_UNLEARNT_DEFAULT));
        move2FirstCheckBox.setSelected(Utils.getPrefs().getBoolean(DO_MOVE2_FIRST, DO_MOVE2_FIRST_DEFAULT));
    }


    private void numBoxesSpinnerStateChanged() {
        int numBoxes = (Integer) numBoxesSpinner.getValue();
        int curStartBox = (Integer) startBoxSpinner.getValue();

        if (numBoxes <= curStartBox)
            startBoxSpinner.setValue(curStartBox - 1);

        ((SpinnerNumberModel) startBoxSpinner.getModel()).setMaximum(numBoxes - 1);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        boxPanel = new JPanel();
        numBoxedLabel = new JLabel();
        numBoxesSpinner = new JSpinner();
        initBoxLabel = new JLabel();
        startBoxSpinner = new JSpinner();
        learnPanel = new JPanel();
        move2FirstCheckBox = new JCheckBox();
        preferUnlearntBox = new JCheckBox();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0E-4};

        //======== boxPanel ========
        {
            boxPanel.setBorder(new CompoundBorder(
                    new TitledBorder(null, bundle.getString("LeitnerSettings.boxPanel.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            boxPanel.setMinimumSize(null);
            boxPanel.setPreferredSize(null);
            boxPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) boxPanel.getLayout()).columnWidths = new int[]{0, 48, 27, 0};
            ((GridBagLayout) boxPanel.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) boxPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) boxPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

            //---- numBoxedLabel ----
            numBoxedLabel.setText(bundle.getString("LeitnerSettings.numBoxedLabel.text"));
            boxPanel.add(numBoxedLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- numBoxesSpinner ----
            numBoxesSpinner.setModel(new SpinnerNumberModel(5, 2, 20, 1));
            numBoxesSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    numBoxesSpinnerStateChanged();
                }
            });
            boxPanel.add(numBoxesSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- initBoxLabel ----
            initBoxLabel.setText(bundle.getString("LeitnerSettings.initBoxLabel.text"));
            boxPanel.add(initBoxLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

            //---- startBoxSpinner ----
            startBoxSpinner.setModel(new SpinnerNumberModel(1, 1, 20, 1));
            boxPanel.add(startBoxSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
        }
        add(boxPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //======== learnPanel ========
        {
            learnPanel.setBorder(new CompoundBorder(
                    new TitledBorder(null, bundle.getString("LeitnerSettings.learnPanel.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            learnPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) learnPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
            ((GridBagLayout) learnPanel.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) learnPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) learnPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

            //---- move2FirstCheckBox ----
            move2FirstCheckBox.setText(bundle.getString("LeitnerSettings.move2FirstCheckBox.text"));
            learnPanel.add(move2FirstCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- preferUnlearntBox ----
            preferUnlearntBox.setText(bundle.getString("LeitnerSettings.preferUnlearntBox.text"));
            learnPanel.add(preferUnlearntBox, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(learnPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        loadDefaults();


    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel boxPanel;
    private JLabel numBoxedLabel;
    private JSpinner numBoxesSpinner;
    private JLabel initBoxLabel;
    private JSpinner startBoxSpinner;
    private JPanel learnPanel;
    private JCheckBox move2FirstCheckBox;
    private JCheckBox preferUnlearntBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
