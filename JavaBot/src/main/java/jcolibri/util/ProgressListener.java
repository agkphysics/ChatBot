/**
 * ProgressListener.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/01/2007
 */
package jcolibri.util;

/**
 * Interface implemented by the listeners of a progress. The implementations of
 * this interface must register in the ProgressController to recieve the
 * progress.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 * @see jcolibri.util.ProgressController
 */
public interface ProgressListener {

    /**
     * Process finished.
     */
    public void finish();

    /**
     * Method call when a progress begins.
     *
     * @param info
     *            Some textual info
     * @param numberOfSteps
     *            Estimated number of steps (-1 if unknown).
     */
    public void init(String info, int numberOfSteps);

    /**
     * An step executed.
     */
    public void step();
}
