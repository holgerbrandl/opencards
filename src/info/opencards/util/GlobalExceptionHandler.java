package info.opencards.util;

import javax.swing.*;


/**
 * Handles all exception which occur in all current threads.
 *
 * @author Holger Brandl
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {


    public void uncaughtException(Thread t, Throwable e) {
        if (isOpenCardsError(e)) {
            ExceptionDialog exDialog = new ExceptionDialog(new JDialog());

            exDialog.showError(t, e);
            exDialog.setVisible(true);
        }
    }


    /**
     * Returns true if at least one opencards-class was involved into the problem.
     */
    private boolean isOpenCardsError(Throwable e) {
        for (StackTraceElement traceElement : e.getStackTrace()) {
            if (traceElement.toString().contains("opencards"))
                return true;
        }

        return false;
    }
}