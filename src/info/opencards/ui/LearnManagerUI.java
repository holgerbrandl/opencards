/*
 * Created by JFormDesigner on Wed Aug 29 22:40:03 CEST 2007
 */

package info.opencards.ui;

import info.opencards.CardFileBackend;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;
import info.opencards.learnstrats.ltm.ScheduleUtils;
import info.opencards.ui.actions.HelpAction;
import info.opencards.ui.actions.LastMinLearnAction;
import info.opencards.ui.actions.StartLearningAction;
import info.opencards.ui.catui.CategoryPanel;
import info.opencards.ui.catui.CategoryTree;
import info.opencards.ui.ltmstats.LongTermMemStatsPanel;
import info.opencards.ui.preferences.SettingsDialog;
import info.opencards.ui.table.CardSetTable;
import info.opencards.ui.table.CardTableModel;
import info.opencards.ui.table.FilePopUp;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class LearnManagerUI extends JPanel implements CardFileSelectionListener {


    private final CardFileBackend backend;
    private CurFileSelectionManager selectionManager;

    private boolean isShiftPressed;
    private boolean isCtrlPressed;


    public LearnManagerUI(final JFrame owner, CardFileBackend backend) {
//        super(owner);
        this.backend = backend;

        initComponents();

        // serialize and restore split spane location changes
        restoreContentLayout();

        // setup cardfile-contents
        if (backend == null)
            return;

        selectionManager = new CurFileSelectionManager(cardfileTable);
        selectionManager.addCardFileSelectionListener(ltmStatsPanel);

        Category rootCat = CategoryUtils.deserializeCategoryModel(owner);

//        categoryPanel.getCatTree().setRootCategory(null);
        final CategoryTree catTree = categoryPanel.getCatTree();
        catTree.setRootCategory(rootCat);

        cardfileTable.addMouseListener(new FilePopUp(cardfileTable));
        cardfileTable.getSelectionModel().addListSelectionListener(selectionManager);

        // this listener needs to process selection events frist in order to set the Serializer
        CardFilesPreloader preLoader = new CardFilesPreloader(this, backend.getSerializer());
        catTree.addCardFilesSelectionListener(preLoader);

        catTree.addCardFilesSelectionListener((CardTableModel) cardfileTable.getModel());
        catTree.getRootCategory().addCategoryChangeListener(((CardTableModel) cardfileTable.getModel()).getCatListener());

        catTree.addCardFilesSelectionListener(selectionManager);

        helpButton.setAction(new HelpAction("help/category-view"));

        selectionManager.addCardFileSelectionListener(this);


        final StartLearningAction startLearningAction = new StartLearningAction(this);
        selectionManager.addCardFileSelectionListener(startLearningAction);
        startLearnButton.setAction(startLearningAction);

        LastMinLearnAction lastMinLearnAction = new LastMinLearnAction();
        selectionManager.addCardFileSelectionListener(lastMinLearnAction);
        startLastMinButton.setAction(lastMinLearnAction);

        numNewExceedLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        numNewExceedLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    // open the preferences for the long-term learning
                    SettingsDialog dialog = new SettingsDialog(owner);
                    dialog.setActiveSettingsPanel(1);
                    dialog.setModal(true);
                    dialog.setVisible(true);

                    refreshFileViews();
                }
            }
        });

        // this will update the all selection-listeners
        catTree.refire(catTree.getPathForRow(0));
//        cardsSplitPanel.setDividerLocation(0.6);


        // register for shift-pressings
        setupModifierKeyProcessing(startLearningAction);
    }


    /**
     * serialize and restore split spane location changes.
     */
    private void restoreContentLayout() {
        final String contentPaneDividerID = "contentPaneSplitLoc";

        contentSplitPanel.addPropertyChangeListener(new PropertyChangeListener() {


            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
                    Utils.getPrefs().putInt(contentPaneDividerID, contentSplitPanel.getDividerLocation());
                }
            }
        });

        final String cardfilePaneDividerID = "cardfilePaneSplitLoc";
        cardsSplitPanel.addPropertyChangeListener(new PropertyChangeListener() {


            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
                    Utils.getPrefs().putInt(cardfilePaneDividerID, cardsSplitPanel.getDividerLocation());
                }
            }
        });

        // restore divider locations
        contentSplitPanel.setDividerLocation(Utils.getPrefs().getInt(contentPaneDividerID, 180));
        cardsSplitPanel.setDividerLocation(Utils.getPrefs().getInt(cardfilePaneDividerID, 399));
    }


    private void setupModifierKeyProcessing(final StartLearningAction startLearningAction) {
        KeyStroke altDown = KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK, false);
