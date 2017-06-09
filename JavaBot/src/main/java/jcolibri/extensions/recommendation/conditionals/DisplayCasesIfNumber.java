/**
 * DisplayCasesIfNumber.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 28/10/2007
 */
package jcolibri.extensions.recommendation.conditionals;

import java.util.Collection;

import javax.swing.JOptionPane;

import jcolibri.cbrcore.CBRCase;

/**
 * Conditional method that returns true if the number of recieved cases is
 * inside a range.<br>
 * An optional value sets if a message should be displayed when the number of
 * cases is not in the range.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class DisplayCasesIfNumber {
    /**
     * Checks if the number of cases is inside a range.
     *
     * @param max
     *            allowed cases
     * @param min
     *            allowed cases
     * @param cases
     *            received cases
     * @param showMessage
     *            indicates if messages should be shown.
     * @return true if the number of cases is in range.
     */
    public static boolean displayCases(int max, int min, Collection<CBRCase> cases, boolean showMessage) {
        int size = cases.size();

        if (size < min) {
            JOptionPane.showMessageDialog(null,
                    "There are not enough cases that correspond with the query. Please use a more general query.",
                    "Not cases found", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (size > max) {
            if (showMessage) JOptionPane.showMessageDialog(null,
                    "There are too many cases that correspond with the query. Please use a more specific query.",
                    "Too many cases found", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;

    }

    /**
     * Checks if the number of cases is inside a range. It shows a message if
     * the number of cases is not in range.
     *
     * @param max
     *            allowed cases
     * @param min
     *            allowed cases
     * @param cases
     *            received cases
     * @return true if the number of cases is in range.
     */
    public static boolean displayCasesWithMessage(int max, int min, Collection<CBRCase> cases) {
        return displayCases(max, min, cases, true);
    }

    /**
     * Checks if the number of cases is inside a range. It does not show any
     * message if the number of cases is not in range.
     *
     * @param max
     *            allowed cases
     * @param min
     *            allowed cases
     * @param cases
     *            received cases
     * @return true if the number of cases is in range.
     */
    public static boolean displayCasesWithoutMessage(int max, int min, Collection<CBRCase> cases) {
        return displayCases(max, min, cases, false);
    }
}
