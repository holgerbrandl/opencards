/*
 * Created by JFormDesigner on Sun Jul 01 10:57:26 CEST 2007
 */

package info.opencards.ui.lastmin;

import info.opencards.Utils;
import info.opencards.learnstrats.leitner.CardLimitLeitner;
import info.opencards.learnstrats.leitner.LearnAllLeitner;
import info.opencards.learnstrats.leitner.LeitnerLearnMethodFactory;
import info.opencards.learnstrats.leitner.TimeLimitLeitner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


/**
 * @author Holger Brandl
 */
public class LeitnerStrategyPanel extends JPanel {


    private static final String LEITLEARN_TIME_LIMIT = "leitlearn.timeLimitSpinner";
    private static final String LEITLEARN_CARD_LIMIT = "leitlearn.cardLimitSpinner";
    private static final String LEITLEARN_MODE = "leitlearn.mode";


    public LeitnerStrategyPanel() {
        initComponents();
    }


    public LeitnerLearnMethodFactory getLearnMethodFactory() {
        if (limitTimeOption.isSelected()) {
            return TimeLimitLeitner.getFactory((Integer) timeLimitSpinner.getValue());
        } else if (limitCardsOption.isSelected()) {
            return CardLimitLeitner.getFactory((Integer) cardLimitSpinner.getValue());
        } else if (learnAllOption.isSelected()) {
            return LearnAllLeitner.getFactory();
        }

        throw new RuntimeException("nothing is selected");
    }


    public void saveDefaults() {
        Preferences prefs = Utils.getPrefs();
        prefs.putInt(LEITLEARN_TIME_LIMIT, (Integer) timeLimitSpinner.getValue());
        prefs.putInt(LEITLEARN_CARD_LIMIT, (Integer) cardLimitSpinner.getValue());

        if (limitTimeOption.isSelected()) {
            prefs.putInt(LEITLEARN_MODE, 1);

        } else if (limitCardsOption.isSelected()) {
            prefs.putInt(LEITLEARN_MODE, 2);

        } else if (learnAllOption.isSelected()) {
            prefs.putInt(LEITLEARN_MODE, 0);
        }

        Utils.flushPreferences();
    }


    void initializeStrategy() {
        Preferences prefs = Utils.getPrefs();

        timeLimitSpinner.setValue(prefs.getInt(LEITLEARN_TIME_LIMIT, 10));
        cardLimitSpinner.setValue(prefs.getInt(LEITLEARN_CARD_LIMIT, 10));

        int mode = prefs.getInt(LEITLEARN_MODE, 0);

        switch (mode) {
            case 0:
                learnAllOption.setSelected(true);
                break;
            case 1:
                limitTimeOption.setSelected(true);
                break;
            case 2:
                limitCardsOption.setSelected(true);
                break;
        }
    }


    private void limitTimeOptionItemStateChanged() {
        timeLimitSpinner.setEnabled(limitTimeOption.isSelected());
    }


    private void limitCardsOptionItemStateChanged() {
        cardLimitSpinner.setEnabled(limitCardsOption.isSelected());
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ResourceBundle bundle = ResourceBundle.getBundle("info.opencards.translation");
        panel2 = new JPanel();
        learnAllOption = new JRadioButton();
        limitTimeOption = new JRadioButton();
        JLabel timeLimitLabel = new JLabel();
        timeLimitSpinner = new JSpinner();
        JLabel minutesLabel = new JLabel();
        limitCardsOption = new JRadioButton();
        JLabel cardLimitLabel = new JLabel();
        cardLimitSpinner = new JSpinner();
        JLabel cardsLabel = new JLabel();
        sessionTypeGroup = new ButtonGroup();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel2 ========
        {
            panel2.setBorder(new EmptyBorder(20, 20, 20, 20));
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{25, 55, 52, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 20, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- learnAllOption ----
            learnAllOption.setText(bundle.getString("LeitnerStrategyPanel.learnAllOption.text"));
            learnAllOption.setSelected(true);
            panel2.add(learnAllOption, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- limitTimeOption ----
            limitTimeOption.setText(bundle.getString("LeitnerStrategyPanel.limitTimeOption.text"));
            limitTimeOption.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    limitTimeOptionItemStateChanged();
                }
            });
            panel2.add(limitTimeOption, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- timeLimitLabel ----
            timeLimitLabel.setText(bundle.getString("LeitnerStrategyPanel.timeLimitLabel.text"));
            panel2.add(timeLimitLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- timeLimitSpinner ----
            timeLimitSpinner.setModel(new SpinnerNumberModel(10, 1, null, 1));
            timeLimitSpinner.setEnabled(false);
            panel2.add(timeLimitSpinner, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- minutesLabel ----
            minutesLabel.setText(bundle.getString("LeitnerStrategyPanel.minutesLabel.text"));
            panel2.add(minutesLabel, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- limitCardsOption ----
            limitCardsOption.setText(bundle.getString("LeitnerStrategyPanel.limitCardsOption.text"));
            limitCardsOption.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    limitCardsOptionItemStateChanged();
                }
            });
            panel2.add(limitCardsOption, new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

            //---- cardLimitLabel ----
            cardLimitLabel.setText(bundle.getString("LeitnerStrategyPanel.cardLimitLabel.text"));
            panel2.add(cardLimitLabel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- cardLimitSpinner ----
            cardLimitSpinner.setModel(new SpinnerNumberModel(10, 1, null, 1));
            cardLimitSpinner.setEnabled(false);
            panel2.add(cardLimitSpinner, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- cardsLabel ----
            cardsLabel.setText(bundle.getString("LeitnerStrategyPanel.cardsLabel.text"));
            panel2.add(cardsLabel, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
        }
        add(panel2, BorderLayout.CENTER);

        //---- sessionTypeGroup ----
        sessionTypeGroup = new ButtonGroup();
        sessionTypeGroup.add(learnAllOption);
        sessionTypeGroup.add(limitTimeOption);
        sessionTypeGroup.add(limitCardsOption);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        initializeStrategy();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel2;
    private JRadioButton learnAllOption;
    private JRadioButton limitTimeOption;
    private JSpinner timeLimitSpinner;
    private JRadioButton limitCardsOption;
    private JSpinner cardLimitSpinner;
    private ButtonGroup sessionTypeGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}