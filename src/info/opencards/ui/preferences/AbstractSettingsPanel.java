package info.opencards.ui.preferences;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author Holger Brandl
 */
public abstract class AbstractSettingsPanel extends JPanel {


    private final List<SettingsPanelChangeListener> settingsPanelChangeListeners = new ArrayList<SettingsPanelChangeListener>();

    private boolean settingsChanged;


    AbstractSettingsPanel() {
    }


    /**
     * Adds a new listener.
     */
    public void addSettingsPanelChangeListener(SettingsPanelChangeListener l) {
        if (l == null)
            return;

        settingsPanelChangeListeners.add(l);
    }


    /**
     * Removes a listener.
     */
    public void removeSettingsPanelChangeListener(SettingsPanelChangeListener l) {
        if (l == null)
            return;

        settingsPanelChangeListeners.remove(l);
    }


    abstract void resetPanelSettings();


    abstract void applySettingsChanges();


    abstract protected void loadDefaults();


    public boolean isSettingsChanged() {
        for (SettingsPanelChangeListener settingsPanelChangeListener : settingsPanelChangeListeners) {
            settingsPanelChangeListener.settingsChanged(this);
        }

        return settingsChanged;
    }


    protected void setSettingsChanged(boolean settingsChanged) {
        this.settingsChanged = settingsChanged;
    }
}
