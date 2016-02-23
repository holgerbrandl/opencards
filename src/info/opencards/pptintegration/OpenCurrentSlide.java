package info.opencards.pptintegration;

import info.opencards.OpenCards;
import info.opencards.Utils;
import info.opencards.core.CardFile;
import org.apache.poi.sl.usermodel.Slide;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class OpenCurrentSlide {


    static void showCurrentSlideInPPTEditor(final CardFile curCardFile, final Slide slide) {
        String slideOpenCounter = "slide.open.counter";
        Utils.getPrefs().putInt(slideOpenCounter, Utils.getPrefs().getInt(slideOpenCounter, 0) + 1);

        if (Utils.getPrefs().getInt(slideOpenCounter, 1) == 1) {
            JOptionPane.showMessageDialog(OpenCards.getInstance(),
                    "OpenCards will now try to open the current slide in your system's ppt-editor software.\n" +
                            "Changes to the presentation will be automatically picked up in the next learning session.",
                    "Open current slide in ppt-editor", JOptionPane.INFORMATION_MESSAGE);
        }

        new Thread() {
            @Override
            public void run() {

                try {
                    if (Utils.isMacOSX()) {
                        Runtime runtime = Runtime.getRuntime();
                        String aliasPath = curCardFile.getFileLocation().getAbsolutePath().replaceAll("/", ":");
                        aliasPath = aliasPath.substring(1, aliasPath.length());

                        String gotoSlide =
                                "tell application \"Microsoft PowerPoint\"\n" +
                                        "   activate\n" +
                                        "   open alias \"" + aliasPath + "\"\n" +
                                        "   set theView to view of document window 1\n" +
                                        "   go to slide theView number " + slide.getSlideNumber() + "\n" +
                                        "end tell";
                        String[] args = {"osascript", "-e", gotoSlide};

                        runtime.exec(args);


                    } else if (Utils.isWindowsPlatform()) {
                        Runtime runtime = Runtime.getRuntime();
                        String aliasPath = curCardFile.getFileLocation().getAbsolutePath().replaceAll("/", ":");
                        aliasPath = aliasPath.substring(1, aliasPath.length());

                        File tempVBSfile = File.createTempFile("showSlide", ".vbs");
                        BufferedWriter bw = new BufferedWriter(new FileWriter(tempVBSfile));

                        Desktop.getDesktop().open(curCardFile.getFileLocation());
//                        http://www.ehow.com/how_7245511_run-vbs-script-java.html
//                        http://www.ehow.com/how_8360266_run-powerpoint-command-line.html
//                        http://www.ozgrid.com/forum/showthread.php?t=75180
//                           http://support.microsoft.com/kb/163301
//                           http://www.dotnetfunda.com/Blogs/Naimishforu/1113/hide-power-point-application-window-in-net-office-automation
//                        bw.write("set shell = CreateObject(\"Shell.Application\")\n" +
//                                "\n" +
//                                "shell.Open \"http://www.ehow.com\"");


//                        String gotoSlideVBS =
//                                "Set objApp = CreateObject(\"PowerPoint.Application\") \n" +
//                                        "   If Err = ERR_APP_NOTFOUND Then \n" +
//                                        "            MsgBox \"Power Point isn't installed on this computer. Could not launch PowerPoint.\" \n" +
//                                        "            WScript.Quit\n" +
//                                        "        End If\n\n" +
//                                        "   With objApp \n" +
//                                        "            .Activate \n" +
//                                        "            .Presentations.Open \"" + curCardFile + "\"\n" +
//                                        "            .ActivePresentation.SlideShowWindow.View.GotoSlide " + slide.getSlideNumber();
////                                        "            .ActivePresentation.Slides(Y).Select "

//                           String openAndSelectSlide = "For Each oPresObject In PowerPoint.Presentations\n" +
//                                   "'\tIf (StrComp(oPresObject.Name, \"fruits.ppt\", vbTextCompare) = 0) Then\n" +
//                                   "'\t\toPresObject.Windows(1).Activate\n" +
//                                   "'\t\toPresObject.Slides(9).Select\n" +
//                                   "'\t\tExit For\n" +
//                                   " '   End If\n" +
//                                   "Next oPresObject";

                        String gotoSlideVBS = "Set objPPT = CreateObject(\"PowerPoint.Application\") \n" +
                                "With objPPT \n" +
                                "\t.Activate \n" +
                                "\t.ActivePresentation.Slides(" + slide.getSlideNumber() + ").Select\n" +
                                "End With";

                        bw.write(gotoSlideVBS);
                        bw.flush();
                        bw.close();


                        String[] args = {"wscript", tempVBSfile.getAbsolutePath()};
                        runtime.exec(args);
                        tempVBSfile.deleteOnExit();

                    } else {
                        try {
                            Desktop.getDesktop().open(curCardFile.getFileLocation());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Throwable t) {
                    //just open the file as a fallback
                    try {
                        System.err.println("slide opening failed");
                        Desktop.getDesktop().open(curCardFile.getFileLocation());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
