package info.opencards.ui.table;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryChangeAdapter;
import info.opencards.core.categories.CategoryChangeListener;
import info.opencards.core.categories.CategoryUtils;
import info.opencards.learnstrats.ltm.*;
import info.opencards.ui.catui.CategoryTreeSelectionListener;

import javax.swing.table.DefaultTableModel;
import java.util.*;


/**
 * A custom table model which shows card-files including some basic statistics in each row.
 *
 * @author Holger Brandl
 */
public class CardTableModel extends DefaultTableModel implements CategoryTreeSelectionListener {


    private static final boolean[] columnEditable = new boolean[]{false, false, false, false};
    private static final String[] columnNames = new String[]{Utils.getRB().getString("CardTableModel.fileName"), Utils.getRB().getString("CardTableModel.nextScheduled"), Utils.getRB().getString("CardTableModel.numNewCards"), Utils.getRB().getString("CardTableModel.numCards")};

    private List<CardFile> curFiles = new ArrayList<CardFile>();
    private List<Category> curCategories = new ArrayList<Category>();
    private LTMProcessManager dummyLtmManager;


    public CardTableModel() {
        super(new Object[][]{}, columnNames);

//        refreshTableData();
    }


    public int getRowCount() {
        if (curFiles == null) // this is the case only when the constructor is called
            return 0;
        else
            return curFiles.size();
    }


    public int getColumnCount() {
        return columnNames.length;
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnEditable[columnIndex];
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        CardFile rowFile = curFiles.get(rowIndex);
        if (!rowFile.getFileLocation().isFile())
            return "can not find '" + rowFile + "'";

        LTMCollection rowItems = rowFile.getFlashCards().getLTMItems();

        switch (columnIndex) {
            case 0:
//                return rowFile.getFile().getName();
                return rowFile.getFileLocation().getName();
            case 1:
                return getNextScheduleDate(rowItems, rowFile);
//            case 1:
//                Double avgRetention = SM2.getAvgRetention(rowItems);
//                DecimalFormat df = new DecimalFormat("#.#");
//                return avgRetention != null ? df.format(avgRetention) : "N/A";
//            case 2:
//                return ScheduleUtils.getNewReducedItCo(rowItems.getScheduledItems(), ScheduleUtils.getMaxCardToBeLearntToday()).size();
            case 2:
                return ScheduleUtils.getNewItems(rowItems).size();
            case 3:
                return rowItems.size();
            case 50: // a special case to get the current file even from a resorted table
                return rowFile;
        }

        return null;
    }


    /**
     * package visible only to make referring components to use {@code CardTableModel.getSortedRowFile} instead.
     */
    CardFile getRowFile(int rowIndex) {
        // return the file with the index with respect to the current ordering of the table

        return curFiles.get(rowIndex);
    }


    /**
     * Clean up the current table model: remove no longer selected Categorys and add newly selected ones.
     */
    public void categorySelectionChanged(List<CardFile> selectedFiles, Set<Category> selCategories) {
        curCategories = new ArrayList<Category>(selCategories);

        curFiles = new ArrayList<CardFile>(CategoryUtils.extractSelectedFiles(selCategories));

        //sort files based on name
        Collections.sort(curFiles, new Comparator<CardFile>() {
            public int compare(CardFile o1, CardFile o2) {
                return o1.getFileLocation().getName().compareTo(o2.getFileLocation().getName());
            }
        });


        tableSelectionedChanged(curFiles);
        fireTableDataChanged();
    }


    public void tableSelectionedChanged(List<CardFile> selFiles) {
        // instantiate a dummy process-manager which gives us a preleminary schedule
        dummyLtmManager = new LTMProcessManager(new DummyValuator(), SM2.getFactory());
        dummyLtmManager.setupSchedule(selFiles);
    }


    public CategoryChangeListener getCatListener() {
        return new CategoryChangeAdapter() {
            public void registeredCardset(Category category, CardFile cardSet) {
                if (curFiles.contains(cardSet)) {
                    return;
                }

                if (curCategories.contains(category) && !curFiles.contains(cardSet)) {
                    curFiles.add(cardSet);
                }

                fireTableDataChanged();
            }


            public void unregisteredCardset(Category category, CardFile cardSet) {
                if (!curFiles.contains(cardSet)) {
                    return;
                }

                boolean isStillValid = false;
                for (Category curCategory : curCategories) {
                    if (CategoryUtils.recursiveCardFileCollect(curCategory).contains(cardSet)) {
                        isStillValid = true;
                        break;
                    }
                }

                if (!isStillValid) {
                    curFiles.remove(cardSet);
                }

                fireTableDataChanged();
            }
        };
    }


    public List<Category> getCurCatgories() {
        return curCategories;
    }


    /**
     * Returns the files to be contained currently within this model.
     */
    public Collection<CardFile> getCurFiles() {
        return new ArrayList<CardFile>(curFiles);
    }


    private StringifiedScheduleDate getNextScheduleDate(LTMCollection ltmCollection, CardFile rowFile) {
        if (ltmCollection.isEmpty())
            return new StringifiedScheduleDate(Utils.getRB().getString("CardTableModel.noValidSlides"), Integer.MAX_VALUE);

        Item nextItem = Collections.min(ltmCollection, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return ((LTMItem) o1).getNextScheduledDate().compareTo(((LTMItem) o2).getNextScheduledDate());
            }
        });

        Date nextScheduleDate = ((LTMItem) nextItem).getNextScheduledDate();

        int dayDiff = ScheduleUtils.getDayDiff(nextScheduleDate, ScheduleUtils.getToday());
        String msg;
        if (dayDiff <= 0) {
            msg = Utils.getRB().getString("CardTableModel.when.today");

            String numNewItems;
            Integer numAllItems;
            if (dummyLtmManager.isScheduled(rowFile)) {
                List<? extends Item> scheduledItems = dummyLtmManager.getScheduledItems(rowFile);
                ArrayList<Item> newItems = ScheduleUtils.getNewItems(scheduledItems);
                numAllItems = scheduledItems.size() - newItems.size();
                numNewItems = newItems.size() > 0 ? " +" + newItems.size() : "";
            } else {
                Date predictedSchedDate = ScheduleUtils.getIncDate(ScheduleUtils.getToday(), 0);
                List<? extends Item> scheduledItems = ltmCollection.predictItemsForDate(predictedSchedDate, true);
                ArrayList<Item> newItems = ScheduleUtils.getNewItems(scheduledItems);

                numAllItems = scheduledItems.size() - ScheduleUtils.getNewItems(scheduledItems).size();
                numNewItems = newItems.size() > 0 ? " +?" : "";
            }

            msg += " (" + (numAllItems) + numNewItems + ")";

        } else {
            Date predictedSchedDate = ScheduleUtils.getIncDate(ScheduleUtils.getToday(), dayDiff < 0 ? 0 : dayDiff);
            List<LTMItem> allItems = ltmCollection.predictItemsForDate(predictedSchedDate, true);

            if (dayDiff == 1) {
                msg = Utils.getRB().getString("CardTableModel.when.tomorrow");
            } else {
                msg = "in " + dayDiff + " " + Utils.getRB().getString("CardTableModel.when.days");
            }

            msg += " (" + allItems.size() + ")";
        }

        return new StringifiedScheduleDate(msg, dayDiff);
    }


    public LTMProcessManager getDummyLtmManager() {
        return dummyLtmManager;
    }
}
