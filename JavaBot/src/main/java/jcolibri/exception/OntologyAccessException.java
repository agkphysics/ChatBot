/**
 * OntologyAccessException.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/01/2007
 */
package jcolibri.exception;

/**
 * Error accessing an ontology (using OntoBridge).
 *
 * @author Juan A. Recio-Garc�a
 * @version 2.0
 */
public class OntologyAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    public OntologyAccessException(Exception ex) {
        super(ex);
    }

    public OntologyAccessException(String msg) {
        super(msg);
    }
}