//        JRootPane keyListenerPane = getRootPane();
        JPanel keyListenerPane = this;


        keyListenerPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altDown, "ALTDOWN");
        keyListenerPane.getActionMap().put("ALTDOWN", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                isShiftPressed = true;
                isCtrlPressed = false;
                startLearningAction.updateModKeyState();
            }
        });

        KeyStroke altUp = KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true);
        keyListenerPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altUp, "ALTUP");

        keyListenerPane.getActionMap().put("ALTUP", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                isShiftPressed = false;
                isCtrlPressed = false;
                startLearningAction.updateModKeyState();
            }
        });

        KeyStroke ctrlAltDown = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, KeyEvent.CTRL_DOWN_MASK, false);
        keyListenerPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlAltDown, "CTRLDOWN");
        keyListenerPane.getActionMap().put("CTRLDOWN", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                isCtrlPressed = true;
                isShiftPressed = false;
                startLearningAction.updateModKeyState();
            }
        });

        KeyStroke ctrlAltUp = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true);
        keyListenerPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlAltUp, "CTRLUP");

        keyListenerPane.getActionMap().put("CTRLUP", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                isCtrlPressed = false;
                isShiftPressed = false;
                startLearningAction.updateModKeyState();
            }
        });
    }


    public CardFileBackend getBackend() {
        return backend;
    }


    public CardSetTable getCardfileTable() {
        return cardfileTable;
    }


    public void refreshFileViews() {
        categoryPanel.getCatTree().informCardFileSelectionListeners();
    }


    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);

