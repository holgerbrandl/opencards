/*
 * Created by JFormDesigner on Fri Dec 14 20:43:28 CET 2007
 */

package info.opencards.ui.preferences;

import info.opencards.Utils;
import info.opencards.core.LearnMethod;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class GeneralSettings extends AbstractSettingsPanel {


    public static final String SKIPPING_BEHAVIOR = "advncd.general.skipbehavior";
    public static final int SKIPPING_BEHAVIOR_DEFAULT = LearnMethod.SKIP_UNTIL_NEXT;

    private final Map<ButtonModel, Integer> skipMap;


    public GeneralSettings() {
        initComponents();

        skipMap = new HashMap<ButtonModel, Integer>();
        skipMap.put(sesSkipRadio.getModel(), LearnMethod.SKIP_BUT_KEEP);
        skipMap.put(nextSesSkipRadio.getModel(), LearnMethod.SKIP_UNTIL_NEXT);
        skipMap.put(tomorrowSkipRadio.getModel(), LearnMethod.SKIP_UNTIL_TOMORROW);
    }


    void resetPanelSettings() {
        Utils.getPrefs().remove(SKIPPING_BEHAVIOR);

        loadDefaults();
    }


    void applySettingsChanges() {
//        Utils.getPrefs().putBoolean(SHOW_NOT_ADDED_WARNING, usePerSetsBox.isSelected());

        Utils.getPrefs().putInt(SKIPPING_BEHAVIOR, skipMap.get(skipGroup.getSelection()));
    }


    protected void loadDefaults() {
//        usePerSetsBox.setSelected(Utils.getPrefs().getBoolean(SHOW_NOT_ADDED_WARNING, SHOW_NOT_ADDED_WARNING_DEFAULT));

        int defaultSkipBehavior = Utils.getPrefs().getInt(SKIPPING_BEHAVIOR, SKIPPING_BEHAVIOR_DEFAULT);
        switch (defaultSkipBehavior) {
            case LearnMethod.SKIP_BUT_KEEP:
                sesSkipRadio.setSelected(true);
                break;
            case LearnMethod.SKIP_UNTIL_NEXT:
                nextSesSkipRadio.setSelected(true);
                break;
            case LearnMethod.SKIP_UNTIL_TOMORROW:
                tomorrowSkipRadio.setSelected(true);
                break;
            default:
                throw new RuntimeException("invalid skipping behavior");
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        skippSettingsPanel = new JPanel();
        sesSkipRadio = new JRadioButton();
        nextSesSkipRadio = new JRadioButton();
        tomorrowSkipRadio = new JRadioButton();
        panel4 = new JPanel();
        skipGroup = new ButtonGroup();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};

        //======== skippSettingsPanel ========
        {
            skippSettingsPanel.setBorder(new CompoundBorder(
                    new TitledBorder(null, bundle.getString("GeneralSettings.skipSettingsPanel.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            skippSettingsPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) skippSettingsPanel.getLayout()).columnWidths = new int[]{0, 0, 0};
            ((GridBagLayout) skippSettingsPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) skippSettingsPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
            ((GridBagLayout) skippSettingsPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

            //---- sesSkipRadio ----
            sesSkipRadio.setText(bundle.getString("GeneralSettings.sesSkipRadio.text"));
            skippSettingsPanel.add(sesSkipRadio, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- nextSesSkipRadio ----
            nextSesSkipRadio.setText(bundle.getString("GeneralSettings.nextSesSkipRadio.text"));
            skippSettingsPanel.add(nextSesSkipRadio, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- tomorrowSkipRadio ----
            tomorrowSkipRadio.setText(bundle.getString("GeneralSettings.tomorrowSkipRadio.text"));
            skippSettingsPanel.add(tomorrowSkipRadio, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
        }
        add(skippSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== panel4 ========
        {
            panel4.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Warnings", TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[]{165, 91, 0, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};
        }

        //---- skipGroup ----
        skipGroup.add(sesSkipRadio);
        skipGroup.add(nextSesSkipRadio);
        skipGroup.add(tomorrowSkipRadio);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        loadDefaults();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel skippSettingsPanel;
    private JRadioButton sesSkipRadio;
    private JRadioButton nextSesSkipRadio;
    private JRadioButton tomorrowSkipRadio;
    private JPanel panel4;
    private ButtonGroup skipGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
