package jcolibri.method.maintenance;

import java.util.*;

import jcolibri.cbrcore.CBRCase;

/**
 * Stores the case result information. It contains a <case, result> pair. The
 * result is some double value related to the case.
 *
 * @author Lisa Cummins
 */
public class CaseResult extends QueryResult implements Comparable<Object> {
    /**
     * Sorts the given list of CaseResults in the given order and returns the
     * sorted list.
     *
     * @param ascending
     *            The order in which to sort the elements.
     * @param toSort
     *            The list of CaseResults to sort.
     * @return the sorted list.
     */
    public static List<CaseResult> sortCaseResults(boolean ascending, List<CaseResult> toSort) {
        Collections.sort(toSort);
        if (ascending) {
            return toSort;
        }
        List<CaseResult> sorted = new LinkedList<>();
        for (CaseResult res : toSort) {
            sorted.add(0, res);
        }
        return sorted;
    }

    /**
     * Sets up a <case, result> pair.
     *
     * @param _case
     *            The case to be stored
     * @param result
     *            The result associated with this case.
     */
    public CaseResult(CBRCase _case, double result) {
        super(_case, result);
    }

    /**
     * Returns the case.
     *
     * @return the case.
     */
    @Override
    public CBRCase getCase() {
        return (CBRCase)_case;
    }
}
