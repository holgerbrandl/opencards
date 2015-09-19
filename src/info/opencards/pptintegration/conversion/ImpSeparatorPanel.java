package info.opencards.pptintegration.conversion;

import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.ui.catui.CategoryTreeSelectionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author Holger Brandl
 */
class ImpSeparatorPanel extends JPanel {


    private static final String TAB_SEPARATOR = "\t";
    public static final String SPACE_SEPARATOR = "\" \"";
    private static final String SEM_SEPARATOR = ";";

    private File curFile;


    public ImpSeparatorPanel(JFileChooser importChooser) {
        initComponents();

        previewTable.setModel(new PreviewTableModel());

        importChooser.setAccessory(this);

        importChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    JFileChooser chooser = (JFileChooser) evt.getSource();
                    File newFile = (File) evt.getNewValue();

                    // The selected file should always be the same as newFile
                    curFile = newFile;

                    // update preview view
                    updatePreview(newFile);

                } else if (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    // this should never happen because we can not import several files at once
                }
            }
        });
    }


    public String getCurSeparator() {
        String curSep = null;

        if (tabRadioButton.isSelected())
            curSep = TAB_SEPARATOR;
        else if (spaceRadioButton.isSelected())
            curSep = SPACE_SEPARATOR;
        else if (semRadioButton.isSelected())
            curSep = SEM_SEPARATOR;

        assert curSep != null;

        return curSep;
    }


    private void updatePreview(File curFile) {
        PreviewTableModel tableModel = (PreviewTableModel) previewTable.getModel();

        if (curFile == null) {
            tableModel.setImpData(new HashMap<String, String>());
            return;
        }

        Map<String, String> title2contents = ImportManager.readCsvFile(curFile, getCurSeparator());
        tableModel.setImpData(title2contents);
    }


    private void separatorChanged() {
        updatePreview(curFile);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        sepRadioPanel = new JPanel();
        tabRadioButton = new JRadioButton();
        semRadioButton = new JRadioButton();
        spaceRadioButton = new JRadioButton();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        previewTable = new JTable();
        enterRadioButton = new JRadioButton();
        sepRadios = new ButtonGroup();

        //======== this ========
        setBorder(null);
        setPreferredSize(new Dimension(300, 150));
        setOpaque(false);
        setLayout(new BorderLayout());

        //======== sepRadioPanel ========
        {
            sepRadioPanel.setBorder(new TitledBorder(null, "Separator", TitledBorder.LEADING, TitledBorder.TOP));
            sepRadioPanel.setLayout(new GridLayout(1, 4));

            //---- tabRadioButton ----
            tabRadioButton.setText("TAB");
            tabRadioButton.setSelected(true);
            tabRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    separatorChanged();
                }
            });
            sepRadioPanel.add(tabRadioButton);

            //---- semRadioButton ----
            semRadioButton.setText(";");
            semRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    separatorChanged();
                }
            });
            sepRadioPanel.add(semRadioButton);

            //---- spaceRadioButton ----
            spaceRadioButton.setText("\" \"");
            spaceRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    separatorChanged();
                }
            });
            sepRadioPanel.add(spaceRadioButton);
        }
        add(sepRadioPanel, BorderLayout.NORTH);

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP));
            panel1.setLayout(new BorderLayout());

            //======== scrollPane1 ========
            {
                scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                //---- previewTable ----
                previewTable.setModel(new DefaultTableModel(
                        new Object[][]{
                                {null, null},
                                {null, null},
                        },
                        new String[]{
                                "Question", "Answer"
                        }
                ));
                previewTable.setPreferredScrollableViewportSize(new Dimension(10, 100));
                scrollPane1.setViewportView(previewTable);
            }
            panel1.add(scrollPane1, BorderLayout.CENTER);
        }
        add(panel1, BorderLayout.CENTER);

        //---- enterRadioButton ----
        enterRadioButton.setText("ENTER");
        enterRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                separatorChanged();
            }
        });

        //---- sepRadios ----
        sepRadios.add(tabRadioButton);
        sepRadios.add(semRadioButton);
        sepRadios.add(spaceRadioButton);
        sepRadios.add(enterRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel sepRadioPanel;
    private JRadioButton tabRadioButton;
    private JRadioButton semRadioButton;
    private JRadioButton spaceRadioButton;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTable previewTable;
    private JRadioButton enterRadioButton;
    private ButtonGroup sepRadios;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}


class PreviewTableModel extends DefaultTableModel implements CategoryTreeSelectionListener {


    private static final String[] columnNames = new String[]{"Question", "Answer"};

    private java.util.List<String> questions;
    private Map<String, String> impData = new HashMap<String, String>();


    public PreviewTableModel() {
        super(new Object[][]{}, columnNames);
    }


    public void setImpData(Map<String, String> impData) {
        if (impData == null)
            return;

        this.impData = impData;
        this.questions = new ArrayList<String>(impData.keySet());

        if (impData.size() != questions.size()) {
            throw new RuntimeException("mismatch between the numbers of questions and answers");
        }

        fireTableDataChanged();
    }


    public int getRowCount() {
        if (impData == null) // this is the case only when the constructor is called
            return 0;
        else
            return impData.size();
    }


    public int getColumnCount() {
        return columnNames.length;
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (questions.size() < rowIndex || columnIndex > 1)
            return null;

        if (columnIndex == 0)
            return questions.get(rowIndex);
        else
            return impData.get(questions.get(rowIndex));
    }


    public void categorySelectionChanged(java.util.List<CardFile> selectedFiles, Set<Category> selCategories) {
    }
}