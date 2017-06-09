package jcolibri.method.maintenance.algorithms;

import java.util.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.maintenance.AbstractCaseBaseEditMethod;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.method.reuse.classification.KNNClassificationConfig;
import jcolibri.method.reuse.classification.KNNClassificationMethod;
import jcolibri.method.revise.classification.BasicClassificationOracle;
import jcolibri.method.revise.classification.ClassificationOracle;

/**
 * Provides the ability to run the RENN case base editing algorithm on a case
 * base to eliminate noise.
 *
 * @author Lisa Cummins
 * @author Derek Bridge 18/05/07
 */
public class RENNNoiseReduction extends AbstractCaseBaseEditMethod {

    /**
     * Simulates the RENN case base editing algorithm, returning the cases that
     * would be deleted by the algorithm.
     *
     * @param cases
     *            The group of cases on which to perform editing.
     * @param simConfig
     *            The similarity configuration for these cases.
     * @return the list of cases that would be deleted by the RENN algorithm.
     */
    @Override
    public Collection<CBRCase> retrieveCasesToDelete(Collection<CBRCase> cases,
            KNNClassificationConfig simConfig) { /*
                                                  * RENN Algorithm:
                                                  *
                                                  * T: Training Set
                                                  *
                                                  * Repeat changes = false For
                                                  * all x E T do If x does not
                                                  * agree with the majority of
                                                  * its NN T = T - {x} changes =
                                                  * true End-If End-For Until
                                                  * not changes
                                                  *
                                                  * Return T
                                                  */
        jcolibri.util.ProgressController.init(this.getClass(), "RENN Noise Reduction",
                jcolibri.util.ProgressController.UNKNOWN_STEPS);
        List<CBRCase> localCases = new LinkedList<>();

        for (CBRCase c : cases) {
            localCases.add(c);
        }

        List<CBRCase> allCasesToBeRemoved = new LinkedList<>();

        boolean changes = true;
        while (changes && localCases.size() > 1) {
            changes = false;
            ListIterator<CBRCase> iter = localCases.listIterator();
            while (iter.hasNext()) {
                CBRCase q = iter.next();
                iter.remove();
                Collection<RetrievalResult> knn = NNScoringMethod.evaluateSimilarity(localCases, q, simConfig);
                knn = SelectCases.selectTopKRR(knn, simConfig.getK());
                try {
                    KNNClassificationMethod classifier = simConfig.getClassificationMethod();
                    ClassificationSolution predictedSolution = classifier.getPredictedSolution(knn);
                    ClassificationOracle oracle = new BasicClassificationOracle();
                    if (!oracle.isCorrectPrediction(predictedSolution, q)) {
                        allCasesToBeRemoved.add(q);
                        changes = true;
                    } else {
                        iter.add(q);
                    }
                } catch (ClassCastException cce) {
                    org.apache.commons.logging.LogFactory.getLog(RENNNoiseReduction.class).error(cce);
                    System.exit(0);
                }
            }
            jcolibri.util.ProgressController.step(this.getClass());
        }
        jcolibri.util.ProgressController.finish(this.getClass());
        return allCasesToBeRemoved;
    }
}
