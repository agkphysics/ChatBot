/**
 * ClassificationSolution.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 04/08/2007
 */
package jcolibri.extensions.classification;

import jcolibri.cbrcore.CaseComponent;

/**
 * Defines a common interface for the solution CaseComponents of a case that
 * will be classified.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 *
 */
public interface ClassificationSolution extends CaseComponent {
    /**
     * Returns the class of the solution (case).
     */
    public Object getClassification();
}
