/*
 * Created by JFormDesigner on Tue Aug 07 23:27:58 CEST 2007
 */

package info.opencards.ui.preferences;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;


/**
 * @author Holger Brandl
 */
public class EffectSettings extends AbstractSettingsPanel {


    public EffectSettings() {
        initComponents();
    }


    void resetPanelSettings() {
    }


    void applySettingsChanges() {
    }


    protected void loadDefaults() {
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel2 = new JPanel();
        noTransRadio = new JRadioButton();
        presDefTransRadio = new JRadioButton();
        randTransRadio = new JRadioButton();
        selTransRadio = new JRadioButton();
        effectCombo = new JComboBox();
        panel3 = new JPanel();
        radioButton5 = new JRadioButton();
        radioButton6 = new JRadioButton();
        radioButton7 = new JRadioButton();
        radioButton8 = new JRadioButton();
        effectCombo2 = new JComboBox();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};

        //======== panel2 ========
        {
            panel2.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Card transistion effects", TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{0, 0, 90, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- noTransRadio ----
            noTransRadio.setText("No transition effects");
            noTransRadio.setSelected(true);
            panel2.add(noTransRadio, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- presDefTransRadio ----
            presDefTransRadio.setText("Use presentation defaults");
            panel2.add(presDefTransRadio, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- randTransRadio ----
            randTransRadio.setText("Random effect");
            panel2.add(randTransRadio, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- selTransRadio ----
            selTransRadio.setText("Select effect");
            panel2.add(selTransRadio, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));
            panel2.add(effectCombo, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== panel3 ========
        {
            panel3.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Answer showup effects", TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- radioButton5 ----
            radioButton5.setText("No transition effects");
            panel3.add(radioButton5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- radioButton6 ----
            radioButton6.setText("Use presentation defaults");
            panel3.add(radioButton6, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- radioButton7 ----
            radioButton7.setText("Random effect");
            panel3.add(radioButton7, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- radioButton8 ----
            radioButton8.setText("select effect");
            panel3.add(radioButton8, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));
            panel3.add(effectCombo2, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //---- cardTransEffectsGroup ----
        ButtonGroup cardTransEffectsGroup = new ButtonGroup();
        cardTransEffectsGroup.add(noTransRadio);
        cardTransEffectsGroup.add(presDefTransRadio);
        cardTransEffectsGroup.add(randTransRadio);
        cardTransEffectsGroup.add(selTransRadio);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel2;
    private JRadioButton noTransRadio;
    private JRadioButton presDefTransRadio;
    private JRadioButton randTransRadio;
    private JRadioButton selTransRadio;
    private JComboBox effectCombo;
    private JPanel panel3;
    private JRadioButton radioButton5;
    private JRadioButton radioButton6;
    private JRadioButton radioButton7;
    private JRadioButton radioButton8;
    private JComboBox effectCombo2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
