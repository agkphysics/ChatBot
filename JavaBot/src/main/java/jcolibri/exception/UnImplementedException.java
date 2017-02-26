/**
 * UnImplementedException.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/01/2007
 */
package jcolibri.exception;

/**
 * Exception thrown when something is not implemented yet.
 *
 * @author Juan A. Recio-Garc�a
 * @version 2.0
 */
public class UnImplementedException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnImplementedException(Exception ex) {
        super(ex);
    }

    public UnImplementedException(String msg) {
        super(msg);
    }
}
