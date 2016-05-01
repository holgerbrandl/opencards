package info.opencards.ui.table;

import info.opencards.core.CardFile;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Lists a set of card-sets including their properties.
 *
 * @author Holger Brandl
 */
public class CardSetTable extends JTable {


    private final CardTableModel cardTableModel;


    public CardSetTable() {
        cardTableModel = new CardTableModel(this);
        setModel(cardTableModel);


        // define sorting behavior

        setAutoCreateRowSorter(true);
        setRowSorter(new CardTableRowSorter(cardTableModel));

        setTransferHandler(new CardFileDragHandler(this));


        setDragEnabled(true);

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                List<CardFile> selFiles = new ArrayList<CardFile>();
                for (int rowIndex : getSelectedRows())
                    selFiles.add(getSortedRowFile(rowIndex));

                cardTableModel.tableSelectionedChanged(selFiles);
                if (cardTableModel.getRowCount() > 0) {
                    cardTableModel.fireTableRowsUpdated(0, cardTableModel.getRowCount() - 1);
                }
            }
        });


//        final int[][] lastSelection = new int[1][1];
//// Save selected row table
//        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                lastSelection[0] = getSelectedRows();
//            }
//        });
//
//// Restore selected raw table
//        cardTableModel.addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(lastSelection.length==0) return;
//                        for (int selRow : lastSelection[0]) {
////                            setRowSelectionInterval(selRow, selRow);
//                            addRowSelectionInterval(selRow, selRow);
//                        }
//                    }
//                });
//            }
//        });

        // Set the first visible column to 100 pixels wide
//        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        int vColIndex = 0;
//        TableColumn col = getColumnModel().getColumn(vColIndex);
//        col.setPreferredWidth(100);
    }


    public CardFile getSortedRowFile(int rowIndex) {
        return cardTableModel.getRowFile(getRowSorter().convertRowIndexToModel(rowIndex));
    }


    public String getToolTipText(MouseEvent event) {
        java.awt.Point p = event.getPoint();

        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);

        int rowIndex = rowAtPoint(p);
        CardFile cardFile = getSortedRowFile(rowIndex);
        return cardFile != null && realColumnIndex == 0 ? cardFile.getFileLocation().getAbsolutePath() : super.getToolTipText(event);
    }


    // see See http://stackoverflow.com/questions/254212/preserve-jtable-selection-across-tablemodel-change
    public void rebuildWithSelectionRestore() {

        // preserve selection calling fireTableDataChanged()
        final int[] sel = getSelectedRows();
        final int numRowsBefore = getRowCount();

        ((CardTableModel) getModel()).fireTableDataChanged();

        if (numRowsBefore == cardTableModel.getRowCount()) {

            for (int aSel : sel) {
                getSelectionModel().addSelectionInterval(aSel, aSel);
            }
        }
    }
}
