/**
 * SimilarityInfluence.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 30/10/2007
 */
package jcolibri.extensions.recommendation.navigationByAsking;

import java.util.*;

import jcolibri.cbrcore.*;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.util.*;

/**
 * Selects the attribute that has the highest inf uence on the KNN similarity.
 * The in uence on the similarity can be measured by the expected variance of
 * the similarities of a set of selected cases.
 *
 * This method is not recommended with large case bases.
 *
 * <p>
 * See:
 * <p>
 * R. Bergmann. Experience Management: Foundations, Development Methodology, and
 * Internet-Based Applications. Springer-Verlag New York, Inc.,Secaucus, NJ,
 * USA, 2002.
 * <p>
 * A. Kohlmaier, S. Schmitt, and R. Bergmann. A similarity-based approach to
 * attribute selection in user-adaptive sales dialogs. In D. W. Aha and I.
 * Watson, editors, Proceedings of the 4th International Conference on
 * Case-Based Reasoning, pages 306320, Seattle, Washington, 2001.
 * Springer-Verlag.
 * <p>
 * S. Schmitt, P. Dopichaj, and P. Dom�nguez-Mar�n. Entropy-based vs.
 * similarity-in uenced: Attribute selection methods for dialogs tested on
 * different electronic commerce domains. In S. Craw and A. Preece, editors,
 * Proceedings of the 6th European Conference on Case-Based Reasoning, pages
 * 380-394, Aberdeen, Scotland, 2002. Springer-Verlag.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class SimilarityInfluence implements SelectAttributeMethod {
    private static ArrayList<Attribute> asked;

    /******************************************************************************/
    /** STATIC METHODS **/
    /******************************************************************************/

    /** KNN configuration */
    private NNConfig simConfig;

    /**
     * Selects the attribute with more expected influence in the NN scoring.
     *
     * @param cases
     *            Set of working cases
     * @param query
     *            Query to compare with the cases
     * @param simConfig
     *            is the NN similiarity configuration
     * @param init
     *            indicates if this is the first time that the algorithm is
     *            executed. This way, in following iterations past chosen
     *            attributes are not computed.
     * @return the selected attribute or null if there are not more attributes
     *         to ask.
     */
    public static Attribute getMoreSimVarAttribute(Collection<CBRCase> cases, CBRQuery query, NNConfig simConfig,
            boolean init) throws ExecutionException {
        if (init) asked = new ArrayList<>();
        if (asked == null) throw new ExecutionException("Similarity Influence method must be initialized each cycle");
        CBRCase acase = cases.iterator().next();
        Collection<Attribute> atts = AttributeUtils.getAttributes(acase.getDescription());
        atts.remove(acase.getDescription().getIdAttribute());

        atts.removeAll(asked);
        if (atts.isEmpty()) {
            asked = new ArrayList<>();
            atts = AttributeUtils.getAttributes(acase.getDescription());
        }

        ProgressController.init(SimilarityInfluence.class, "Similarity Influence selection",
                ProgressController.UNKNOWN_STEPS);
        System.out.println("Computing SimVar for " + cases.size() + " cases");

        double maxSimVar = 0;
        Attribute maxSimVaratt = null;
        for (Attribute a : atts) {
            double simVar = computeSimVar(a, cases, query, simConfig);
            System.out.println("SimVar(" + a.getName() + ") = " + simVar);
            if (simVar > maxSimVar) {
                maxSimVar = simVar;
                maxSimVaratt = a;
            }
        }

        ProgressController.finish(SimilarityInfluence.class);

        asked.add(maxSimVaratt);
        return maxSimVaratt;
    }

    /**
     * Computes the simVar of an attribute with respect to a set of cases and a
     * query.
     */
    private static double computeSimVar(Attribute a, Collection<CBRCase> cases, CBRQuery query, NNConfig simConfig) {
        double Csize = cases.size();

        Hashtable<Object, HashSet<CBRCase>> clases = new Hashtable<>();
        for (CBRCase c : cases) {
            Object value = AttributeUtils.findValue(a, c.getDescription());
            HashSet<CBRCase> set = clases.get(value);
            if (set == null) {
                set = new HashSet<>();
                clases.put(value, set);
            }
            set.add(c);
        }

        double simVar = 0;
        for (Object v : clases.keySet()) {
            double pv = (clases.get(v).size()) / Csize;

            CBRQuery newQuery = new CBRQuery();
            newQuery.setDescription(CopyUtils.copyCaseComponent(query.getDescription()));
            AttributeUtils.setValue(a, newQuery, v);
            double var = computeVar(newQuery, cases, simConfig);
            simVar += (pv * var);
            ProgressController.step(SimilarityInfluence.class);

        }

        return simVar;
    }

    /******************************************************************************/
    /** OBJECT METHODS **/
    /******************************************************************************/

    /**
     * Computes the Var formulae
     */
    private static double computeVar(CBRQuery query, Collection<CBRCase> cases, NNConfig simConfig) {
        Collection<RetrievalResult> sim = NNScoringMethod.evaluateSimilarity(cases, query, simConfig);

        double niu = 0;
        for (RetrievalResult rr : sim)
            niu += rr.getEval();
        niu = niu / sim.size();

        double res = 0;
        for (RetrievalResult rr : sim)
            res += ((rr.getEval() - niu) * (rr.getEval() - niu));

        return res / (cases.size());
    }

    /**
     * Constructor
     *
     * @param simConfig
     *            is the KNN configuration
     */
    public SimilarityInfluence(NNConfig simConfig) {
        this.simConfig = simConfig;
    }

    /**
     * Selects the attribute to be asked
     *
     * @param cases
     *            list of working cases
     * @param query
     *            is the current query
     * @return selected attribute
     * @throws ExecutionException
     */
    @Override
    public Attribute getAttribute(Collection<CBRCase> cases, CBRQuery query) throws ExecutionException {
        return getMoreSimVarAttribute(cases, query, simConfig, false);
    }
}
