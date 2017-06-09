/**
 * WeightedMoreLikeThis.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 02/11/2007
 */
package jcolibri.extensions.recommendation.navigationByProposing.queryElicitation;

import java.util.Collection;
import java.util.HashSet;

import jcolibri.cbrcore.*;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.util.AttributeUtils;

/**
 * The WeightedMoreLikeThis transfers all attributes from the selected case to
 * the query but weights them given preference to diverse attributes among the
 * proposed cases. The new weights are stored into a NNConfig object, so this
 * strategy should be used with NN retrieval.
 * <p>
 * See:
 * <p>
 * L. McGinty and B. Smyth. Comparison-based recommendation. In ECCBR'02:
 * Proceedings of the 6th European Conference on Advances in Case-Based
 * Reasoning, pages 575-589, London, UK, 2002. Springer-Verlag.
 *
 * @see jcolibri.method.retrieve.NNretrieval.NNConfig
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class WeightedMoreLikeThis implements ComparisonQueryElicitation {
    /******************************************************************************/
    /** STATIC METHODS **/
    /******************************************************************************/

    /******************************************************************************/
    /** OBJECT METHODS **/
    /******************************************************************************/

    private NNConfig _simConfig;

    /**
     * Replaces current query with the description of the selected case but
     * weighting the attributes given preference to diverse attributes among the
     * proposed cases.
     */
    public static void weightedMoreLikeThis(CBRQuery query, CBRCase selectedCase, Collection<CBRCase> proposedCases,
            NNConfig simConfig) {
        for (Attribute at : AttributeUtils.getAttributes(selectedCase.getDescription())) {
            Object selectedValue = AttributeUtils.findValue(at, selectedCase);
            HashSet<Object> alternatives = new HashSet<>();
            for (CBRCase c : proposedCases) {
                Object value = AttributeUtils.findValue(at, c);
                if (selectedValue == null) {
                    if (value != null) alternatives.add(value);
                } else if (selectedValue.equals(value)) alternatives.add(value);
            }
            AttributeUtils.setValue(at, query, selectedValue);
            simConfig.setWeight(at, ((double)alternatives.size()) / ((double)proposedCases.size()));

        }

    }

    public WeightedMoreLikeThis(NNConfig simConfig) {
        _simConfig = simConfig;
    }

    /**
     * Replaces current query with the description of the selected case but
     * weighting the attributes given preference to diverse attributes among the
     * proposed cases.
     */
    @Override
    public void reviseQuery(CBRQuery query, CBRCase selectedCase, Collection<CBRCase> proposedCases) {
        weightedMoreLikeThis(query, selectedCase, proposedCases, _simConfig);
    }
}
