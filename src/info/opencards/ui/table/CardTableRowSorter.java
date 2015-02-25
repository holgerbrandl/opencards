package info.opencards.ui.table;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


class CardTableRowSorter extends TableRowSorter<CardTableModel> {


    public CardTableRowSorter(CardTableModel model) {
        super(model);

        // define the different column comparators

        // skip the name row because the default sorter does a great job here
        setComparator(1, new Comparator<StringifiedScheduleDate>() {
            public int compare(StringifiedScheduleDate o1, StringifiedScheduleDate o2) {
                return o1.compareTo(o2);
            }
        });

        setComparator(2, new IntegerComparator());
        setComparator(3, new IntegerComparator());

        // apply the default sorting

        List<SortKey> sortKeys = new ArrayList<SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        setSortKeys(sortKeys);
        sort();
    }


    private static class IntegerComparator implements Comparator<Integer> {


        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }
}
