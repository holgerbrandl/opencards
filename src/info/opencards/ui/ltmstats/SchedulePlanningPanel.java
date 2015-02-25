package info.opencards.ui.ltmstats;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.learnstrats.ltm.LTMCollection;
import info.opencards.learnstrats.ltm.ScheduleUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
class SchedulePlanningPanel extends JPanel {


    private final DefaultCategoryDataset dataset;
    private final ValueAxis valueAxis;

    private static final int NUM_HIST_BINS = 8;
    private Collection<CardFile> currentFiles;


    public SchedulePlanningPanel() {
        setLayout(new BorderLayout());

        // setup the stacked bar chart
        dataset = new DefaultCategoryDataset();
        final JFreeChart chart = ChartFactory.createStackedBarChart(
                null,  // chart title
                Utils.getRB().getString("CardTableModel.stats.weekSchedule"),                  // domain axis label
//                "# cards",                     // range axis label
                null,                     // range axis label
                dataset,                     // data
                PlotOrientation.VERTICAL,    // the plot orientation
                false,                        // legend
                true,                        // tooltips
                false                        // urls
        );

        StackedBarRenderer renderer = new StackedBarRenderer();
        renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));

        //green
        renderer.setItemMargin(0.0);
        Paint greenPaint = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88));
        renderer.setSeriesPaint(1, greenPaint);
        renderer.setSeriesToolTipGenerator(1, new StandardCategoryToolTipGenerator());


        //blue
        Paint bluePaint = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0x22, 0xFF), 0.0f, 0.0f, new Color(0x88, 0x88, 0xFF));
        renderer.setSeriesPaint(0, bluePaint);
        renderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator());


//        Paint p2 = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x88, 0x88));
//        renderer.setSeriesPaint(1, p2);
//        renderer.setSeriesToolTipGenerator(1, new StandardCategoryToolTipGenerator());
//
//        Paint p3 = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88));
//        renderer.setSeriesPaint(2, p3);
//        renderer.setSeriesToolTipGenerator(2, new StandardCategoryToolTipGenerator());

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setRenderer(renderer);
        valueAxis = plot.getRangeAxis();
        TickUnits units = (TickUnits) NumberAxis.createIntegerTickUnits();
        valueAxis.setStandardTickUnits(units);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPopupMenu(null);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        add(chartPanel, BorderLayout.CENTER);

        rebuildPanel(new HashSet<CardFile>());
    }


    void rebuildPanel(Collection<CardFile> currentFiles) {
        if (currentFiles != null)
            this.currentFiles = currentFiles;

        dataset.clear();

        if (this.currentFiles == null)
            return;

        computeScheduleHist(this.currentFiles);
//        set2EDistribution(currentFiles);
    }


    private boolean computeScheduleHist(Collection<CardFile> currentFiles) {

        Map<Integer, List<Item>> schedule = new HashMap<Integer, List<Item>>();

        for (int i = 0; i < NUM_HIST_BINS; i++) {

            for (CardFile currentFile : currentFiles) {
                LTMCollection ltmCollection = currentFile.getFlashCards().getLTMItems();

                if (!schedule.containsKey(i)) {
                    schedule.put(i, new ArrayList<Item>());
                }

                if (i == 0) {
                    schedule.get(i).addAll(ltmCollection.getScheduledItems());
                } else {
                    schedule.get(i).addAll(ltmCollection.predictItemsForDate(ScheduleUtils.getIncDate(ScheduleUtils.getToday(), i), false));
                }
            }
        }

        // now set up the histogram

        int[] scheduleHist = new int[NUM_HIST_BINS];
        int[] newHist = new int[NUM_HIST_BINS];

        int numRemainingNewItems = -Integer.MAX_VALUE; // just to make sure that is never used
        int maxNewPerDay = ScheduleUtils.getMaxNewCardsPerDay();

        for (Integer scheduleDate : schedule.keySet()) {
            int numScheduledCards;
            List<Item> curScheduleSet = schedule.get(scheduleDate);
            List<Item> newItems = ScheduleUtils.getNewItems(curScheduleSet);

            // incorporate the knowledge about new cards when computing the schedule-histogram
            int curDatNewRation = 0;
            if (scheduleDate == 0) {

                int maxNewCardsToday = ScheduleUtils.getMaxCardToBeLearntToday();
                curDatNewRation = Math.min(maxNewCardsToday, newItems.size());

                numScheduledCards = (curScheduleSet.size() - newItems.size());

                numRemainingNewItems = newItems.size() - Math.min(maxNewCardsToday, newItems.size());
            } else {
                numRemainingNewItems += newItems.size();

                numScheduledCards = curScheduleSet.size() - newItems.size();
                if (numRemainingNewItems > 0) {
                    int dailyNewRation = Math.min(maxNewPerDay, numRemainingNewItems);
                    curDatNewRation = dailyNewRation;
//                    numScheduledCards += dailyNewRation;
                    numRemainingNewItems -= dailyNewRation;
                }
            }

            // set the value to the histogram
            if (scheduleDate < NUM_HIST_BINS) {
                scheduleHist[scheduleDate] = numScheduledCards;
                newHist[scheduleDate] = curDatNewRation;
            }
//            scheduleHist[scheduleDate >= NUM_HIST_BINS - 1 ? NUM_HIST_BINS - 1 : scheduleDate] = numScheduledCards;
        }

        List<Integer> eBinSize = new ArrayList<Integer>();

        dataset.addValue((double) newHist[0], "# new flashcards", Utils.getRB().getString("CardTableModel.when.today"));
        dataset.addValue((double) scheduleHist[0], "# flashcards", Utils.getRB().getString("CardTableModel.when.today"));
        eBinSize.add(scheduleHist[0] + newHist[0]);

        dataset.addValue((double) newHist[1], "# new flashcards", Utils.getRB().getString("CardTableModel.when.tomorrow"));
        dataset.addValue((double) scheduleHist[1], "# flashcards", Utils.getRB().getString("CardTableModel.when.tomorrow"));
        eBinSize.add(scheduleHist[1] + newHist[1]);


        for (int i = 2; i < scheduleHist.length; i++) {
            String days = Utils.getRB().getString("CardTableModel.when.days");
//            dataset.addValue((double) scheduleHist[i], "Scheduled", (i == scheduleHist.length - 1 ? (NUM_HIST_BINS-1) + "+" + " " + days : i + " " + days));
            dataset.addValue((double) newHist[i], "# new flashcards", i + " " + days);
            dataset.addValue((double) scheduleHist[i], "# flashcards", i + " " + days);
            eBinSize.add(scheduleHist[i] + newHist[i]);
        }

        valueAxis.setLowerBound(0);
        valueAxis.setUpperBound(1.1 * Math.max(Collections.max(eBinSize), 1));
        valueAxis.setAutoRange(false);

        return false;
    }


    public void selectionChanged(List<CardFile> selectedFiles) {
        rebuildPanel(selectedFiles);
    }
}
