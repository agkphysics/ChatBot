/**
 * ExecutionException.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.exception;

/**
 * Exception in the execution of a method.
 *
 * @author Juan A. Recio-Garc�a
 * @version 2.0
 */
public class ExecutionException extends java.lang.Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>ExecutionException</code> without a detail message.
     */
    public ExecutionException() {}

    /**
     * Creates a new <code>ExecutionException</code> with the specified detail
     * message.
     *
     * @param msg
     *            description message.
     */
    public ExecutionException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>ExecutionException</code> with the specified
     * detail message.
     *
     * @param th
     *            cause of the exception.
     */
    public ExecutionException(Throwable th) {
        super(th);
    }

}
