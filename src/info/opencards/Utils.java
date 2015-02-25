package info.opencards;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * Some global static utility methods which are used here, there and everywhere in OpenCards codebase.
 *
 * @author Holger Brandl
 */
public class Utils {


    public static final String PROP_STARTUP_COUNTER = "general.startup.counter";
    private static Random r;


    public static File getConfigDir() {
        File userHome = getUserHome();

        return getOrCreateHiddenOCDirectory(userHome);
    }


    public static File getUserHome() {
        return new File(System.getProperty("user.home"));
    }


    public static File getOrCreateHiddenOCDirectory(File directory) {
        if (!directory.isDirectory()) {
            return null;
        }

        File metadataDirectory = new File(directory, ".opencards");

        if (!metadataDirectory.isDirectory()) {
            metadataDirectory.mkdir();


            // add a file which describe the purpose of the opencards directory
            final URL url = Utils.loadResource("ReadMe.txt");
            try {
                copyStreamIntoFile(new File(metadataDirectory, "ReadMe.txt"), url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Utils.isWindowsPlatform()) {
                try {
                    Runtime.getRuntime().exec("attrib +H '" + metadataDirectory.getAbsolutePath() + "'");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return metadataDirectory;
    }


    /**
     * Returns a random generator, which is also globally accessible in order to allow a determinstic behavior for
     * testing purposes.
     */
    public synchronized static Random getRandGen() {
        if (r == null)
            r = new Random(System.currentTimeMillis());

        return r;
    }


    public synchronized static Preferences getPrefs() {
        return Preferences.userNodeForPackage(Utils.class);
    }


    public static void flushPreferences() {
        try {
            getPrefs().flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }


    public static void resetAllSettings() {
        try {
            getPrefs().clear();
            getPrefs().flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the global translation bundle of OpenCards.
     */
    public static ResourceBundle getRB() {
        // note: to start OC with another local just add the following vm properties: -Duser.language=de -Duser.country=DE
        Locale locale = Locale.getDefault();

        return ResourceBundle.getBundle("info.opencards.translation", locale);
    }


    public static void log(String msg) {
        log(Level.INFO, msg);
    }


    public static void log(Level level, String msg) {
        Logger.getLogger("opencards").log(level, msg);
    }


    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name");
        return os != null && os.startsWith("Windows");
    }


    public static boolean isMacOSX() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Mac OS X");
    }


    public static boolean isLinux() {
        return !isWindowsPlatform() & !isMacOSX();
    }


    public static File createTempCopy(File file) {
        // create a random file-name
        try {
            assert file.isFile() : "File to be copied is not a regular one";
            File tempFile = File.createTempFile(file.getName().replace(".ppt", ""), ".ppt");

            copyFile(file, tempFile);

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static File copyFile(File inFile, File outFile) {
        try {
            assert inFile.isFile();
            assert outFile.getParentFile().isDirectory();

            InputStream is = new FileInputStream(inFile);
            copyStreamIntoFile(outFile, is);

            return outFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void copyStreamIntoFile(File outFile, InputStream is) throws IOException {
        OutputStream os = new FileOutputStream(outFile);

        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }

        is.close();
        os.close();
    }


    /**
     * Collects all files list which match ALL given filters.
     *
     * @param directory the base directory for the search
     * @param suffix
     * @return The list of all odp-files found in one of the subdirectories of <code>directory</code>.
     */
    public static java.util.List<File> findFiles(File directory, String suffix) {
        assert directory.isDirectory();

        java.util.List<File> allFiles = new ArrayList<File>();

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                allFiles.addAll(findFiles(file, suffix));
            } else {
                if (file.isFile() && file.getName().endsWith(suffix))
                    allFiles.add(file);
            }
        }

        Collections.sort(allFiles);
        return allFiles;
    }


    public static boolean isAllFalse(Collection<Boolean> booleans) {
        for (Boolean aBoolean : booleans) {
            if (aBoolean)
                return false;
        }

        return true;
    }


    public static void sleep(final int timeMs) {
        new Thread() {

            public void run() {
                try {
                    sleep(timeMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }


    public static URL loadResource(String resourceName) {
        if (Utils.class.getClass().getClassLoader() != null) {
            return Utils.class.getClass().getClassLoader().getResource(resourceName);
        } else {
            return Utils.class.getClassLoader().getResource("info/opencards/" + resourceName);
        }
    }


    public static void main(String[] args) {
//        try {
//            for (String s : getPrefs().keys()) {
//                System.out.println("pref is : " + s + " and its value is " + getPrefs().get(s, "foobar"));
//            }
//        } catch (BackingStoreException e) {
//            e.printStackTrace();
//        }

        resetAllSettings();
//        flushPreferences();
        OpenCards.main(null);

        // set pref for testing
//        getPrefs().putDouble("lalelu", 56);
    }


}

