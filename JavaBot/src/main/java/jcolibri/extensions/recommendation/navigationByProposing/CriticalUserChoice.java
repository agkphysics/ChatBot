/**
 * CriticalUserChoice.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 31/10/2007
 */
package jcolibri.extensions.recommendation.navigationByProposing;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.extensions.recommendation.casesDisplay.UserChoice;
import jcolibri.method.retrieve.FilterBasedRetrieval.FilterConfig;

/**
 * Extends the UserChoice object to store critiques (CritiqueOptions) about the
 * selected case (that in the next converstaion cycle is the query).
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 * @see jcolibri.extensions.recommendation.navigationByProposing.CriticalUserChoice
 */
public class CriticalUserChoice extends UserChoice {

    private Collection<CritiqueOption> critiques = null;

    public CriticalUserChoice() {
        super(UserChoice.REFINE_QUERY, null);
        critiques = new ArrayList<>();
    }

    /**
     * Creates an object with the user choice, critiques
     *
     * @param choice
     *            contains a value from UserChoice: QUIT, BUY or REFINE_QUERY.
     *            If the value is REFINE_QUERY, the critiquedCase parameter
     *            contains the new query to edit.
     * @param critiques
     *            to the new query
     * @param critiquedCase
     *            is the new query/case thas was critiqued. This parameter is
     *            only used if the "choice" parameter contains REFINE_QUERY.
     */
    public CriticalUserChoice(int choice, Collection<CritiqueOption> critiques, CBRCase critiquedCase) {
        super(choice, critiquedCase);
        this.critiques = critiques;
    }

    /**
     * Returns the critiques
     */
    public Collection<CritiqueOption> getCritiques() {
        return critiques;
    }

    /**
     * Gets the FilterConfig object used by the FilterBasedRetrieval method that
     * is obtained from the critiques.
     */
    public FilterConfig getFilterConfig() {
        FilterConfig fc = new FilterConfig();
        for (CritiqueOption critique : critiques)
            fc.addPredicate(critique.getAttribute(), critique.getPredicate());
        return fc;
    }

}
