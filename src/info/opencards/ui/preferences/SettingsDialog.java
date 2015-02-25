/*
 * Created by JFormDesigner on Sun Jul 01 00:41:01 CEST 2007
 */

package info.opencards.ui.preferences;

import info.opencards.Utils;
import info.opencards.util.ScaleableIcon;
import info.opencards.util.UIUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class SettingsDialog extends JDialog implements SettingsPanelChangeListener {


    public SettingsDialog(JFrame parentUI) {
        super(parentUI);
        initComponents();
        setTitle("OpenCards " + ResourceBundle.getBundle("info.opencards.translation").getString("General.prefs"));


        UIUtils.closeOnEsc(this, true);

//        setBounds(LayoutRestorer.getInstance().getBounds("global.uilocation", this, getBounds()));
        pack();
//        setLocationRelativeTo(null);
    }


    public void settingsChanged(AbstractSettingsPanel panel) {
        applyButton.setEnabled(true);
    }


    private void applyButtonActionPerformed() {
        AbstractSettingsPanel settingsPanel = (AbstractSettingsPanel) setPanelContainer.getComponent(0);

        if (settingsPanel.isSettingsChanged()) {
            settingsPanel.applySettingsChanges();
        }

        Utils.flushPreferences();
        applyButton.setEnabled(false);
    }


    private void okButtonActionPerformed() {
        for (Component component : setPanelContainer.getComponents()) {
            ((AbstractSettingsPanel) component).applySettingsChanges();
        }

        dispose();
    }


    private void cancelButtonActionPerformed() {
        dispose();
    }


    private void resetButtonActionPerformed() {
        for (Component component : setPanelContainer.getComponents()) {
            ((AbstractSettingsPanel) component).resetPanelSettings();
        }

        Utils.resetAllSettings();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        splitPane1 = new JSplitPane();
        listScrollPane = new JScrollPane();
        tabList = new JList();
        panel1 = new JPanel();
        setPanelContainer = new JPanel();
        buttonBar = new JPanel();
        resetButton = new JButton();
        cancelButton = new JButton();
        okButton = new JButton();
        applyButton = new JButton();

        //======== this ========
        setTitle("OpenCards Settings");
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setMinimumSize(new Dimension(400, 200));
            dialogPane.setPreferredSize(null);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setPreferredSize(null);
                contentPanel.setLayout(new BorderLayout());

                //======== splitPane1 ========
                {
                    splitPane1.setDividerSize(0);
                    splitPane1.setBorder(null);
                    splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    splitPane1.setMinimumSize(null);
                    splitPane1.setPreferredSize(null);

                    //======== listScrollPane ========
                    {
                        listScrollPane.setBorder(null);
                        listScrollPane.setMinimumSize(new Dimension(80, 80));
                        listScrollPane.setOpaque(false);
                        listScrollPane.setPreferredSize(null);

                        //---- tabList ----
                        tabList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        tabList.setPreferredSize(null);
                        tabList.setOpaque(false);
                        tabList.setMaximumSize(null);
                        tabList.setMinimumSize(null);
                        tabList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                        tabList.setVisibleRowCount(1);
                        tabList.setBorder(null);
                        tabList.setFont(tabList.getFont().deriveFont(tabList.getFont().getStyle() | Font.BOLD));
                        listScrollPane.setViewportView(tabList);
                    }
                    splitPane1.setTopComponent(listScrollPane);

                    //======== panel1 ========
                    {
                        panel1.setBorder(new BevelBorder(BevelBorder.LOWERED));
                        panel1.setPreferredSize(null);
                        panel1.setMaximumSize(null);
                        panel1.setMinimumSize(null);
                        panel1.setLayout(new BorderLayout());

                        //======== setPanelContainer ========
                        {
                            setPanelContainer.setLayout(new CardLayout(4, 4));
                        }
                        panel1.add(setPanelContainer, BorderLayout.CENTER);
                    }
                    splitPane1.setBottomComponent(panel1);
                }
                contentPanel.add(splitPane1, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(5, 5, 5, 5));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 0, 75, 75, 70};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0};

                //---- resetButton ----
                resetButton.setText(bundle.getString("SettingsDialog.resetAll"));
                resetButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        resetButtonActionPerformed();
                    }
                });
                buttonBar.add(resetButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("General.cancel"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed();
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText("OK");
                okButton.setFont(okButton.getFont().deriveFont(okButton.getFont().getStyle() | Font.BOLD));
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);

        //---- applyButton ----
        applyButton.setText(" Apply ");
        applyButton.setEnabled(false);
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyButtonActionPerformed();
            }
        });
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        DefaultListModel menuListmodel = new DefaultListModel();

        menuListmodel.addElement(bundle.getString("SettingsDialog.GeneralItem"));
        menuListmodel.addElement(bundle.getString("General.ltm"));
        menuListmodel.addElement(bundle.getString("General.lastmin"));
