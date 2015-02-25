/*
 * Created by JFormDesigner on Wed Aug 08 22:26:07 CEST 2007
 */

package info.opencards.ui.preferences;

import info.opencards.Utils;
import info.opencards.util.UIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * @author Holger Brandl
 */
public class AdvancedSettings extends AbstractSettingsPanel {


    private static final String WRITE_LOGFILE = "advncd.writeLogFile";
    private static final Boolean WRITE_LOGFILE_DEFAULT = false;

    public static final String SHOW_ADVNCD_LTM_STATS = "advncd.showAvncdLtmStats";
    public static final Boolean SHOW_ADVNCD_LTM_STATS_DEFAULT = false;

    public static final String AUTO_DISCOVER_CARDFILES = "advncd.autodiscover";
    public static final Boolean AUTO_DISCOVER_CARDFILES_DEFAULT = false;

    public static final String CUSTOM_CATTREE_LOCATION = "advncd.custcattreelocation";


    public AdvancedSettings() {
        initComponents();
    }


    void resetPanelSettings() {
        Utils.getPrefs().remove(WRITE_LOGFILE);
        Utils.getPrefs().remove(SHOW_ADVNCD_LTM_STATS);
        Utils.getPrefs().remove(AUTO_DISCOVER_CARDFILES);
        Utils.getPrefs().remove(CUSTOM_CATTREE_LOCATION);

        loadDefaults();
    }


    void applySettingsChanges() {
        Utils.getPrefs().putBoolean(WRITE_LOGFILE, writeLogFileCheckbox.isSelected());
        Utils.getPrefs().putBoolean(SHOW_ADVNCD_LTM_STATS, advndLtmStatsCheckbox.isSelected());
        Utils.getPrefs().putBoolean(AUTO_DISCOVER_CARDFILES, autoDiscoverCheckbox.isSelected());
        Utils.getPrefs().put(CUSTOM_CATTREE_LOCATION, catTreeLocationLabel.getText());
    }


    protected void loadDefaults() {
        writeLogFileCheckbox.setSelected(Utils.getPrefs().getBoolean(WRITE_LOGFILE, WRITE_LOGFILE_DEFAULT));
        advndLtmStatsCheckbox.setSelected(Utils.getPrefs().getBoolean(SHOW_ADVNCD_LTM_STATS, SHOW_ADVNCD_LTM_STATS_DEFAULT));
        autoDiscoverCheckbox.setSelected(Utils.getPrefs().getBoolean(AUTO_DISCOVER_CARDFILES, AUTO_DISCOVER_CARDFILES_DEFAULT));
        catTreeLocationLabel.setText(Utils.getPrefs().get(CUSTOM_CATTREE_LOCATION, getDefaultCatTreeLocation().getAbsolutePath()));
    }


    private void catTreeLocationbuttonActionPerformed() {
        JFileChooser catLocChooser = new JFileChooser(catTreeLocationLabel.getText());

        if (catLocChooser.showSaveDialog(UIUtils.getOwnerDialog(this)) == JFileChooser.APPROVE_OPTION) {
            File newLocation = catLocChooser.getSelectedFile();
            catTreeLocationLabel.setText(newLocation.getAbsolutePath());
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel6 = new JPanel();
        autoDiscoverCheckbox = new JCheckBox();
        label1 = new JLabel();
        catTreeLocationLabel = new JTextField();
        catTreeLocationbutton = new JButton();
        panel3 = new JPanel();
        writeLogFileCheckbox = new JCheckBox();
        advndLtmStatsCheckbox = new JCheckBox();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};

        //======== panel6 ========
        {
            panel6.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Categorization", TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            panel6.setLayout(new GridBagLayout());
            ((GridBagLayout) panel6.getLayout()).columnWidths = new int[]{128, 165, 26, 0, 0};
            ((GridBagLayout) panel6.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
            ((GridBagLayout) panel6.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel6.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

            //---- autoDiscoverCheckbox ----
            autoDiscoverCheckbox.setText("Auto-discover card-files in registerd card-file directories");
            panel6.add(autoDiscoverCheckbox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- label1 ----
            label1.setText("Category-tree location : ");
            panel6.add(label1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- catTreeLocationLabel ----
            catTreeLocationLabel.setEnabled(false);
            panel6.add(catTreeLocationLabel, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

            //---- catTreeLocationbutton ----
            catTreeLocationbutton.setText(".");
            catTreeLocationbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    catTreeLocationbuttonActionPerformed();
                }
            });
            panel6.add(catTreeLocationbutton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //======== panel3 ========
        {
            panel3.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Miscellaneous", TitledBorder.LEADING, TitledBorder.TOP),
                    new EmptyBorder(5, 5, 5, 5)));
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

            //---- writeLogFileCheckbox ----
            writeLogFileCheckbox.setText("Enable logging (for support purposes only) ");
            panel3.add(writeLogFileCheckbox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //---- advndLtmStatsCheckbox ----
        advndLtmStatsCheckbox.setText("Show advanced LTM-statistics");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        loadDefaults();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel6;
    private JCheckBox autoDiscoverCheckbox;
    private JLabel label1;
    private JTextField catTreeLocationLabel;
    private JButton catTreeLocationbutton;
    private JPanel panel3;
    private JCheckBox writeLogFileCheckbox;
    private JCheckBox advndLtmStatsCheckbox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public static File getDefaultCatTreeLocation() {
        return new File(Utils.getConfigDir().getPath() + File.separator + "OpenCards.tree");
    }
}
