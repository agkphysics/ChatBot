/**
 * BoundedGreedySelection.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 05/11/2007
 */
package jcolibri.method.retrieve.selection.diversity;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.selection.SelectCases;

/**
 * Tries to reduce the complexity of the greedy selection algorithm first
 * selecting the best b�k cases according to their similarity to the query and
 * then applies the greedy selection method to these cases.
 * <p>
 * See:
 * <p>
 * B. Smyth and P. McClave. Similarity vs. diversity. In ICCBR '01: Proceedings
 * of the 4th International Conference on Case-Based Reasoning, pages 347-361,
 * London, UK, 2001. Springer-Verlag.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class BoundedGreedySelection {

    /**
     * Executes the algorithm
     *
     * @param cases
     *            to retrieve from
     * @param query
     *            to compare
     * @param simConfig
     *            is the knn config that defines the k returned cases
     * @param bound
     *            to create the bounded retrieval set
     * @param k
     *            is the number of cases to select
     * @return k cases
     */
    public static Collection<CBRCase> boundedGreddySelection(Collection<RetrievalResult> cases, CBRQuery query,
            NNConfig simConfig, int k, int bound) {

        Collection<CBRCase> C = SelectCases.selectTopK(cases, bound * k);

        Collection<CBRCase> R = new ArrayList<>();
        for (int i = 0; (i < k) && (!C.isEmpty()); i++) {
            CBRCase best = GreedySelection.getMoreQuality(query, C, R, simConfig);
            R.add(best);
            C.remove(best);
        }
        return R;
    }
}
