/**
 * BuyOrQuit.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 24/10/2007
 */
package jcolibri.extensions.recommendation.conditionals;

import jcolibri.extensions.recommendation.casesDisplay.UserChoice;

/**
 * Conditional method that checks if the user choice is BUY the case.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 * @see jcolibri.extensions.recommendation.casesDisplay.UserChoice
 */
public class BuyOrQuit {
    /**
     * Returns true if the choice is a case.
     *
     * @param choice
     *            of the user
     */
    public static boolean buyOrQuit(UserChoice choice) {
        return choice.isBuy();
    }

}
