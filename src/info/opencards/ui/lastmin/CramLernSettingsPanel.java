/*
 * Created by JFormDesigner on Sun Jul 01 00:35:36 CEST 2007
 */

package info.opencards.ui.lastmin;

import info.opencards.OpenCards;
import info.opencards.core.CardFile;
import info.opencards.core.LearnProcessManager;
import info.opencards.learnstrats.leitner.LeitnerLearnMethodFactory;
import info.opencards.learnstrats.leitner.LeitnerProcessManager;
import info.opencards.learnstrats.leitner.LeitnerSystem;
import info.opencards.learnstrats.leitner.LeitnerUtils;
import info.opencards.ui.actions.CardFileSyncAction;
import info.opencards.ui.actions.HelpAction;
import info.opencards.ui.actions.ResetLeiterSystemAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class CramLernSettingsPanel extends JPanel {


    private List<CardFile> cardFiles;


    public CramLernSettingsPanel() {
        initComponents();

        defStratPanel = new LeitnerStrategyPanel();
        stratPanelPanel.add(defStratPanel);
        stratPanelPanel.validate();

        helpButton.setAction(new HelpAction("help/last-minute-learning/"));


//        CardFilePropsAction propsAction = new CardFilePropsAction(defStratPanel, false, Utils.getRB().getString("CramLernSettingsPanel.fileProperties") + "...");
//        propsAction.setCardFile(Arrays.asList(cardFile));
//        configureFileButton.setAction(propsAction);
    }


    public void configure(List<CardFile> cardFiles) {
        this.cardFiles = cardFiles;

        if (cardFiles.isEmpty())
            return;

        // todo  make sure that it does not steal the selection of the category view
        final CardFileSyncAction cardFileSyncAction = new CardFileSyncAction(null);
        cardFileSyncAction.actionPerformed(null);

        resetBoxesButton.setAction(new ResetLeiterSystemAction(cardFiles, this));

        List<LeitnerSystem> leitners = new ArrayList<LeitnerSystem>();
        for (CardFile cardFile : cardFiles) {
            leitners.add(cardFile.getFlashCards().getLeitnerItems());
        }

        LeitnerSystem leitnerSystem = LeitnerUtils.merge(leitners);
        leitnerStatePanel.setLeitnerSystem(leitnerSystem);
    }


    private void startLearningAction() {

        LeitnerLearnMethodFactory factory = defStratPanel.getLearnMethodFactory();

        LeitnerLearnDialog itemEvaluater = new LeitnerLearnDialog();
        LearnProcessManager processManager = new LeitnerProcessManager(cardFiles, itemEvaluater, factory);

        OpenCards.getInstance().getLearnPanel().setControls(itemEvaluater);
        OpenCards.showLearnView();

        processManager.startProcessing();
    }


    private void cancelLearningAction() {
        OpenCards.showCategoryView();
    }


    public void setVisible(boolean b) {
        startLearnButton.requestFocusInWindow();
        super.setVisible(b);
    }


    private void setDefaultButtonActionPerformed() {
        defStratPanel.saveDefaults();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        panel4 = new JPanel();
        leitnerStatePanel = new LeitnerStatePanel();
        panel5 = new JPanel();
        resetBoxesButton = new JButton();
        stratPanelPanel = new JPanel();
        defStratPanel = new LeitnerStrategyPanel();
        JPanel panel3 = new JPanel();
        helpButton = new JButton();
        setDefaultButton = new JButton();
        hSpacer1 = new JPanel(null);
        cancelButton = new JButton();
        startLearnButton = new JButton();
        configureFileButton = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel4 ========
        {
            panel4.setBorder(new TitledBorder(null, bundle.getString("LearnSettingsPanel.panel4.border"), TitledBorder.LEADING, TitledBorder.TOP));
            panel4.setLayout(new BorderLayout());

            //---- leitnerStatePanel ----
            leitnerStatePanel.setMinimumSize(new Dimension(100, 100));
            leitnerStatePanel.setPreferredSize(new Dimension(100, 100));
            panel4.add(leitnerStatePanel, BorderLayout.CENTER);

            //======== panel5 ========
            {
                panel5.setLayout(new GridBagLayout());
                ((GridBagLayout) panel5.getLayout()).columnWidths = new int[]{0, 0};
                ((GridBagLayout) panel5.getLayout()).rowHeights = new int[]{0, 0};
                ((GridBagLayout) panel5.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                ((GridBagLayout) panel5.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                //---- resetBoxesButton ----
                resetBoxesButton.setText("Reset Cards");
                panel5.add(resetBoxesButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            panel4.add(panel5, BorderLayout.SOUTH);
        }
        add(panel4, BorderLayout.NORTH);

        //======== stratPanelPanel ========
        {
            stratPanelPanel.setBorder(new TitledBorder(null, bundle.getString("LearnSettingsPanel.stratPanelPanel.border"), TitledBorder.LEADING, TitledBorder.TOP));
            stratPanelPanel.setLayout(new BorderLayout());
            stratPanelPanel.add(defStratPanel, BorderLayout.CENTER);
        }
        add(stratPanelPanel, BorderLayout.CENTER);

        //======== panel3 ========
        {
            panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0, 0, 90, 100, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

            //---- helpButton ----
            helpButton.setText(bundle.getString("General.help"));
            helpButton.setName(null);
            panel3.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 10), 0, 0));

            //---- setDefaultButton ----
            setDefaultButton.setText(bundle.getString("CramLernSettingsPanel.setDefaultButton.text"));
            setDefaultButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setDefaultButtonActionPerformed();
                }
            });
            panel3.add(setDefaultButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 10), 0, 0));
            panel3.add(hSpacer1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 10), 0, 0));

            //---- cancelButton ----
            cancelButton.setText(bundle.getString("General.cancel"));
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelLearningAction();
                }
            });
            panel3.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 10), 0, 0));

            //---- startLearnButton ----
            startLearnButton.setText(bundle.getString("StartLearningAction.StartLearning"));
            startLearnButton.setFont(startLearnButton.getFont().deriveFont(startLearnButton.getFont().getStyle() | Font.BOLD));
            startLearnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startLearningAction();
                }
            });
            panel3.add(startLearnButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel3, BorderLayout.SOUTH);

        //---- configureFileButton ----
        configureFileButton.setText(bundle.getString("CardFileTable.cxtMenu.configureCards"));
        configureFileButton.setEnabled(false);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel4;
    private LeitnerStatePanel leitnerStatePanel;
    private JPanel panel5;
    private JButton resetBoxesButton;
    private JPanel stratPanelPanel;
    private LeitnerStrategyPanel defStratPanel;
    private JButton helpButton;
    private JButton setDefaultButton;
    private JPanel hSpacer1;
    private JButton cancelButton;
    private JButton startLearnButton;
    private JButton configureFileButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}