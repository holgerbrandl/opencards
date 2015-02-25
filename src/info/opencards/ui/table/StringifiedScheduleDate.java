package info.opencards.ui.table;

/**
 * A simple helper class the models the entries of the 'next on schedule'-columns entries in {@code CardSetTable}, in
 * order to implement an less hacky sorting for this columns
 *
 * @author Holger Brandl
 * @see info.opencards.ui.table.CardTableRowSorter
 */
class StringifiedScheduleDate implements Comparable {


    private final String msg;
    private final int dayDiff;


    public StringifiedScheduleDate(String msg, int dayDiff) {
        this.msg = msg;
        this.dayDiff = dayDiff;
    }


    public String toString() {
        return msg;
    }


    public int compareTo(Object o) {
        if (o instanceof StringifiedScheduleDate) {
            return dayDiff - ((StringifiedScheduleDate) o).dayDiff;
        }

        return -1;
    }
}
