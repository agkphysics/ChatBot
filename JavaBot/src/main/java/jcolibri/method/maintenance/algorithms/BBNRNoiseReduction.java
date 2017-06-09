package jcolibri.method.maintenance.algorithms;

import java.util.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.exception.InitializingException;
import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.maintenance.*;
import jcolibri.method.maintenance.solvesFunctions.CBESolvesFunction;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.method.reuse.classification.KNNClassificationConfig;
import jcolibri.method.reuse.classification.KNNClassificationMethod;
import jcolibri.method.revise.classification.BasicClassificationOracle;
import jcolibri.method.revise.classification.ClassificationOracle;

/**
 * Provides the ability to run the BBNR case base editing algorithm on a case
 * base to eliminate noise.
 *
 * @author Lisa Cummins
 * @author Derek Bridge 18/05/07
 */
public class BBNRNoiseReduction extends AbstractCaseBaseEditMethod {

    /**
     * Simulates the BBNR editing algorithm, returning the cases that would be
     * deleted by the algorithm.
     *
     * @param cases
     *            The group of cases on which to perform editing.
     * @param simConfig
     *            The similarity configuration for these cases.
     * @return the list of cases that would be deleted by the BBNR algorithm.
     */
    @Override
    public LinkedList<CBRCase> retrieveCasesToDelete(Collection<CBRCase> cases,
            KNNClassificationConfig simConfig) { /*
                                                  * Blame-based Noise Reduction
                                                  * (BBNR) Algorithm T, Training
                                                  * Set For each c in T CSet(c)
                                                  * = Coverage Set of c LSet(c)
                                                  * = Liability Set of c End-For
                                                  *
                                                  * TSet = T sorted in
                                                  * descending order of LSet(c)
                                                  * size c = first case in TSet
                                                  *
                                                  * While |LSet(c)| >0 TSet =
                                                  * TSet - {c} misClassifiedFlag
                                                  * = false For each x in
                                                  * CSet(c) If x cannot be
                                                  * correctly classified by TSet
                                                  * misClassifiedFlag = true
                                                  * break End-If End-For If
                                                  * misClassifiedFlag = true
                                                  * TSet = TSet + {c} End-If c =
                                                  * next case in TSet End-While
                                                  *
                                                  * Return TSet
                                                  */

        jcolibri.util.ProgressController.init(this.getClass(), "Blame-based Noise Reduction (BBNR)",
                jcolibri.util.ProgressController.UNKNOWN_STEPS);
        List<CBRCase> localCases = new LinkedList<>();
        for (CBRCase c : cases) {
            localCases.add(c);
        }

        CompetenceModel sc = new CompetenceModel();
        sc.computeCompetenceModel(new CBESolvesFunction(), simConfig, localCases);

        List<CaseResult> caseLiabilitySetSizes = new LinkedList<>();

        for (CBRCase c : localCases) {
            Collection<CBRCase> currLiabilitySet = null;
            try {
                currLiabilitySet = sc.getLiabilitySet(c);
            } catch (InitializingException e) {
                e.printStackTrace();
            }
            int liabilitySetSize = 0;

            if (currLiabilitySet != null) {
                liabilitySetSize = currLiabilitySet.size();
            }

            caseLiabilitySetSizes.add(new CaseResult(c, liabilitySetSize));
            jcolibri.util.ProgressController.step(this.getClass());
        }

        caseLiabilitySetSizes = CaseResult.sortCaseResults(false, caseLiabilitySetSizes);

        LinkedList<CBRCase> allCasesToBeRemoved = new LinkedList<>();

        for (ListIterator<CaseResult> liabIter = caseLiabilitySetSizes.listIterator(); liabIter.hasNext();) {
            CaseResult highestLiability = liabIter.next();
            if (highestLiability.getResult() <= 0) {
                break;
            }

            CBRCase removed = highestLiability.getCase();
            localCases.remove(removed);

            Collection<CBRCase> covSet = null;
            try {
                covSet = sc.getCoverageSet(removed);
            } catch (InitializingException e) {
                e.printStackTrace();
            }

            boolean caseMisclassified = false;
            for (CBRCase query : covSet) {
                Collection<RetrievalResult> knn = NNScoringMethod.evaluateSimilarity(localCases, query, simConfig);
                knn = SelectCases.selectTopKRR(knn, simConfig.getK());
                try {
                    KNNClassificationMethod classifier = simConfig.getClassificationMethod();
                    ClassificationSolution predictedSolution = classifier.getPredictedSolution(knn);
                    ClassificationOracle oracle = new BasicClassificationOracle();

                    if (!oracle.isCorrectPrediction(predictedSolution, query)) {
                        caseMisclassified = true;
                        break;
                    }
                } catch (ClassCastException cce) {
                    org.apache.commons.logging.LogFactory.getLog(BBNRNoiseReduction.class).error(cce);
                    System.exit(0);
                }
            }
            if (caseMisclassified) {
                localCases.add(removed);
            } else {
                allCasesToBeRemoved.add(removed);
            }
            jcolibri.util.ProgressController.step(this.getClass());
        }
        jcolibri.util.ProgressController.finish(this.getClass());
        return allCasesToBeRemoved;
    }
}
