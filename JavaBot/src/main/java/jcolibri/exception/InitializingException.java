/**
 * InitializingException.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.exception;

/**
 * Exception in the initialization of an object.
 *
 * @author Juan A. Recio-Garc�a
 * @version 2.0
 */
public class InitializingException extends ExecutionException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>InitializingException</code> without detail message.
     */
    public InitializingException() {}

    /**
     * Creates a new <code>InitializingException</code> with the specified
     * detail message.
     *
     * @param msg
     *            detail message.
     */
    public InitializingException(String msg) {
        super(msg);
    }

    /**
     * Creates a new <code>InitializingException</code> with the specified
     * detail message.
     *
     * @param th
     *            cause of the exception.
     */
    public InitializingException(Throwable th) {
        super(th);
    }
}
