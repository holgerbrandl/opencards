/*
 * Created by JFormDesigner on Thu Sep 06 23:30:59 CEST 2007
 */

package info.opencards.ui;

import info.opencards.CardFileBackend;
import info.opencards.Utils;
import info.opencards.core.*;
import info.opencards.ui.actions.HelpAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Holger Brandl
 */
public class AbstractLearnDialog extends JPanel implements ItemValuater, KeyEventPostProcessor {


    private final PresenterProxy presProxy;

    Item curItem;

    private static final String SCORE_CONTROLS = "scoreControls";
    private static final String SHOW_CONTROLS = "showControls";

    // normally only the current learn-method will be registerd as scoring listener to this method
    private final List<ScoringListener> scoringListeners = new ArrayList<ScoringListener>();

    public final static int SCORE_1 = 1;
    private final static int SCORE_2 = 2;
    private final static int SCORE_3 = 3;
    private final static int SCORE_4 = 4;
    public final static int SCORE_5 = 5;

    public static final int INVALID_ITEM = -123;
    public static final int SKIP_ITEM = -124;
    public static final int SESSION_INTERRUPTED = -125;

    private boolean doIgnoreScoresUntilNewItem;
    private boolean disableKeyPP;
    private boolean wasAlreadyCanceled;


    protected AbstractLearnDialog() {
        initComponents();

        this.presProxy = CardFileBackend.getBackend().getPresProxy();

        ResourceBundle bundle = Utils.getRB();
        oneButton.setAction(new ScoreAction(bundle.getString("AbstractLearnDialog.oneButton.text"), SCORE_1));
        twoButton.setAction(new ScoreAction(bundle.getString("AbstractLearnDialog.twoButton.text"), SCORE_2));
        threeButton.setAction(new ScoreAction(bundle.getString("AbstractLearnDialog.threeButton.text"), SCORE_3));
        fourButton.setAction(new ScoreAction(bundle.getString("AbstractLearnDialog.fourButton.text"), SCORE_4));
        fiveButton.setAction(new ScoreAction(bundle.getString("AbstractLearnDialog.fiveButton.text"), SCORE_5));

        controlContainer.add(showControlsContainer, SHOW_CONTROLS);
        controlContainer.add(scoreButtonsContainer, SCORE_CONTROLS);


//        addWindowListener(new WindowAdapter() {
//
//            public void windowClosed(WindowEvent e) {
//                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(AbstractLearnDialog.this);
//            }
//        });

        helpButton.setAction(getHelpAction());

    }


    protected HelpAction getHelpAction() {
        return new HelpAction("ltm.html");
    }


    public boolean postProcessKeyEvent(KeyEvent e) {
        if (!e.paramString().startsWith("KEY_RELEASED") || disableKeyPP)
            return false;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                cancelLearningButtonActionPerformed();
                break;
            case KeyEvent.VK_UP:
                skipCardButtonActionPerformed();
                break;
//            case KeyEvent.VK_C:
//                // open the configuration dialog for the current item
//                if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
//                    disableKeyPP = true;
//                    showItemConfigDialog();
//                }
        }

        if (!isShowingComplete()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_DOWN:
                    showCompleteCardButtonActionPerformed();
                    break;
            }

