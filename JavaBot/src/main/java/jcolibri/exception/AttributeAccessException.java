/**
 * AttributeAccessException.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.exception;

/**
 * Exception for attribute access errors.
 *
 * @author Juan A. Recio-Garc�a
 * @version 2.0
 */
public class AttributeAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    public AttributeAccessException(Exception ex) {
        super(ex);
    }

    public AttributeAccessException(String msg) {
        super(msg);
    }
}
