/*
 * Created by JFormDesigner on Sat Jun 30 21:10:08 CEST 2007
 */

package info.opencards.util;

import info.opencards.Utils;
import info.opencards.ui.actions.URLAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class AboutDialog extends JDialog {


    public static final String OPENCARDS_VERSION = "2.3";
    public static final String OC_WEBSITE = "http://opencards.info/";


    public AboutDialog(JFrame owner) {
        super(owner);
        initComponents();

//        donateButton.setAction(new URLAction("", new ImageIcon(Utils.loadResource("icons/paypal_donate.gif")), "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5YTSWDUXLXT26"));
        donateButton.setAction(new URLAction("Support OpenCards with a small donation", null, "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5YTSWDUXLXT26"));

        ocLabelIcon.setIcon(new ImageIcon(Utils.loadResource(("oc-biglogo.png"))));
        ocLabelIcon.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                new URLAction(null, OC_WEBSITE).actionPerformed(null);
            }
        });

        String text = "<html><p><b>Version: " + OPENCARDS_VERSION +
                "</b></p>" +
                "</p></p>" +
                "<p><br>Homepage:     http://www.opencards.info</p>" +
                "<p></p>" +
                "<p>OpenCards is published under BSD-style license.</p>" +
                "<p>Copyright © 2016 Holger Brandl and contributors.</p>" +
                "<p></p>" +
                "<p>Contains XStream © 2003-20011 Joe Walnes.</p>" +
                "<p>Contains JFreeChart © 2009 Object Refinery Limited</p>" +
                "</html>";


        infoText.setText(text);

        UIUtils.actionOnEsc(this, new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                closeButtonActionPerformed(null);
            }
        });
    }


    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        JPanel panel3 = new JPanel();
        donateButton = new JButton();
        closeButton = new JButton();
        JPanel contentPanel = new JPanel();
        infoText = new JLabel();
        ocLabelIcon = new JLabel();

        //======== this ========
        setResizable(false);
        setTitle(bundle.getString("AboutDialog.this.title"));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel3 ========
        {
            panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0, 95, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};
            panel3.add(donateButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

            //---- closeButton ----
            closeButton.setText(bundle.getString("General.close"));
            closeButton.setFont(null);
            closeButton.addActionListener(e -> closeButtonActionPerformed(e));
            panel3.add(closeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel3, BorderLayout.SOUTH);

        //======== contentPanel ========
        {
            contentPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
            contentPanel.setLayout(new BorderLayout());

            //---- infoText ----
            infoText.setBorder(new EmptyBorder(20, 25, 25, 20));
            contentPanel.add(infoText, BorderLayout.CENTER);

            //---- ocLabelIcon ----
            ocLabelIcon.setBorder(new EtchedBorder());
            contentPanel.add(ocLabelIcon, BorderLayout.NORTH);
        }
        contentPane.add(contentPanel, BorderLayout.CENTER);
        setSize(485, 420);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JButton donateButton;
    private JButton closeButton;
    private JLabel infoText;
    private JLabel ocLabelIcon;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
