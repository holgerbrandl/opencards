/*
 * Created by JFormDesigner on Tue Sep 20 21:24:48 CEST 2011
 */

package info.opencards.ui;

import info.opencards.core.ItemValuater;

import javax.swing.*;
import java.awt.*;


/**
 * @author Holger Brandl
 */
public class LearningModePanel extends JPanel {


    public LearningModePanel() {
        initComponents();
    }


    public JPanel getSlideRenderPanel() {
        return renderPanelContainer;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        renderPanelContainer = new JPanel();
        controlsPanel = new JPanel();

        //======== this ========
        setLayout(new BorderLayout());

        //======== renderPanelContainer ========
        {
            renderPanelContainer.setLayout(new BorderLayout());
        }
        add(renderPanelContainer, BorderLayout.CENTER);

        //======== controlsPanel ========
        {
            controlsPanel.setLayout(new BorderLayout());
        }
        add(controlsPanel, BorderLayout.SOUTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel renderPanelContainer;
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