//        ((CardTableModel) cardfileTable.getModel()).refreshTableData();
//        ltmStatsPanel.categorySelectionChanged(null, new HashSet<Category>());
        selectionManager.refireLastSelection();
    }


    public void cardFileSelectionChanged(List<CardFile> curSelCardFiles) {
        Map<CardFile, List<Item>> fileListMap = ((CardTableModel) cardfileTable.getModel()).getDummyLtmManager().getNewButNotScheduledItems();

        int numNotSchedItems = 0;
        for (CardFile cardFile : fileListMap.keySet()) {
            numNotSchedItems += fileListMap.get(cardFile).size();
        }

        if (numNotSchedItems > 0) {
            int numMaxNewPerDay = ScheduleUtils.getMaxNewCardsPerDay();

            String msg = Utils.getRB().getString("LearnManagerUI.numNewExceeded");
            msg = msg.replace("MAX_ITEMS_PER_DAY", "<font color=blue>" + numMaxNewPerDay + "</font>");
            msg = msg.replace("NUM_NOT_SCHED_ITEMS", numNotSchedItems + "");

            numNewExceedLabel.setText("<html>" + msg + "</html>");
            panel1.add(numNewExceedLabel, BorderLayout.SOUTH);
            panel1.validate();
        } else {
            numNewExceedLabel.setText("");
            panel1.remove(numNewExceedLabel);
            panel1.validate();
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        JPanel dialogPane = new JPanel();
        JPanel buttonBar = new JPanel();
        helpButton = new JButton();
        startLastMinButton = new JButton();
        startLearnButton = new JButton();
        contentSplitPanel = new JSplitPane();
        categoryPanel = new CategoryPanel();
        JPanel contentPanel = new JPanel();
        cardsSplitPanel = new JSplitPane();
        JScrollPane scrollPane = new JScrollPane();
        cardfileTable = new CardSetTable();
        panel1 = new JPanel();
        ltmStatsPanel = new LongTermMemStatsPanel();
        numNewExceedLabel = new JLabel();

        //======== this ========
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{84, 0, 0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};

                //---- helpButton ----
                helpButton.setText(bundle.getString("General.help"));
                helpButton.setName(bundle.getString("General.help"));
                buttonBar.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- startLastMinButton ----
                startLastMinButton.setText(bundle.getString("General.lastmin"));
                buttonBar.add(startLastMinButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- startLearnButton ----
                startLearnButton.setText(bundle.getString("StartLearningAction.StartLearning"));
                startLearnButton.setFont(startLearnButton.getFont().deriveFont(startLearnButton.getFont().getStyle() | Font.BOLD));
                buttonBar.add(startLearnButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== contentSplitPanel ========
            {
                contentSplitPanel.setDividerSize(4);
                contentSplitPanel.setDividerLocation(180);
                contentSplitPanel.setBorder(null);
                contentSplitPanel.setResizeWeight(0.2);

                //---- categoryPanel ----
                categoryPanel.setPreferredSize(new Dimension(180, 324));
                categoryPanel.setBorder(new CompoundBorder(
                        new TitledBorder(null, bundle.getString("LearnManagerUI.categoryPanel.border"), TitledBorder.LEADING, TitledBorder.TOP),
                        new EmptyBorder(1, 2, 2, 2)));
                categoryPanel.setBackground(null);
                contentSplitPanel.setLeftComponent(categoryPanel);

                //======== contentPanel ========
                {
                    contentPanel.setBorder(null);
                    contentPanel.setLayout(new BorderLayout());

                    //======== cardsSplitPanel ========
                    {
                        cardsSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        cardsSplitPanel.setDividerSize(4);
                        cardsSplitPanel.setBorder(new EmptyBorder(0, 2, 0, 0));
                        cardsSplitPanel.setResizeWeight(0.8);

                        //======== scrollPane ========
                        {
                            scrollPane.setPreferredSize(null);
                            scrollPane.setMaximumSize(null);
                            scrollPane.setMinimumSize(null);
                            scrollPane.setViewportView(cardfileTable);
                        }
                        cardsSplitPanel.setTopComponent(scrollPane);

                        //======== panel1 ========
                        {
                            panel1.setLayout(new BorderLayout());

                            //======== ltmStatsPanel ========
                            {
                                ltmStatsPanel.setPreferredSize(new Dimension(140, 140));
                                ltmStatsPanel.setMinimumSize(new Dimension(100, 100));
                            }
                            panel1.add(ltmStatsPanel, BorderLayout.CENTER);
                        }
                        cardsSplitPanel.setBottomComponent(panel1);
                    }
                    contentPanel.add(cardsSplitPanel, BorderLayout.CENTER);
                }
                contentSplitPanel.setRightComponent(contentPanel);
            }
            dialogPane.add(contentSplitPanel, BorderLayout.CENTER);
        }
        add(dialogPane, BorderLayout.CENTER);

        //---- numNewExceedLabel ----
        numNewExceedLabel.setBorder(new TitledBorder(""));
        numNewExceedLabel.setFont(numNewExceedLabel.getFont().deriveFont(numNewExceedLabel.getFont().getStyle() & ~Font.BOLD));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JButton helpButton;
    private JButton startLastMinButton;
    private JButton startLearnButton;
    private JSplitPane contentSplitPanel;
    public CategoryPanel categoryPanel;
    private JSplitPane cardsSplitPanel;
    private CardSetTable cardfileTable;
    private JPanel panel1;
    private LongTermMemStatsPanel ltmStatsPanel;
    private JLabel numNewExceedLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public static void main(String[] args) {
        JFrame owner = new JFrame();

        owner.setBounds(100, 100, 500, 700);
        owner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardFileBackend backend = CardFileBackend.getBackend();
        owner.add(new LearnManagerUI(owner, backend));

        owner.setVisible(true);
    }


    public boolean isShiftPressed() {
        return isShiftPressed;
    }


    public boolean isCtrlPressed() {
        return isCtrlPressed;
    }
}