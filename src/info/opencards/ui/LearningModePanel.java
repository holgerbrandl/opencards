/*
 * Created by JFormDesigner on Tue Sep 20 21:24:48 CEST 2011
 */

package info.opencards.ui;

import info.opencards.core.ItemValuater;
import info.opencards.pptintegration.PPTSlideRenderPanel;

import javax.swing.*;
import java.awt.*;


/**
 * @author Holger Brandl
 */
public class LearningModePanel extends JPanel {


    public LearningModePanel() {
        initComponents();
    }


    public PPTSlideRenderPanel getSlideRenderPanel() {
        return slideRenderPanel;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        slideRenderPanel = new PPTSlideRenderPanel();
        controlsPanel = new JPanel();

        //======== this ========
        setLayout(new BorderLayout());
        add(slideRenderPanel, BorderLayout.CENTER);

        //======== controlsPanel ========
        {
            controlsPanel.setLayout(new BorderLayout());
        }
        add(controlsPanel, BorderLayout.SOUTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private PPTSlideRenderPanel slideRenderPanel;
    private JPanel controlsPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void setControls(ItemValuater itemValuater) {
        controlsPanel.removeAll();
        controlsPanel.add((Component) itemValuater);

//        controlsPanel.revalidate();
//        controlsPanel.repaint();
//        slideRenderPanel.revalidate();
//        slideRenderPanel.repaint();
        revalidate();
        repaint();
    }
}
