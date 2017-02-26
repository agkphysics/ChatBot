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
public class NoApplicableSimilarityFunctionException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoApplicableSimilarityFunctionException(Class<?> sf, Class<?> c) {
        super("SimilarityFunction: " + sf + " not applicable to type: " + c);
    }

    public NoApplicableSimilarityFunctionException(Exception ex) {
        super(ex);
    }

    public NoApplicableSimilarityFunctionException(String msg) {
        super(msg);
    }
}
