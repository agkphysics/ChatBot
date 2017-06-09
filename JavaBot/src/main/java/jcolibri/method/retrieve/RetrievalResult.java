/**
 * RetrievalResult.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.method.retrieve;

import jcolibri.cbrcore.CBRCase;

/**
 * Stores the retrieval information. It contais a <case, evaluation> pair.
 *
 * @author Juan A. Recio-Garcia
 * @version 2.0
 */
public class RetrievalResult implements Comparable<Object> {

    /** Constant used to retrieve all the cases in the retrieval methods. */
    public static final int RETRIEVE_ALL = Integer.MAX_VALUE;

    private CBRCase _case;
    private double eval;

    /**
     * Constructor
     *
     * @param _case
     *            retrieved
     * @param eval
     *            is the similiarty with the query
     */
    public RetrievalResult(CBRCase _case, Double eval) {
        this._case = _case;
        this.eval = eval;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof RetrievalResult)) return 0;
        RetrievalResult other = (RetrievalResult)o;
        if (other.getEval() < eval) return -1;
        else if (other.getEval() > eval) return 1;
        else return 0;
    }

    /**
     * @return Returns the _case.
     */
    public CBRCase get_case() {
        return _case;
    }

    /**
     * @return Returns the eval.
     */
    public double getEval() {
        return eval;
    }

    /**
     * @param _case
     *            The _case to set.
     */
    public void set_case(CBRCase _case) {
        this._case = _case;
    }

    /**
     * @param eval
     *            The eval to set.
     */
    public void setEval(double eval) {
        this.eval = eval;
    }

    @Override
    public String toString() {
        return _case + " -> " + eval;
    }
}
