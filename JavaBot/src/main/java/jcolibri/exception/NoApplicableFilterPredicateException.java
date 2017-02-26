/**
 * NoApplicableSiliarityFunctionException.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.exception;

/**
 * Trying to apply a similarity function to an incompatible attribute
 *
 * @author Juan A. Recio-Garc�a
 * @version 2.0
 */
public class NoApplicableFilterPredicateException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoApplicableFilterPredicateException(Class<?> predicate, Class<?> c) {
        super("Filter predicate: " + predicate + " not applicable to type: " + c);
    }

    public NoApplicableFilterPredicateException(Exception ex) {
        super(ex);
    }

    public NoApplicableFilterPredicateException(String msg) {
        super(msg);
    }
}
