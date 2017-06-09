package jcolibri.method.maintenance;

import java.util.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.exception.InitializingException;
import jcolibri.method.reuse.classification.KNNClassificationConfig;

/**
 * Computes the competence model for a given case base.
 *
 * @author Lisa Cummins
 * @author Derek Bridge 22/05/07
 */
public class CompetenceModel {

    private Map<CBRCase, Collection<CBRCase>> coverageSets;

    private Map<CBRCase, Collection<CBRCase>> reachabilitySets;

    private Map<CBRCase, Collection<CBRCase>> liabilitySets;

    /**
     * Computes the competence model for the given cases using the given solves
     * function.
     *
     * @param solves
     *            the function to use to find which cases solve a query case.
     * @param knnConfig
     * @param cases
     *            the cases for which the competence model is being computed.
     */
    public void computeCompetenceModel(SolvesFunction solves, KNNClassificationConfig knnConfig,
            Collection<CBRCase> cases) {
        coverageSets = new HashMap<>();
        reachabilitySets = new HashMap<>();
        liabilitySets = new HashMap<>();

        for (CBRCase q : cases) {
            solves.setCasesThatSolveAndMisclassifyQ(q, cases, knnConfig);
            Collection<CBRCase> solveQ = solves.getCasesThatSolvedQuery();
            Collection<CBRCase> misclassifyQ = solves.getCasesThatMisclassifiedQuery();
            Collection<CBRCase> reachabilitySet = new LinkedList<>();
            if (solveQ != null) {
                for (CBRCase c : solveQ) {
                    reachabilitySet.add(c);
                    Collection<CBRCase> coverageSet = coverageSets.get(c);
                    if (coverageSet == null) {
                        coverageSet = new LinkedList<>();
                    }
                    coverageSet.add(q);
                    coverageSets.put(c, coverageSet);
                }
                reachabilitySets.put(q, reachabilitySet);
            }

            if (misclassifyQ != null) {
                for (CBRCase c : misclassifyQ) {
                    Collection<CBRCase> liabilitySet = liabilitySets.get(c);
                    if (liabilitySet == null) {
                        liabilitySet = new LinkedList<>();
                    }
                    liabilitySet.add(q);
                    liabilitySets.put(c, liabilitySet);
                }
            }
        }
    }

    /**
     * Returns the coverage set of the given case.
     *
     * @param c
     *            the case whose coverage set is being retrieved.
     * @return the coverage set of c.
     * @throws InitializingException
     *             Indicates that the competence model has not yet been
     *             computed.
     */
    public Collection<CBRCase> getCoverageSet(CBRCase c) throws InitializingException {
        if (coverageSets == null) throw new InitializingException();
        return coverageSets.get(c);
    }

    /**
     * Returns the coverage sets of the case base.
     *
     * @return the coverage sets of the case base.
     */
    public Map<CBRCase, Collection<CBRCase>> getCoverageSets() {
        return coverageSets;
    }

    /**
     * Returns the liability set of the given case.
     *
     * @param c
     *            the case whose liability set is being retrieved.
     * @return the liability set of c.
     * @throws InitializingException
     *             Indicates that the competence model has not yet been
     *             computed.
     */
    public Collection<CBRCase> getLiabilitySet(CBRCase c) throws InitializingException {
        if (liabilitySets == null) throw new InitializingException();
        return liabilitySets.get(c);
    }

    /**
     * Returns the liability sets of the case base.
     *
     * @return the liability sets of the case base.
     */
    public Map<CBRCase, Collection<CBRCase>> getLiabilitySets() {
        return liabilitySets;
    }

    /**
     * Returns the reachability set of the given case.
     *
     * @param c
     *            the case whose reachability set is being retrieved.
     * @return the reachability set of c.
     * @throws InitializingException
     *             Indicates that the competence model has not yet been
     *             computed.
     */
    public Collection<CBRCase> getReachabilitySet(CBRCase c) throws InitializingException {
        if (reachabilitySets == null) throw new InitializingException();
        return reachabilitySets.get(c);
    }

    /**
     * Returns the reachability sets of the case base.
     *
     * @return the reachability sets of the case base.
     */
    public Map<CBRCase, Collection<CBRCase>> getReachabilitySets() {
        return reachabilitySets;
    }
}
