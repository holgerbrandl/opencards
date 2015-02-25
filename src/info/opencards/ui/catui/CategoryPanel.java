/*
 * Created by JFormDesigner on Wed Aug 29 22:42:01 CEST 2007
 */

package info.opencards.ui.catui;

import javax.swing.*;
import java.awt.*;


/**
 * @author Holger Brandl
 */
public class CategoryPanel extends JPanel {


    public CategoryPanel() {
        initComponents();
    }


    public CategoryTree getCatTree() {
        return catTree;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        catTree = new CategoryTree();

        //======== this ========
        setLayout(new BorderLayout());

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(catTree);
        }
        add(scrollPane1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private CategoryTree catTree;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

