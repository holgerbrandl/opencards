package info.opencards.ui.ltmstats;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.learnstrats.ltm.LTMItem;
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
 * A panel which shows the current ltm-learning state of a collection of flash-card files.
 *
 * @author Holger Brandl
 */
class LTMStateGraphPanel extends JPanel {


    private final DefaultCategoryDataset dataset;
    private final ValueAxis valueAxis;

    private static final int NUM_HIST_BINS = 9;
    private Collection<CardFile> currentFiles;
    private final String learnedPerfectly;
    private final String learnedNotYet;
    private final String learnedNaJa;


    public LTMStateGraphPanel() {
        setLayout(new BorderLayout());

        // setup the stacked bar chart
        dataset = new DefaultCategoryDataset();
        final JFreeChart chart = ChartFactory.createStackedBarChart(
                null,  // chart title
                Utils.getRB().getString("CardTableModel.stats.learnsuccess"),                  // domain axis label
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

        renderer.setItemMargin(0.0);
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

        learnedPerfectly = Utils.getRB().getString("CardTableModel.stats.perfect");
        learnedNotYet = Utils.getRB().getString("CardTableModel.stats.notatall");
        learnedNaJa = Utils.getRB().getString("CardTableModel.stats.well");
    }


    void rebuildPanel(Collection<CardFile> currentFiles) {
        if (currentFiles != null)
            this.currentFiles = currentFiles;

        dataset.clear();

        if (this.currentFiles == null)
            return;

        set2NumRepHist(this.currentFiles);
//        set2EDistribution(currentFiles);
    }


    private boolean set2NumRepHist(Collection<CardFile> currentFiles) {
        List<Integer> numReps = new ArrayList<Integer>();

        for (CardFile currentFile : currentFiles) {
            for (Item item : currentFile.getFlashCards().getLTMItems()) {
                numReps.add(((LTMItem) item).getNumRepetition());
            }
        }

        if (numReps.isEmpty())
            return true;

        // compute the histgram
        int[] eHist = new int[NUM_HIST_BINS];

        for (Integer numRep : numReps) {
            eHist[numRep >= NUM_HIST_BINS - 1 ? NUM_HIST_BINS - 1 : numRep]++;
        }

        List<Integer> eBinSize = new ArrayList<Integer>();
        String emptyString = "";

        for (int i = 0; i < eHist.length; i += 1) {
            String columnTitle;
            if (i == eHist.length - 1)
                columnTitle = learnedPerfectly;
            else if (i == 0)
                columnTitle = learnedNotYet;
            else if (i == 4)
                columnTitle = learnedNaJa;
            else {
                emptyString += " ";
                columnTitle = emptyString;
            }

            dataset.addValue((double) eHist[i], "", columnTitle);
            eBinSize.add(eHist[i]);
//            dataset.addValue((double) eHist[i] + eHist[i+1], "Learned", columnTitle);
//            eBinSize.add(eHist[i] + eHist[i+1]);
        }

        valueAxis.setLowerBound(0);
        valueAxis.setUpperBound(1.1 * Collections.max(eBinSize));
        valueAxis.setAutoRange(false);

        return false;
    }


    public void selectionChanged(List<CardFile> selectedFiles) {
        rebuildPanel(selectedFiles);
    }
}
