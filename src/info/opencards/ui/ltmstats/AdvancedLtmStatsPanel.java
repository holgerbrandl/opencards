/*
 * Created by JFormDesigner on Thu Feb 14 00:14:12 CET 2008
 */

package info.opencards.ui.ltmstats;

import info.opencards.Utils;
import info.opencards.core.CardFile;
import info.opencards.core.Item;
import info.opencards.learnstrats.ltm.LTMItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Holger Brandl
 */
class AdvancedLtmStatsPanel extends JPanel {


    private Collection<CardFile> currentFiles;
    private HistogramDataset dataset;


    public AdvancedLtmStatsPanel() {
        initComponents();
    }


    public void rebuildPanel(Collection<CardFile> currentFiles) {
        removeAll();

        // setup the stacked bar chart
        dataset = new HistogramDataset();
//        dataset.addSeries("test", new double[]{1,2,3},1);
        final JFreeChart chart = ChartFactory.createHistogram(
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

        add(new ChartPanel(chart), BorderLayout.CENTER);
//        rebuildPanel(new HashSet<CardFile>());

        if (currentFiles != null)
            this.currentFiles = currentFiles;

//        dataset.setGroup(null);

        if (this.currentFiles == null)
            return;

        List<Double> eValues = new ArrayList<Double>();

        for (CardFile currentFile : currentFiles) {
            for (Item item : currentFile.getFlashCards().getLTMItems()) {
                LTMItem ltmItem = (LTMItem) item;
                eValues.add(ltmItem.getEFactor());
            }
        }
        double[] eVals = new double[eValues.size()];
        for (int i = 0; i < eValues.size(); i++) {
            eVals[i] = eValues.get(i);

        }


        if (eVals.length > 0)
            dataset.addSeries("test", eVals, 10);

        HistogramDataset dataset1 = new HistogramDataset();
        dataset1.addSeries("test", eVals, 10);

        repaint();

//        computeScheduleHist(this.currentFiles);
//        set2EDistribution(currentFiles);
    }


    public void selectionChanged(List<CardFile> curSelCardFiles) {
//        rebuildPanel(curSelCardFiles);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        //======== this ========
        setLayout(new BorderLayout());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
