/**
 * ComparisonQueryElicitation.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 08/11/2007
 */
package jcolibri.extensions.recommendation.navigationByProposing.queryElicitation;

import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;

/**
 * Interface for query elicitation in Navigation by Proposing
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public interface ComparisonQueryElicitation {
    /**
     * Revises the query comparing the values of the user's selected case and
     * other proposed cases
     *
     * @param query
     *            to revise
     * @param selectedCase
     *            by the user
     * @param proposedCases
     *            to the user
     */
    public void reviseQuery(CBRQuery query, CBRCase selectedCase, Collection<CBRCase> proposedCases);
}
