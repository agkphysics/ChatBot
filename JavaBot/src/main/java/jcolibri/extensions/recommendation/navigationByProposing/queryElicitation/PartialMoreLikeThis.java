/**
 * PartialMoreLikeThis.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 02/11/2007
 */
package jcolibri.extensions.recommendation.navigationByProposing.queryElicitation;

import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.util.AttributeUtils;

/**
 * Partially replaces current query with the description of the selected case.
 * It only transfers a feature value from the selected case if none of the
 * rejected cases have the same feature value.
 * <p>
 * See:
 * <p>
 * L. McGinty and B. Smyth. Comparison-based recommendation. In ECCBR'02:
 * Proceedings of the 6th European Conference on Advances in Case-Based
 * Reasoning, pages 575-589, London, UK, 2002. Springer-Verlag.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class PartialMoreLikeThis implements ComparisonQueryElicitation {
    /******************************************************************************/
    /** STATIC METHODS **/
    /******************************************************************************/

    /**
     * Partially replaces current query with the description of the selected
     * case.
     */
    public static void partialMoreLikeThis(CBRQuery query, CBRCase selectedCase, Collection<CBRCase> proposedCases) {
        boolean copyAttribute = true;
        for (Attribute at : AttributeUtils.getAttributes(selectedCase.getDescription())) {
            Object selectedValue = AttributeUtils.findValue(at, selectedCase);
            for (CBRCase c : proposedCases) {
                Object value = AttributeUtils.findValue(at, c);
                if (selectedValue == null) {
                    if (value == null) copyAttribute = false;
                } else copyAttribute = !selectedValue.equals(value);
                if (!copyAttribute) break;
            }
            if (copyAttribute) AttributeUtils.setValue(at, query, selectedValue);
        }

    }

    /******************************************************************************/
    /** OBJECT METHODS **/
    /******************************************************************************/

    /**
     * Partially replaces current query with the description of the selected
     * case.
     */
    @Override
    public void reviseQuery(CBRQuery query, CBRCase selectedCase, Collection<CBRCase> proposedCases) {
        partialMoreLikeThis(query, selectedCase, proposedCases);
    }
}
