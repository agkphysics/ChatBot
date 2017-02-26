/**
 * BoundedRandomSelection.java jCOLIBRI2 framework.
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
import jcolibri.method.retrieve.selection.SelectCases;

/**
 * Is the simplest diversity strategy: select the k cases at random from a
 * larger set of the b�k most similar cases to the query.
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
 */
public class BoundedRandomSelection {

    /**
     * Executes the algorithm.
     */
    public static Collection<CBRCase> boundedRandomSelection(Collection<RetrievalResult> cases, CBRQuery query, int k,
            int bound) {
        Collection<CBRCase> nn = SelectCases.selectTopK(cases, k * bound);

        Collection<CBRCase> res = new ArrayList<>();

        for (int i = 0; (i < k) && (i < nn.size()); i++) {
            int chosen = (int)(Math.random() * nn.size());
            CBRCase rr = (CBRCase)nn.toArray()[chosen];
            res.add(rr);
            nn.remove(rr);
        }

        return res;
    }
}
