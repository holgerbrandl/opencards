/*
 * Created by JFormDesigner on Sat Sep 15 21:19:13 CEST 2007
 */

package info.opencards.ui;

import info.opencards.Utils;
import info.opencards.core.*;
import info.opencards.learnstrats.ltm.LTMCollection;
import info.opencards.learnstrats.ltm.LTMItem;
import info.opencards.pptintegration.PPTSerializer;
import info.opencards.ui.actions.HelpAction;
import info.opencards.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class CardFilePropsDialog extends JDialog {


    public static final int REVERSE_POLICY_DEFAULT = 0;

    private java.util.List<CardFile> cardFiles;
    private List<FlashCard> applyCards;


    private boolean isFileProps;


    private CardFilePropsDialog(Dialog owner) throws HeadlessException {
        super(owner);
        setModal(true);
        initComponents();

        //set possible reversing policies
        revPolicyComboBox.setModel(new DefaultComboBoxModel(new String[]{
                Utils.getRB().getString("CardFilePropsDialog.revPol.normal"),
                Utils.getRB().getString("CardFilePropsDialog.revPol.reverse"),
                Utils.getRB().getString("CardFilePropsDialog.revPol.randReverse")}));

        learnTypePropsContainer.add(Utils.getRB().getString("General.ltm"), ltmPropsPanel);
        learnTypePropsContainer.add(Utils.getRB().getString("General.lastmin"), lastMinPropsPanel);


        UIUtils.closeOnEsc(this, true);
        helpButton.setAction(new HelpAction());
        cancelButton.requestFocusInWindow();
    }


    public CardFilePropsDialog(Dialog owner, CardFile cardFile, List<FlashCard> applyCards) {
        this(owner);


        if (cardFile == null || applyCards == null || applyCards.isEmpty())
            return;

        this.applyCards = applyCards;
        this.cardFiles = Arrays.asList(cardFile);
        isFileProps = false;


        StringBuilder slideNames = new StringBuilder();
        for (FlashCard applyCard : applyCards) {
            slideNames.append(", " + applyCard.getCardTitle());
        }
        slideNames.replace(0, 2, "");

        setTitle(Utils.getRB().getString("CardFilePropsDialog.settingsof") + " '" + slideNames.toString() + "'");


        FlashCardCollection cardCollection = cardFile.getFlashCards();

        // extract the reversing policy of the item
        revPolicyComboBox.setSelectedIndex(ReversePolicy.toInt(applyCards.get(0).getRevPolicy()));

        //extract ltm-settings (retention, etc.)
        LTMItem ltmItem = (LTMItem) cardCollection.getLTMItems().findItem(applyCards.get(0));
        int retention = (Integer) ltmItem.getProperty(LTMItem.DESIRED_RETENTION, LTMItem.DESIRED_RETENTION_DEFAULT);

        retentionSpinner.setValue(retention);
    }


    public CardFilePropsDialog(Dialog owner, java.util.List<CardFile> cardFiles, boolean isLTMProps) {
        this(owner);

        isFileProps = true;

        assert !cardFiles.isEmpty() : "what a mess: settings just for fun without any cardfile.";
        this.cardFiles = cardFiles;

        setIsLTMProps(isLTMProps);

        // set all settings to the file-settings
        CardFile firstFile = cardFiles.get(0);

        if (cardFiles.size() == 1) {
            File fileLocation = firstFile.getFileLocation();
            String fileName = fileLocation != null ? fileLocation.getAbsolutePath() : Utils.getRB().getString("CardFilePropsDialgo.unsavedcards");
            setTitle(Utils.getRB().getString("CardFilePropsDialog.settingsof") + " '" + fileName + "'");
        } else
            setTitle(Utils.getRB().getString("CardFilePropsDialog.multifiles"));

        // extract common file settings
        FlashCardCollection flashCards = firstFile.getFlashCards();
        int slidePol = ReversePolicy.toInt((ReversePolicy) flashCards.getProperty(FlashCardCollection.REVERSE_POLICY, ReversePolicy.NORMAL));
        int preSelectIndex = revPolicyComboBox.getModel().getSize() <= slidePol ? 0 : slidePol;
        revPolicyComboBox.setSelectedIndex(preSelectIndex);

        //extract ltm-settings
        LTMCollection ltmCollection = flashCards.getLTMItems();
        firstFile.getFlashCards().getLTMItems().getProperty(LTMItem.DESIRED_RETENTION, "" + LTMItem.DESIRED_RETENTION_DEFAULT);
        int retention = (Integer) ltmCollection.getProperty(LTMItem.DESIRED_RETENTION, LTMItem.DESIRED_RETENTION_DEFAULT);

        retentionSpinner.setValue(retention);
    }


    public void setIsLTMProps(boolean isLTMProps) {
        learnTypePropsContainer.setSelectedIndex(isLTMProps ? 0 : 1);
    }


    private void okButtonActionPerformed() {
        //extract the selected reverse policy
        int policyIndex = revPolicyComboBox.getSelectedIndex();
        ReversePolicy selectPolicy = ReversePolicy.toPolicy(policyIndex);

        if (isFileProps) {

            for (CardFile cardFile : cardFiles) {
                // set the global value
                FlashCardCollection cardCollection = cardFile.getFlashCards();

                cardCollection.getProps().put(FlashCardCollection.REVERSE_POLICY, selectPolicy);
                cardFile.getFlashCards().getLTMItems().setProperty(LTMItem.DESIRED_RETENTION, retentionSpinner.getValue());

                // remove all card specific settings
                for (FlashCard card : cardCollection) {
                    card.setRevPolicy(selectPolicy);
                }

                for (Item item : cardCollection.getLTMItems()) {
                    item.setProperty(LTMItem.DESIRED_RETENTION, retentionSpinner.getValue());
                }

                // no cardfile-specific STM-settings yet :-)

                // flush the properties
                // note: this might be buggy, because we might loose the changes on close (c.f. comment in ImpressSerializer)
                cardFile.flush();
            }


        } else {
            // the config-dialog was shown for some slides of a single presentation

            CardFile cardFile = cardFiles.get(0);

            for (FlashCard applyCard : applyCards) {
                applyCard.setRevPolicy(selectPolicy);

                LTMItem ltmItem = (LTMItem) cardFile.getFlashCards().getLTMItems().findItem(applyCard);
                ltmItem.getItemProps().put(LTMItem.DESIRED_RETENTION, retentionSpinner.getValue());
            }

            cardFile.flush();
        }

        dispose();
    }


    private void cancelButtonActionPerformed() {
        dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JPanel panel1 = new JPanel();
        JLabel revPolicyLabel = new JLabel();
        revPolicyComboBox = new JComboBox();
        learnTypePropsContainer = new JTabbedPane();
        JPanel buttonBar = new JPanel();
        helpButton = new JButton();
        cancelButton = new JButton();
        JButton okButton = new JButton();
        lastMinPropsPanel = new JPanel();
        noLeitnerYetLabel = new JLabel();
        ltmPropsPanel = new JPanel();
        desRetenLabel = new JLabel();
        retentionSpinner = new JSpinner();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[]{0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[]{0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[]{0.0, 1.0, 1.0E-4};

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder(null, bundle.getString("CardFilePropsDialog.panel1.border"), TitledBorder.LEADING, TitledBorder.TOP));
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths = new int[]{0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[]{0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

                    //---- revPolicyLabel ----
                    revPolicyLabel.setText(bundle.getString("CardFilePropsDialog.revPolicyLabel.text"));
                    panel1.add(revPolicyLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- revPolicyComboBox ----
                    revPolicyComboBox.setModel(new DefaultComboBoxModel(new String[]{
                            "Normal",
                            "Reverse",
                            "Random reverse"
                    }));
                    panel1.add(revPolicyComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));
                }
                contentPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                contentPanel.add(learnTypePropsContainer, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};

                //---- helpButton ----
                helpButton.setText(bundle.getString("General.help"));
                buttonBar.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("General.cancel"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed();
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText(bundle.getString("General.close"));
                okButton.setFont(new Font("Tahoma", Font.BOLD, 11));
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed();
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(455, 280);
        setLocationRelativeTo(null);

        //======== lastMinPropsPanel ========
        {
            lastMinPropsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            lastMinPropsPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) lastMinPropsPanel.getLayout()).columnWidths = new int[]{0, 0, 0};
            ((GridBagLayout) lastMinPropsPanel.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) lastMinPropsPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
            ((GridBagLayout) lastMinPropsPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

            //---- noLeitnerYetLabel ----
            noLeitnerYetLabel.setText("No properties yet");
            lastMinPropsPanel.add(noLeitnerYetLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));
        }

        //======== ltmPropsPanel ========
        {
            ltmPropsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            ltmPropsPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) ltmPropsPanel.getLayout()).columnWidths = new int[]{0, 0, 0};
            ((GridBagLayout) ltmPropsPanel.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) ltmPropsPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
            ((GridBagLayout) ltmPropsPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

            //---- desRetenLabel ----
            desRetenLabel.setText(bundle.getString("CardFilePropsDialog.desRetenLabel.text"));
            ltmPropsPanel.add(desRetenLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- retentionSpinner ----
            retentionSpinner.setModel(new SpinnerNumberModel(80, 60, 100, 1));
            ltmPropsPanel.add(retentionSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JComboBox revPolicyComboBox;
    private JTabbedPane learnTypePropsContainer;
    private JButton helpButton;
    private JButton cancelButton;
    private JPanel lastMinPropsPanel;
    private JLabel noLeitnerYetLabel;
    private JPanel ltmPropsPanel;
    private JLabel desRetenLabel;
    private JSpinner retentionSpinner;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public static void main(String[] args) {
        CardFile cardFile = new CardFile(new File("testdata/testpres.odp"));

        cardFile.setSerializer(new PPTSerializer());

//        new CardFilePropsDialog(new JDialog(), Arrays.asList(cardFile), cardFile.getFlashCards(), true).setVisible(true);
        new CardFilePropsDialog(new JDialog(), cardFile, cardFile.getFlashCards()).setVisible(true);
    }
}
