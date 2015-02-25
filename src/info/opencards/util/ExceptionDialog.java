/*
 * Created by JFormDesigner on Tue Aug 14 22:25:52 CEST 2007
 */

package info.opencards.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class ExceptionDialog extends JDialog {


    private StackTraceElement shortErrorMsg;
    private String longErrorMsg;


    public ExceptionDialog(Frame owner) {
        super(owner);
        initComponents();
    }


    public ExceptionDialog(Dialog owner) {
        super(owner);
        initComponents();
    }


    private void submitButtonPressed() {
        String subject = convertToMailURIFormat("OpenCards problem report: " + shortErrorMsg);
        String body = convertToMailURIFormat(
                "Dear OpenCards-team,\n\nthe following problem occurred while I was using OpenCards:" +
                        " \n\n" + longErrorMsg + "\n\nAdd more comments here if necessary!!\n\n Best, ...");

        String mailToArg = "mailto:opencards4ppt@gmail.com?subject=" + subject + "&body=" + body;

        try {
            URI reportURI = new URI(mailToArg);
            Desktop.getDesktop().mail(reportURI);
        } catch (Throwable t) {
            System.err.println("problem report submission failed because of " + t.toString());
        }

        // dispose the window
        dispose();
    }


    private String convertToMailURIFormat(String s) {
        return s.replace(" ", "%20").replace("\n", "%0D%0A");
    }


    private void problemPanelMouseReleased(MouseEvent e) {
        if (!e.isPopupTrigger())
            return;

        JTextComponent tc = problemPanel;
        JPopupMenu menu = new JPopupMenu();
        menu.add(new CopyAction(tc));
        menu.add(new SelectAllAction(tc));

        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), tc);
        menu.show(tc, pt.x, pt.y);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        dialogPane = new JPanel();
        explanationArea = new JTextPane();
        submitReportButton = new JButton();
        scrollPane1 = new JScrollPane();
        problemPanel = new JEditorPane();

        //======== this ========
        setTitle(bundle.getString("ExceptionDialog.title"));
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //---- explanationArea ----
            explanationArea.setText(bundle.getString("ExceptionDialog.explanationArea.whatshappend"));
            explanationArea.setEditable(false);
            explanationArea.setBackground(UIManager.getColor("ArrowButton.background"));
            dialogPane.add(explanationArea, BorderLayout.CENTER);

            //---- submitReportButton ----
            submitReportButton.setText(bundle.getString("ExceptionDialog.explanationArea.submitissue"));
            submitReportButton.setFont(submitReportButton.getFont().deriveFont(submitReportButton.getFont().getStyle() | Font.BOLD));
            submitReportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    submitButtonPressed();
                }
            });
            dialogPane.add(submitReportButton, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(435, 155);
        setLocationRelativeTo(null);

        //======== scrollPane1 ========
        {

            //---- problemPanel ----
            problemPanel.setEditable(false);
            problemPanel.setEnabled(false);
            problemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    problemPanelMouseReleased(e);
                }
            });
            scrollPane1.setViewportView(problemPanel);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JTextPane explanationArea;
    private JButton submitReportButton;
    private JScrollPane scrollPane1;
    private JEditorPane problemPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    /**
     * Copies the thread information and the exception statcktrace to the error window.
     */
    public void showError(Thread t, Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("OpenCards version: " + AboutDialog.OPENCARDS_VERSION + "\n");
        sb.append("Platform: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        sb.append("Java Version: " + System.getProperty("java.version", "Unkown Java version") + " - " + System.getProperty("java.vendor", "Unkown VM-vendor"));

        String msg = "\n\n" + t.toString() + "\n\n" + e + "\n\n";
//        for (int i = 0; i < e.getStackTrace().length; i++) {
        for (int i = 0; i < 10; i++) {
            StackTraceElement traceElement = e.getStackTrace()[i];
            msg += traceElement.toString() + "\n";
        }

        sb.append(msg);

        longErrorMsg = sb.toString();
        shortErrorMsg = e.getStackTrace()[0];

        problemPanel.setText(longErrorMsg);
    }


    // a little test app for the dialog
    public static void main(String[] args) {
        ExceptionDialog dialog = new ExceptionDialog((Dialog) null);

        String s = null;

        // create an exception
        try {
            s.replace("foo", "bar");
        } catch (Throwable t) {
            dialog.showError(new Thread(), t);
            dialog.setVisible(true);
        }
    }
}


// @author Santhosh Kumar T - santhosh@in.fiorano.com
class CopyAction extends AbstractAction {


    private final JTextComponent comp;


    public CopyAction(JTextComponent comp) {
        super("Copy");
        this.comp = comp;
    }


    public void actionPerformed(ActionEvent e) {
        comp.copy();
    }


    public boolean isEnabled() {
        return comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}


// @author Santhosh Kumar T - santhosh@in.fiorano.com
class SelectAllAction extends AbstractAction {


    private final JTextComponent comp;


    public SelectAllAction(JTextComponent comp) {
        super("Select All");
        this.comp = comp;
    }


    public void actionPerformed(ActionEvent e) {
        comp.selectAll();
    }


    public boolean isEnabled() {
        return comp.isEnabled()
                && comp.getText().length() > 0;
    }
}
