package info.opencards.ui.lastmin;

import info.opencards.Utils;
import info.opencards.core.Item;
import info.opencards.learnstrats.leitner.LeitnerItem;
import info.opencards.learnstrats.leitner.LeitnerListener;
import info.opencards.learnstrats.leitner.LeitnerSystem;
import info.opencards.learnstrats.leitner.LeitnerUtils;
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
import java.util.ResourceBundle;


/**
 * A panel which shows the current learning state of the the flashcards in the current presentation.
 *
 * @author Holger Brandl
 */
class LeitnerStatePanel extends JPanel implements LeitnerListener {


    private LeitnerSystem myLeitnerSystem;
    private DefaultCategoryDataset dataset;
    private ValueAxis valueAxis;

    private LeitnerItem hightlightItem;
    private CategoryPlot plot;


    public LeitnerStatePanel() {
        setLayout(new BorderLayout());
    }


    public void setLeitnerSystem(LeitnerSystem leitnerSystem) {
        assert leitnerSystem != null;

        if (this.myLeitnerSystem != null)
            this.myLeitnerSystem.removeLearnSessionChangeListener(this);

        this.myLeitnerSystem = leitnerSystem;
        this.myLeitnerSystem.addLearnSessionChangeListener(this);

        // setup the stacked bar chart
        dataset = new DefaultCategoryDataset();
        final JFreeChart chart = ChartFactory.createStackedBarChart(
                null,  // chart title
                null,                  // domain axis label
//                "# cards",                     // range axis label
                null,                     // range axis label
                dataset,                     // data
                PlotOrientation.VERTICAL,    // the plot orientation
                false,                        // legend
                true,                        // tooltips
                false                        // urls
        );

        plot = (CategoryPlot) chart.getPlot();

        valueAxis = plot.getRangeAxis();
        TickUnits units = (TickUnits) NumberAxis.createIntegerTickUnits();
        valueAxis.setStandardTickUnits(units);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPopupMenu(null);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        removeAll();
        add(chartPanel, BorderLayout.CENTER);

        rebuildBoxBarchart();
    }


    private void reconfigureColorEncoding() {
        StackedBarRenderer renderer = new StackedBarRenderer();
        renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));

        renderer.setItemMargin(0.0);

        int barIndex = 0;

        //blue
        Paint p1 = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0x22, 0xFF), 0.0f, 0.0f, new Color(0x88, 0x88, 0xFF));
        renderer.setSeriesPaint(barIndex, p1);
        renderer.setSeriesToolTipGenerator(barIndex, new StandardCategoryToolTipGenerator());

        //green
        barIndex++;
        Paint p3 = new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88));
        renderer.setSeriesPaint(barIndex, p3);
        renderer.setSeriesToolTipGenerator(barIndex, new StandardCategoryToolTipGenerator());

        if (hightlightItem != null) {
            //red
            barIndex++;
            Paint p2 = new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x88, 0x88));
            renderer.setSeriesPaint(barIndex, p2);
            renderer.setSeriesToolTipGenerator(barIndex, new StandardCategoryToolTipGenerator());
        }

        plot.setRenderer(renderer);
    }


    private void rebuildBoxBarchart() {
        dataset.clear();
        reconfigureColorEncoding();

        ResourceBundle rb = Utils.getRB();

//        System.out.println("cur boxes state:\n" + LeitnerUtils.print(myLeitnerSystem));

        for (int i = 0; i < myLeitnerSystem.numBoxes(); i++) {
            java.util.List<LeitnerItem> flashCards = myLeitnerSystem.getBoxes().get(i);

            int numLearnt = LeitnerUtils.count(flashCards, LeitnerSystem.LEARNT);
            int numFailed = LeitnerUtils.count(flashCards, LeitnerSystem.FAILED);
            int numUnknown = LeitnerUtils.count(flashCards, LeitnerSystem.NEW);

            int boxIndex = i + 1;
            String boxName = myLeitnerSystem.numBoxes() > 5 ? boxIndex + "" : rb.getString("LeitnerStatePanel.box") + " " + boxIndex;

            if (hightlightItem != null & myLeitnerSystem.getBoxIndex(hightlightItem) == i) {
                if (hightlightItem.getState() == LeitnerSystem.NEW)
                    numUnknown--;
                else
                    numLearnt--;
            }

            dataset.addValue(numUnknown, "New flashcards", boxName);
//            dataset.addValue(numFailed, "Failed", boxName);
            dataset.addValue(numLearnt + numFailed, "Learnt", boxName);

            if (hightlightItem != null) {
                if (myLeitnerSystem.getBoxIndex(hightlightItem) == i) {
                    dataset.addValue(1, "Current flashcard", boxName);
                } else {
                    dataset.addValue(0, "Current flashcard", boxName);
                }
            }
        }

        valueAxis.setLowerBound(0);
        valueAxis.setUpperBound(Math.max(myLeitnerSystem.getAllCards().size(), 1) * 1.1);
        valueAxis.setAutoRange(false);

        revalidate();
    }


    public void newCard(Item flashcard) {
        rebuildBoxBarchart();
    }


    public void boxingChanged(Item... movedCards) {
        rebuildBoxBarchart();
    }


    public void removedCard(Item flashcard) {
        rebuildBoxBarchart();
    }


    /**
     * Highlights an item of a leitner system.
     */
    public void higlightItem(Item item) {
        assert item instanceof LeitnerItem;
        this.hightlightItem = (LeitnerItem) item;

        rebuildBoxBarchart();
    }
}