//        menuListmodel.addElement(bundle.getString("SettingsDialog.EffectsItem"));
        menuListmodel.addElement(bundle.getString("SettingsDialog.AvancedItem"));


        setPanelContainer.add("General", new GeneralSettings());
        setPanelContainer.add("Learning", new GlobLearnSettings());
        setPanelContainer.add("LastMinute", new LeitnerSettings());
//        setPanelContainer.add("Effects", new EffectSettings());
        setPanelContainer.add("Advanced", new AdvancedSettings());


        tabList.setModel(menuListmodel);
        tabList.setCellRenderer(new MyListCellRenderer());
        tabList.setSelectedIndex(0);

        final CardLayout cardLayout = (CardLayout) setPanelContainer.getLayout();

        setPanelContainer.setLayout(cardLayout);
        tabList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                int index = tabList.getSelectedIndex();
                switch (index) {
                    case 0:
                        cardLayout.show(setPanelContainer, "General");
                        break;
                    case 1:
                        cardLayout.show(setPanelContainer, "Learning");
                        break;
                    case 2:
                        cardLayout.show(setPanelContainer, "LastMinute");
                        break;
//                    case 2:
//                        cardLayout.show(setPanelContainer, "Effects");
//                        break;
                    case 3:
                        cardLayout.show(setPanelContainer, "Advanced");
                }
            }
        });
    }


    public void setActiveSettingsPanel(int tabIndex) {
        tabList.setSelectedIndex(tabIndex);
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JSplitPane splitPane1;
    private JScrollPane listScrollPane;
    private JList tabList;
    private JPanel panel1;
    private JPanel setPanelContainer;
    private JPanel buttonBar;
    private JButton resetButton;
    private JButton cancelButton;
    private JButton okButton;
    private JButton applyButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    private static class MyListCellRenderer extends DefaultListCellRenderer {


        private final ScaleableIcon generalIcon;
        private final ScaleableIcon learnIcon;
        private final ScaleableIcon lastMinIcon;
        private final ScaleableIcon advancedIcon;
        private ScaleableIcon effectsIcon;
        private final ScaleableIcon oointIcon;


        public MyListCellRenderer() {
            int iconSize = 35;

            generalIcon = new ScaleableIcon(new ImageIcon(Utils.loadResource("icons/globset_general.png")), iconSize, iconSize);
            learnIcon = new ScaleableIcon(new ImageIcon(Utils.loadResource("icons/globset_learning.png")), iconSize, iconSize);
            lastMinIcon = new ScaleableIcon(new ImageIcon(Utils.loadResource("icons/5vorUhr.png")), iconSize, iconSize);
            oointIcon = new ScaleableIcon(new ImageIcon(Utils.loadResource("icons/globset_ooint.png")), iconSize, iconSize);
            advancedIcon = new ScaleableIcon(new ImageIcon(Utils.loadResource("icons/globset_advanced.png")), iconSize, iconSize);

//            effectsIcon = new ImageIcon(Utils.loadResource("icons/globset_effects.png"));
        }


        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            switch (index) {
                case 0:
                    setIcon(generalIcon);
                    break;
                case 1:
                    setIcon(learnIcon);
                    break;
                case 2:
                    setIcon(lastMinIcon);
                    break;
                case 3:
                    setIcon(oointIcon);
                    break;
//                case 3:
//                    setIcon(effectsIcon);
//                    break;
                case 4:
                    setIcon(advancedIcon);
            }

            setHorizontalTextPosition(CENTER);
            setVerticalTextPosition(BOTTOM);

            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);

            setBorder(new EmptyBorder(12, 4, 12, 4));

            return this;
        }
    }


    public static void main(String[] args) {
        Locale ooLocale = new Locale("el", "GR");
        Locale.setDefault(ooLocale != null ? ooLocale : java.util.Locale.ENGLISH);
        new SettingsDialog(new JFrame()).setVisible(true);
    }
}
