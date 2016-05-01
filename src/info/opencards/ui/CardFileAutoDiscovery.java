/*
 * Created by JFormDesigner on Mon Feb 18 20:59:46 CET 2008
 */

package info.opencards.ui;

import info.opencards.CardFileBackend;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.CardFileCache;
import info.opencards.core.LearnStatusSerializer;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;
import info.opencards.pptintegration.PPTSerializer;
import info.opencards.ui.actions.HelpAction;
import info.opencards.ui.catui.CategoryModel;
import info.opencards.ui.preferences.AdvancedSettings;
import info.opencards.util.ScaleableIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;


/**
 * A class which is able to discover cardfiles which are not registered to any category yet but are located in one of
 * the users flashcard-directories.
 *
 * @author Holger Brandl
 */
public class CardFileAutoDiscovery extends JDialog {


    private DefaultTableModel dataModel;

    private final Category nullCategory = new Category(Utils.getRB().getString("CardFileAutoDiscovery.selcat"));
    private static LearnStatusSerializer serializer;


    private CardFileAutoDiscovery(JFrame owner, LearnStatusSerializer serializer, List<File> allODPs, Category rootCategory) {
        super(owner);
        initComponents();

        CardFileAutoDiscovery.serializer = serializer;

        helpButton.setAction(new HelpAction());
        disableButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Utils.getPrefs().putBoolean(AdvancedSettings.AUTO_DISCOVER_CARDFILES, false);
                cancelButton.getActionListeners()[0].actionPerformed(null);
            }
        });

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                applyCategorization();
                cancelButton.getActionListeners()[0].actionPerformed(null);
            }
        });

        iconLabel.setIcon(new ScaleableIcon("icons/category_addcardset.png", 50, 50));

        // generate the necessary ui-elements with respect to the current list of discovered files
        generateTable(allODPs, rootCategory);
    }


    private void applyCategorization() {
        Map<CardFile, Category> preloadBuffer = new HashMap<CardFile, Category>();
        for (int i = 0; i < cardFileTable.getRowCount(); i++) {
            String filePath = (String) dataModel.getValueAt(i, 0);
            Category fileCategory = ((CatNameWrapper) dataModel.getValueAt(i, 1)).getCategory();

            if (fileCategory.equals(nullCategory))
                continue;

            preloadBuffer.put(CardFileCache.getCardFile(new File(filePath)), fileCategory);
        }

        // now preload all files before registering them to the tree
        CardFilesPreloader preloader = new CardFilesPreloader(this, serializer);
        preloader.categorySelectionChanged(new ArrayList<CardFile>(preloadBuffer.keySet()), null);

        for (CardFile cardFile : preloadBuffer.keySet()) {
            // register the cardFile to the appropriate category
            preloadBuffer.get(cardFile).registerCardSet(cardFile);
        }
    }


    private void generateTable(List<File> allODPs, Category rootCategory) {
        Object[][] tableData = new Object[allODPs.size()][2];
        for (int i = 0; i < allODPs.size(); i++) {
            File file = allODPs.get(i);
            tableData[i][0] = file.getAbsolutePath();
            tableData[i][1] = new CatNameWrapper(nullCategory);
        }

        dataModel = new DefaultTableModel(
                tableData,
                new String[]{
                        Utils.getRB().getString("CardTableModel.fileName"),
                        Utils.getRB().getString("CardFileAutoDiscovery.selcategory")
                }
        ) {
            final boolean[] columnEditable = new boolean[]{false, true};


            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        cardFileTable.setModel(dataModel);

        TableColumnModel cm = cardFileTable.getColumnModel();

        List<Category> categoryList = CategoryUtils.recursiveCatCollect(rootCategory);
        List<CatNameWrapper> wrappedCats = new ArrayList<CatNameWrapper>();
        wrappedCats.add(new CatNameWrapper(nullCategory));
        for (Category category : categoryList)
            wrappedCats.add(new CatNameWrapper(category));

        cm.getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(new DefaultComboBoxModel(wrappedCats.toArray()))));
    }


    public static void run(JFrame owner, Category rootCategory, LearnStatusSerializer serializer) {
        if (!Utils.getPrefs().getBoolean(AdvancedSettings.AUTO_DISCOVER_CARDFILES, AdvancedSettings.AUTO_DISCOVER_CARDFILES_DEFAULT))
            return;

        Set<CardFile> allCardFiles = CategoryUtils.recursiveCardFileCollect(rootCategory.getRootCategory());
        Set<File> allCatFiles = new HashSet<File>();

        Set<File> cardDirectories = new HashSet<File>();
        // collect all the directories
        for (CardFile cardFile : allCardFiles) {
            cardDirectories.add(cardFile.getFileLocation().getParentFile());
            allCatFiles.add(cardFile.getFileLocation());
        }

        Set<File> allODPs = new HashSet<File>();
        // collect all odp-files in all directories
        for (File cardDirectory : cardDirectories) {
            for (File file : cardDirectory.listFiles()) {
                if (CardFileBackend.hasSupportedExtension(file)) {
                    allODPs.add(file);
                }
            }
        }

        // remove all already categorized files
        allODPs.removeAll(allCatFiles);

        // remove already discoverd and user-skipped files

        // show the autodiscover dialog in a new thread to avoid to block the UI
        if (allODPs.size() > 0)
            new CardFileAutoDiscovery(owner, serializer, new ArrayList<File>(allODPs), rootCategory).setVisible(true);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        hSpacer1 = new JPanel(null);
        iconLabel = new JLabel();
        textArea1 = new JTextArea();
        scrollPane2 = new JScrollPane();
        cardFileTable = new JTable();
        buttonBar = new JPanel();
        helpButton = new JButton();
        disableButton = new JButton();
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("CardFileAutoDiscovery.title"));
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[]{15, 55, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[]{85, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[]{0.0, 1.0, 1.0E-4};
                contentPanel.add(hSpacer1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));
                contentPanel.add(iconLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                //---- textArea1 ----
                textArea1.setText(bundle.getString("CardFileAutoDiscovery.description"));
                textArea1.setTabSize(4);
                textArea1.setWrapStyleWord(true);
                textArea1.setLineWrap(true);
                textArea1.setEnabled(false);
                textArea1.setDisabledTextColor(Color.black);
                textArea1.setMargin(new Insets(5, 5, 5, 5));
                textArea1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP));
                textArea1.setBackground(null);
                contentPanel.add(textArea1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //======== scrollPane2 ========
                {
                    scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                    //---- cardFileTable ----
                    cardFileTable.setModel(new DefaultTableModel(
                            new Object[][]{
                                    {"test", "123"},
                                    {"house", "123"},
                            },
                            new String[]{
                                    null, null
                            }
                    ) {
                        boolean[] columnEditable = new boolean[]{
                                false, true
                        };


                        @Override
                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return columnEditable[columnIndex];
                        }
                    });
                    {
                        TableColumnModel cm = cardFileTable.getColumnModel();
                        cm.getColumn(1).setCellEditor(new DefaultCellEditor(
                                new JComboBox(new DefaultComboBoxModel(new String[]{
                                        "123",
                                        "23",
                                        "23",
                                        "123"
                                }))));
                    }
                    scrollPane2.setViewportView(cardFileTable);
                }
                contentPanel.add(scrollPane2, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 0, 85, 0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0};

                //---- helpButton ----
                helpButton.setText(bundle.getString("General.help"));
                buttonBar.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- disableButton ----
                disableButton.setText(bundle.getString("CardFileAutoDiscovery.disableme"));
                disableButton.setFont(disableButton.getFont().deriveFont(disableButton.getFont().getStyle() & ~Font.BOLD));
                disableButton.setToolTipText(bundle.getString("CardFileAutoDiscovery.disableButton.toolTipText"));
                buttonBar.add(disableButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("General.cancel"));
                cancelButton.setSelectedIcon(null);
                cancelButton.setFont(cancelButton.getFont().deriveFont(cancelButton.getFont().getStyle() & ~Font.BOLD));
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText(bundle.getString("General.ok"));
                okButton.setSelectedIcon(null);
                okButton.setFont(okButton.getFont().deriveFont(okButton.getFont().getStyle() | Font.BOLD));
                buttonBar.add(okButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(575, 365);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel hSpacer1;
    private JLabel iconLabel;
    private JTextArea textArea1;
    private JScrollPane scrollPane2;
    private JTable cardFileTable;
    private JPanel buttonBar;
    private JButton helpButton;
    private JButton disableButton;
    private JButton cancelButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public static void main(String[] args) {
        Category rootCat = CategoryModel.getDefaultCategory();
//        rootCat.getChildCategories().get(0).registerCardSet(new CardFile(new File("P:/presentations/newtest.odp")));

        CardFileAutoDiscovery.run(new JFrame(), rootCat, new PPTSerializer());
    }


    class CatNameWrapper {


        final Category category;


        CatNameWrapper(Category category) {
            this.category = category;
        }


        public String toString() {
//        return getName();
            StringBuilder sb = new StringBuilder();
            sb.append(category.getName());

            Category parent = category.getParent();
            while (parent != null) {
                sb.insert(0, parent.getName() + "->");
                parent = parent.getParent();
            }

            return sb.toString();
        }


        public Category getCategory() {
            return category;
        }
    }
}
