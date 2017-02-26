/**
 * RemoveRetrievalEvaluation.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 30/05/2007
 */
package jcolibri.method.retrieve;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.CBRCase;

/**
 * Removes the evaluation value from the <case,evaluation> pair of the
 * Collection<RetrievalResult> and returns a Collection<CBRCase> list.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public class RemoveRetrievalEvaluation {

    /**
     * Removes the evaluation value from the <case,evaluation> pair of the
     * Collection<RetrievalResult> and returns a Collection<CBRCase> list.
     */
    public static Collection<CBRCase> removeRetrievalEvaluation(Collection<RetrievalResult> rrList) {
        Collection<CBRCase> res = new ArrayList<>();
        for (RetrievalResult rr : rrList)
            res.add(rr.get_case());
        return res;
    }
}