            return true;
        }

        return false;
    }


    protected boolean isShowingComplete() {
        return scoreButtonsContainer.isShowing();
    }


    public void score(Item item) {
        doIgnoreScoresUntilNewItem = false;

        curItem = item;

        boolean showSuccessful = presProxy.showCardQuestion(curItem);
        if (showSuccessful) {
            ((CardLayout) controlContainer.getLayout()).first(controlContainer);
            titleLabel.setText((Utils.getRB().getString("AbstractLearnDialog.title.choose")));
            showCompleteCardButton.requestFocusInWindow();

        } else {
            processInvalidItem();
        }
    }


    void processInvalidItem() {
        publishItemScore(AbstractLearnDialog.INVALID_ITEM);
    }


    public void prepareLearnSession() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
    }


    public void prepareFileSession(CardFile cardFile) {
        presProxy.openCardFile(cardFile);
    }


    public void startFileSession(CardFile cardFile, ItemCollection cardItemCollection) {
        Utils.log("starting file session : " + cardFile.toString());
//        presProxy.openCardFile(cardFile);
        // --> moved to prepareFileSession. this will make OC to sync with the already open file and not with the
        // closed one, which should descrease the file-transition tiem

        // now renable all controls
        enableControls(true);
        progressBar.setIndeterminate(false);

        presProxy.startFileSession(cardItemCollection);
    }


    private void enableControls(boolean doEnable) {
        oneButton.setEnabled(doEnable);
        twoButton.setEnabled(doEnable);
        threeButton.setEnabled(doEnable);
        fourButton.setEnabled(doEnable);
        fiveButton.setEnabled(doEnable);

        skipCardButton.setEnabled(doEnable);
        showCompleteCardButton.setEnabled(doEnable);

        oneButton.paintImmediately(oneButton.getBounds());
    }


    private void disableControls() {
        enableControls(false);

        // set progress bar state
        progressBar.setIndeterminate(true);
        progressBar.setString(Utils.getRB().getString("AbstractLearnDialog.loadingNextFile"));
    }


    public void finishedFileSession(CardFile cardFile) {
        presProxy.stopFileSession();

        // disable all controls and set status to 'loading'
        disableControls();
    }


    public void finishedLearnSession() {
        presProxy.stopLearnSession();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(this);
    }


    /**
     * Informs all registered scoring listeners about current event. (Normally this will be only the used LearnMethod).
     * <p/>
     * The method is synchronized to make that sure that 'doIgnoreScoresUntilNewItem' is set correctly.
     * <p/>
     * This method is invoked exactly one for each item being processed. It is invoked only via awt-events like pressed
     * buttons or keys.
     */
    synchronized void publishItemScore(final int score) {
        if (doIgnoreScoresUntilNewItem) { // if a score is currently being processed do nothing but wait for the next item
            System.err.println("ignore item score because of pending thread");
            return;
        }

        System.err.println("processs item score " + score);

        doIgnoreScoresUntilNewItem = true;
        final Item scoreItem = curItem;

        // The thread is necessary to decouple the awt-thread from possible time consuming operations like (file IO,
        // synchronization, scheduler-setup, card-file switsching). Basically it just publishes an event code to the
        // used learn-method which will pursue the necessary action.

//        new Thread() {
//
//            public void run() {
//                super.run();
        // although we loop here to keep all as general as posisble, the only scoring listener that is
        // currently used is the learnMethod (Leitner or UltraShortSM2)
        for (int i = 0; i < scoringListeners.size(); i++) {
            scoringListeners.get(i).itemScored(scoreItem, score);
        }
//            }
//        }.start();
    }


    synchronized void cancelLearningButtonActionPerformed() {
        if (wasAlreadyCanceled)
            return;

        wasAlreadyCanceled = true;

        // used until oc 0.16, but removed to avoid multiple acess to the Impress from different threads in an unsynced manner
        //showCompleteCardButtonActionPerformed();
        publishItemScore(SESSION_INTERRUPTED);
    }


    protected void showCompleteCardButtonActionPerformed() {
        boolean wasSuccessful = presProxy.showCompleteCard(curItem);

        titleLabel.setText(Utils.getRB().getString("AbstractLearnDialog.title"));
        ((CardLayout) controlContainer.getLayout()).last(controlContainer);
        invalidate();

        if (!wasSuccessful) {
            processInvalidItem();
        }
    }


    void skipCardButtonActionPerformed() {
        presProxy.showCompleteCard(curItem);
        publishItemScore(SKIP_ITEM);
    }


    public void updateStatus(int completeness, String msg) {
        progressBar.setValue(completeness);
        progressBar.setString(msg);
    }


    /**
     * Adds a new listener.
     */
    public void addScoringListener(ScoringListener l) {
        if (l == null || scoringListeners.contains(l))
            return;

        scoringListeners.add(l);
    }


    /**
     * Removes a listener.
     */
    public void removeScoringListener(ScoringListener l) {
        if (l == null)
            return;

        scoringListeners.remove(l);
    }


    private void showItemConfigDialog() {
        // we create a new thread here to avoid a blocking of the awt thread.
        new Thread() {

            public void run() {
                super.run();

                List<FlashCard> dummyCardList = Arrays.asList(curItem.getFlashCard());
                CardFilePropsDialog propsDialog = new CardFilePropsDialog(null, AbstractLearnDialog.this.presProxy.getCurCardFile(), dummyCardList);
                propsDialog.setIsLTMProps(AbstractLearnDialog.this instanceof SMLearnDialog);
                propsDialog.setVisible(true);

                // this hacks around the problem
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                disableKeyPP = false;
            }
        }.start();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        panel5 = new JPanel();
        titleLabel = new JLabel();
        controlContainer = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel4 = new JPanel();
        progressBar = new JProgressBar();
        panel6 = new JPanel();
        helpButton = new JButton();
        skipCardButton = new JButton();
        button1 = new JButton();
        learnGraphContainer = new JPanel();
        twoButton = new JButton();
        oneButton = new JButton();
        threeButton = new JButton();
        fourButton = new JButton();
        fiveButton = new JButton();
        scoreButtonsContainer = new JPanel();
        showControlsContainer = new JPanel();
        showCompleteCardButton = new JButton();
        JButton cancelLearningButton = new JButton();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{1.0, 0.0, 0.0, 1.0E-4};

        //======== panel5 ========
        {
            panel5.setLayout(new GridBagLayout());
            ((GridBagLayout) panel5.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) panel5.getLayout()).rowHeights = new int[]{0, 0, 0};
            ((GridBagLayout) panel5.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) panel5.getLayout()).rowWeights = new double[]{0.0, 1.0, 1.0E-4};
            panel5.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

            //======== controlContainer ========
            {
                controlContainer.setLayout(new CardLayout());
            }
            panel5.add(controlContainer, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //======== panel1 ========
        {
            panel1.setLayout(new BorderLayout());

            //======== panel3 ========
            {
                panel3.setLayout(new BorderLayout());

                //======== panel2 ========
                {
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{1.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};
                }
                panel3.add(panel2, BorderLayout.CENTER);

                //======== panel4 ========
                {
                    panel4.setLayout(new BorderLayout());

                    //---- progressBar ----
                    progressBar.setStringPainted(true);
                    panel4.add(progressBar, BorderLayout.CENTER);

                    //======== panel6 ========
                    {
                        panel6.setLayout(new FlowLayout());

                        //---- helpButton ----
                        helpButton.setText(bundle.getString("General.help"));
                        panel6.add(helpButton);

                        //---- skipCardButton ----
                        skipCardButton.setText(bundle.getString("AbstractLearnDialog.skipCardButton.txt"));
                        skipCardButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                skipCardButtonActionPerformed();
                            }
                        });
                        panel6.add(skipCardButton);

                        //---- button1 ----
                        button1.setText(bundle.getString("LearnDialog.button1.text"));
                        button1.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                cancelLearningButtonActionPerformed();
                            }
                        });
                        panel6.add(button1);
                    }
                    panel4.add(panel6, BorderLayout.EAST);
                }
                panel3.add(panel4, BorderLayout.SOUTH);
            }
            panel1.add(panel3, BorderLayout.CENTER);
        }
        add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //======== learnGraphContainer ========
        {
            learnGraphContainer.setLayout(new BorderLayout());
        }
        add(learnGraphContainer, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        //---- twoButton ----
        twoButton.setText(bundle.getString("AbstractLearnDialog.twoButton.text"));
        twoButton.setFont(twoButton.getFont().deriveFont(twoButton.getFont().getSize() + 4f));

        //---- oneButton ----
        oneButton.setText(bundle.getString("AbstractLearnDialog.oneButton.text"));
        oneButton.setFont(oneButton.getFont().deriveFont(oneButton.getFont().getSize() + 4f));

        //---- threeButton ----
        threeButton.setText(bundle.getString("AbstractLearnDialog.threeButton.text"));
        threeButton.setFont(threeButton.getFont().deriveFont(threeButton.getFont().getSize() + 4f));

        //---- fourButton ----
        fourButton.setText(bundle.getString("AbstractLearnDialog.fourButton.text"));
        fourButton.setFont(fourButton.getFont().deriveFont(fourButton.getFont().getSize() + 4f));

        //---- fiveButton ----
        fiveButton.setText(bundle.getString("AbstractLearnDialog.fiveButton.text"));
        fiveButton.setFont(fiveButton.getFont().deriveFont(fiveButton.getFont().getSize() + 4f));

        //======== scoreButtonsContainer ========
        {
            scoreButtonsContainer.setMinimumSize(new Dimension(0, 50));
            scoreButtonsContainer.setPreferredSize(new Dimension(0, 50));
            scoreButtonsContainer.setLayout(new BorderLayout());
        }

        //======== showControlsContainer ========
        {
            showControlsContainer.setLayout(new GridBagLayout());
            ((GridBagLayout) showControlsContainer.getLayout()).columnWidths = new int[]{0, 0};
            ((GridBagLayout) showControlsContainer.getLayout()).rowHeights = new int[]{0, 0};
            ((GridBagLayout) showControlsContainer.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
            ((GridBagLayout) showControlsContainer.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

            //---- showCompleteCardButton ----
            showCompleteCardButton.setText(bundle.getString("AbstractLearnDialog.showCompleteCardButton.text"));
            showCompleteCardButton.setFont(showCompleteCardButton.getFont().deriveFont(showCompleteCardButton.getFont().getSize() + 4f));
            showCompleteCardButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showCompleteCardButtonActionPerformed();
                }
            });
            showControlsContainer.add(showCompleteCardButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        }

        //---- cancelLearningButton ----
        cancelLearningButton.setText(bundle.getString("AbstractLearnDialog.cancelLearningButton.text"));
        cancelLearningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelLearningButtonActionPerformed();
            }
        });
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel5;
    private JLabel titleLabel;
    protected JPanel controlContainer;
    protected JProgressBar progressBar;
    private JPanel panel6;
    private JButton helpButton;
    private JButton skipCardButton;
    private JButton button1;
    protected JPanel learnGraphContainer;
    protected JButton twoButton;
    protected JButton oneButton;
    protected JButton threeButton;
    protected JButton fourButton;
    protected JButton fiveButton;
    protected JPanel scoreButtonsContainer;
    protected JPanel showControlsContainer;
    private JButton showCompleteCardButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    class ScoreAction extends AbstractAction {


        private final int actionScore;


        public ScoreAction(String name, int actionScore) {
            super(name);

            this.actionScore = actionScore;
            putValue(NAME, name);
        }


        public void actionPerformed(ActionEvent e) {
            publishItemScore(actionScore);
        }


        public int getActionScore() {
            return actionScore;
        }
    }
}
