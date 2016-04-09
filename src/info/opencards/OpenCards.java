/*
 * Created by JFormDesigner on Tue Sep 20 03:17:50 CEST 2011
 */

package info.opencards;

import info.opencards.core.CardFile;
import info.opencards.core.categories.Category;
import info.opencards.core.categories.CategoryUtils;
import info.opencards.ui.*;
import info.opencards.ui.actions.*;
import info.opencards.ui.lastmin.CramLernSettingsPanel;
import info.opencards.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class OpenCards extends JFrame {


    private final LearnManagerUI cardSetManager;
    private final LearningModePanel learningPanel;

    private static OpenCards instance;
    private final CardFileBackend backend;
    private CramLernSettingsPanel lastMinConfigView;


    public static void main(String[] args) {
        OpenCards openCards = new OpenCards();
        openCards.setVisible(true);
        openCards.doAfterSetup();
    }


    public static OpenCards getInstance() {
        return instance;
    }


    public void doAfterSetup() {
        CardFileAutoDiscovery.run(this, cardSetManager.categoryPanel.getCatTree().getRootCategory(), backend.getSerializer());
        InviteTranslatorsDialog.inviteForTranslation(this);
        UpdateChecker.check4Update(this);
        ExampleData.installExampleData(this);
    }


    public OpenCards() {
        // increment the startup counter
        Utils.getPrefs().putInt(Utils.PROP_STARTUP_COUNTER, Utils.getPrefs().getInt(Utils.PROP_STARTUP_COUNTER, 0) + 1);

        instance = this;
        initComponents();

        if (!System.getProperty("user.name", "franz").equals("holger"))
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());


        if (Utils.isMacOSX()) {
//            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        } else {
            setIconImage(new ImageIcon(Utils.loadResource("icons/oclogo.png")).getImage());

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    saveCatTreeBeforeQuit(OpenCards.this);
                }
            });
        }

        // link the menu entries to actions
        aboutMenuItem.setAction(new AboutAction(this));
        helpMenuItem.setAction(new HelpAction());
        flahCardsMenuItem.setAction(new URLAction("Flashcard Repository", "http://www.google.com/search?q=filetype%3Appt"));
        prefsMenuItem.setAction(new SettingsAction(this));
        importFlashcardsMenuItem.setAction(new ImportFlashcardsAction(this));


        // setup the ui for the category and the learning view
        backend = CardFileBackend.getBackend();

        cardSetManager = new LearnManagerUI(this, backend);
        learningPanel = new LearningModePanel();
        lastMinConfigView = new CramLernSettingsPanel();


        panelContainer.add(cardSetManager, "Learn Manager");
        panelContainer.add(lastMinConfigView, "LastMinute Setup Panel");
        panelContainer.add(learningPanel, "Learning Panel");


        // restore the previous position and size of the window
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getBounds();
        setBounds(LayoutRestorer.getInstance().getBounds("opencards", this, new Rectangle((int) (screenBounds.getWidth() / 2. - 400), (int) (screenBounds.getHeight() / 2. - 350), 800, 700)));


        // enable dropping of cardfiles from the os int the OC-window
        new DropTarget(this, new CardSetDndHandler());

        UIUtils.helpOnF1(this, "help");


        // auto hide the menu bar
//        http://stackoverflow.com/questions/2911887/auto-hide-jmenubar
//        http://stackoverflow.com/questions/1408080/mouselistener-on-jframe
        // http://www.eshca.net/java/books/javainsel8/javainsel_16_025.htm#mjd822bd01f04530f3827726d809c9ca38
//        if (!Utils.isMacOSX()) {
//            addMouseMotionListener(new MouseAdapter() {
//
//                @Override
//                public void mouseMoved(MouseEvent e) {
//                    getJMenuBar().setVisible(e.getY() < 50);
//                }
//            });
//            getJMenuBar().setVisible(false);
//        }
    }


    public static void saveCatTreeBeforeQuit(OpenCards openCards) {
        try { // wrapped because some users reported an npe in here, but its not clear why
            Category rootCategory = openCards.cardSetManager.categoryPanel.getCatTree().getRootCategory();
            CategoryUtils.serializeCategoryModel(rootCategory);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static LearnManagerUI getCardSetManager() {
        return instance.cardSetManager;
    }


    public static void showCategoryView() {
        ((CardLayout) instance.panelContainer.getLayout()).first(instance.panelContainer);
    }


    public static void showLearnView() {
        ((CardLayout) instance.panelContainer.getLayout()).last(instance.panelContainer);
    }


    public static void showLastMinConfigView(List<CardFile> cardFiles) {
        if (cardFiles != null) {
            instance.getLastMinConfgiView().configure(cardFiles);
        }

        instance.setTitle("OpenCards: " + Utils.getRB().getString("CramLernSettingsPanel.this.title"));
        ((CardLayout) instance.panelContainer.getLayout()).next(instance.panelContainer);
    }


    public LearningModePanel getLearnPanel() {
        return learningPanel;
    }


    public LearnManagerUI getCategoryView() {
        return cardSetManager;
    }


    private void quitMenuItemActionPerformed(ActionEvent e) {
        saveCatTreeBeforeQuit(OpenCards.this);
        setVisible(false);
        dispose();

        System.exit(0);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        helpMenuItem = new JMenuItem();
        importFlashcardsMenuItem = new JMenuItem();
        flahCardsMenuItem = new JMenuItem();
        prefsMenuItem = new JMenuItem();
        aboutMenuItem = new JMenuItem();
        quitMenuItem = new JMenuItem();
        panelContainer = new JPanel();

        //======== this ========
        setTitle("OpenCards");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText(bundle.getString("General.tools"));

                //---- helpMenuItem ----
                helpMenuItem.setText(bundle.getString("General.help"));
                menu1.add(helpMenuItem);

                //---- importFlashcardsMenuItem ----
                importFlashcardsMenuItem.setText(bundle.getString("OpenOffice.ocmenu.impcards"));
                menu1.add(importFlashcardsMenuItem);

                //---- flahCardsMenuItem ----
                flahCardsMenuItem.setText(bundle.getString("OpenOffice.ocmenu.getnewcards"));
                menu1.add(flahCardsMenuItem);
                menu1.addSeparator();

                //---- prefsMenuItem ----
                prefsMenuItem.setText(bundle.getString("General.prefs"));
                menu1.add(prefsMenuItem);

                //---- aboutMenuItem ----
                aboutMenuItem.setText(bundle.getString("OpenCardsUI.aboutButton.text"));
                menu1.add(aboutMenuItem);
                menu1.addSeparator();

                //---- quitMenuItem ----
                quitMenuItem.setText(bundle.getString("General.quitoc"));
                quitMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        quitMenuItemActionPerformed(e);
                    }
                });
                menu1.add(quitMenuItem);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //======== panelContainer ========
        {
            panelContainer.setLayout(new CardLayout());
        }
        contentPane.add(panelContainer, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem helpMenuItem;
    private JMenuItem importFlashcardsMenuItem;
    private JMenuItem flahCardsMenuItem;
    private JMenuItem prefsMenuItem;
    private JMenuItem aboutMenuItem;
    private JMenuItem quitMenuItem;
    private JPanel panelContainer;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void resetWindowTitle() {
        setTitle("OpenCards");
    }


    public CramLernSettingsPanel getLastMinConfgiView() {
        return lastMinConfigView;
    }
}
