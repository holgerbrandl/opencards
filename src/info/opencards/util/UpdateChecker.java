package info.opencards.util;

import info.opencards.Utils;
import info.opencards.ui.actions.URLAction;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class UpdateChecker {


    public static final String LAST_IGNORE_VERSION = "advncd.lastignoreversion";


    public static void main(String[] args) {
        Utils.resetAllSettings();
        check4Update(new JFrame());
    }


    public static void check4Update(final Frame owner) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String latestVersion = getLatestVersion();
                Utils.log("UpdateCheck: latest version is " + latestVersion);
                Utils.log("UpdateCheck: installed version " + AboutDialog.OPENCARDS_VERSION);

                boolean isUp2Date = isUpdate2Date(AboutDialog.OPENCARDS_VERSION, latestVersion);
                String lastIgnoreVersion = Utils.getPrefs().get(LAST_IGNORE_VERSION, "0.2");

                if (!isUp2Date && !latestVersion.equals(lastIgnoreVersion)) {
                    ResourceBundle rb = Utils.getRB();

                    Object[] options = {rb.getString("UpdateChecker.getLatest") + " v" + latestVersion, rb.getString("UpdateChecker.discardupdate")};
                    int status = JOptionPane.showOptionDialog(owner,
                            rb.getString("UpdateChecker.newocversion"),
                            rb.getString("UpdateChecker.updateinfo"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            new ScaleableIcon("icons/oclogo.png", 150, 139),     //do not use a custom Icon
                            options,  //the titles of buttons
                            options[0]); //default button title

                    if (status == JOptionPane.YES_OPTION) {
                        new URLAction(null, AboutDialog.OC_WEBSITE).actionPerformed(null);
                    } else {
                        Utils.getPrefs().put(LAST_IGNORE_VERSION, latestVersion);
                    }
                }
            }
        }.start();
    }


    public static String getLatestVersion() {
        try {
            URL yahoo = new URL("https://dl.dropboxusercontent.com/u/422074/opencards/oc_latest_version.txt");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(yahoo.openStream()));
            String latestVersion = bufferedReader.readLine().trim();

            bufferedReader.close();

            return latestVersion;
        } catch (Throwable t) {
            System.err.println("update check failed: " + t);
        }

        return AboutDialog.OPENCARDS_VERSION;
    }


    private static boolean isUpdate2Date(String currentVersion, String latestVersion) {
        String s1 = normalizeVersionString(currentVersion);
        String s2 = normalizeVersionString(latestVersion);

        return s1.compareTo(s2) >= 0;
    }


    public static String normalizeVersionString(String version) {
        List<Integer> vNumbers = new ArrayList<Integer>();
        for (String vNum : version.split("[.]")) {
            vNumbers.add(Integer.parseInt(vNum));
        }

        while (vNumbers.size() < 3) {
            vNumbers.add(0);
        }

        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("000");
        for (Integer vNumber : vNumbers) {
            sb.append(df.format(vNumber));
        }

        return sb.toString();
    }


    @Test
    public void testVersionComparison() {
        Assert.assertFalse(isUpdate2Date("2.1.13", "2.2"));
        Assert.assertFalse(isUpdate2Date("2", "2.0.1"));
        Assert.assertFalse(isUpdate2Date("2.0", "2.0.1"));

        Assert.assertTrue(isUpdate2Date("2.0.1", "2.0.1"));

        Assert.assertTrue(isUpdate2Date("2.2", "2.1.13"));
        Assert.assertTrue(isUpdate2Date("2.0.1", "2.0.0"));
    }
}
