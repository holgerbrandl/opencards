package info.opencards.pptintegration.conversion;

import info.opencards.Utils;
import info.opencards.ui.actions.URLAction;
import info.opencards.util.AboutDialog;
import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Allows to import 2-column csv files into ppt-flashcard sets
 *
 * @author Holger Brandl
 */
public class ImportManager {


    public ImportManager(final Frame owner) {
        final String DEFAULT_DIR = "import.defdir";
        File defDir = new File(Utils.getPrefs().get(DEFAULT_DIR, System.getProperty("user.home")));
        JFileChooser importChooser = new JFileChooser(defDir);

        ImpSeparatorPanel sepPanel = new ImpSeparatorPanel(importChooser);

        importChooser.setDialogTitle(Utils.getRB().getString("cardimport.filechoose.title"));
        importChooser.setMultiSelectionEnabled(false);

        FileFilter csvFilter = new FileFilter() {

            public boolean accept(File f) {
                String fileName = f.getName();
                return (fileName.endsWith(".csv") || fileName.endsWith(".txt")) || f.isDirectory();
            }


            public String getDescription() {
                return "Text with tab separated Q/A (*.csv, *.txt)";
            }
        };

        importChooser.setFileFilter(csvFilter);
        int status = importChooser.showOpenDialog(null);
        if (status != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = importChooser.getSelectedFile();
        FileFilter selectedFilter = importChooser.getFileFilter();

        Utils.getPrefs().put(DEFAULT_DIR, selectedFile.getParentFile().getAbsolutePath());

        SlideShow slideShow = new SlideShow();

        if (selectedFilter == csvFilter) {
            Map<String, String> title2contents = readCsvFile(selectedFile, sepPanel.getCurSeparator());

            for (String slideTitle : title2contents.keySet()) {
                Slide slide = slideShow.createSlide();

                // create question shape
                TextBox title = slide.addTitle();
                title.setText(slideTitle);

                // create answer shape
                Shape titleShape = slide.getShapes()[0];

                TextBox txt = new TextBox();
                txt.setText(title2contents.get(slideTitle));
                Rectangle titleAnchor = titleShape.getAnchor();
                txt.setAnchor(new Rectangle((int) titleAnchor.getX(), (int) titleAnchor.getY() + 200, (int) titleAnchor.getWidth(), (int) titleAnchor.getHeight()));

//use RichTextRun to work with the text format
                RichTextRun titleFormat = ((AutoShape) titleShape).getTextRun().getRichTextRuns()[0];
                RichTextRun questionFormat = txt.getTextRun().getRichTextRuns()[0];
                questionFormat.setFontSize(titleFormat.getFontSize());
//                slide.getShapes()[0];
                questionFormat.setFontName(titleFormat.getFontName());
                questionFormat.setAlignment(TextBox.AlignCenter);

                slide.addShape(txt);
            }

        }

        // show the FAQ if nothing was imported. But do this only once to avoid users to become annoyed
        String SHOWN_IMPORT_HELP_BEFORE = "hasShownImportHelp";
        if (slideShow.getSlides().length < 2 && !Utils.getPrefs().getBoolean(SHOWN_IMPORT_HELP_BEFORE, false)) {
            Utils.getPrefs().putBoolean(SHOWN_IMPORT_HELP_BEFORE, true);
            new URLAction("nocardsimported", AboutDialog.OC_WEBSITE + "help").actionPerformed(null);
        } else {

            // save the sldeshow into a ppt
            final JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(selectedFile.getAbsolutePath() + ".ppt"));
            fc.setDialogTitle("Save imported flashcards as ");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            if (fc.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
                File saveFile = fc.getSelectedFile();

                try {
                    FileOutputStream out = new FileOutputStream(saveFile);
                    slideShow.write(out);
                    out.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }

        }
    }


    public static Map<String, String> readCsvFile(File selectedFile, String separatorString) {
        if (selectedFile.isDirectory())
            return null;

        try {

            Charset encoding = new SmartEncodingInputStream(new FileInputStream(selectedFile)).getEncoding();

            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), encoding.displayName()));

            Map<String, String> title2contents = new LinkedHashMap<String, String>();

            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] splitLine = line.split(separatorString);

                if (splitLine.length < 2)
                    continue;

                String title = splitLine[0].trim();
                String content = splitLine[1].trim();

                if (title.length() > 0 && content.length() > 0) {

                    // fix initial and final " if space separator has been used
                    if (separatorString.equals(ImpSeparatorPanel.SPACE_SEPARATOR)) {
                        title = title.replace("\"", "");
                        content = content.replace("\"", "");
                    }

                    // make sure that the card set does not already contains the key
                    while (title2contents.containsKey(title))
                        title = title + " ";

                    title2contents.put(title, content);
                }
            }

            fileReader.close();
            return title2contents;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String[] args) {
        new ImportManager(new Frame());
    }
}
