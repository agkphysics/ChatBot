package jcolibri.method.maintenance.solvesFunctions;

import java.util.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.maintenance.SolvesFunction;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.method.reuse.classification.KNNClassificationConfig;
import jcolibri.method.reuse.classification.KNNClassificationMethod;
import jcolibri.method.revise.classification.BasicClassificationOracle;
import jcolibri.method.revise.classification.ClassificationOracle;

/**
 * Provides the solves function that Sarah-Jane Delaney uses in her case-base
 * editing (CBE) algorithms which will decide which cases solve a query.
 *
 * @author Lisa Cummins
 * @author Derek Bridge 22/05/07
 */
public class CBESolvesFunction extends SolvesFunction {
    /**
     * Sets the cases that either solve q or contribute to its
     * misclassification. A case solves a query if the query is correctly
     * classified by its nearest neighbours and the solution of the case agrees
     * with the solution of the query. A case misclassifies a query if the query
     * is incorrectly classified by its nearest neighbours and the solution of
     * the case disagrees with the solution of the query.
     *
     * @param q
     *            the query
     * @param cases
     *            from which to find the cases which solve and classify the
     *            query. These include the query itself.
     * @param knnConfig
     *            the similarity configuration
     */
    @Override
    public void setCasesThatSolveAndMisclassifyQ(CBRCase q, Collection<CBRCase> cases,
            KNNClassificationConfig knnConfig) {
        solveQ = new LinkedList<>(); // It will always contain at least
                                     // the query itself
        misclassifyQ = null;

        /*
         * q is regarded to solve itself regardless of whether it is correctly
         * or incorrectly classified by its nearest neighbours and so we add it
         * to its solveQ set.
         */
        solveQ.add(q);

        /*
         * Because q is included in the cases, we retrieve k+1 neighbours and
         * then either remove q, or, if q is not contained in the retrieved
         * cases, we remove the last case of those retrieved
         */
        knnConfig.setK(knnConfig.getK() + 1);
        Collection<RetrievalResult> knnResults = NNScoringMethod.evaluateSimilarity(cases, q, knnConfig);
        Collection<CBRCase> knn = SelectCases.selectTopK(knnResults, knnConfig.getK());
        knnConfig.setK(knnConfig.getK() - 1);
        RetrievalResult result = null;
        boolean qFound = false;

        for (Iterator<RetrievalResult> cIter = knnResults.iterator(); cIter.hasNext() && !qFound;) {
            result = cIter.next();
            if (result.get_case().equals(q)) {
                knnResults.remove(result);
                qFound = true;
            }
        }
        if (!qFound) {
            knn.remove(result);
        }
        try {
            KNNClassificationMethod classifier = knnConfig.getClassificationMethod();
            ClassificationSolution predictedSolution = classifier.getPredictedSolution(knnResults);

            ClassificationOracle oracle = new BasicClassificationOracle();
            boolean correct = oracle.isCorrectPrediction(predictedSolution, q);
            if (correct) {
                for (RetrievalResult res : knnResults) {
                    CBRCase c = res.get_case();
                    if (oracle.isCorrectPrediction((ClassificationSolution)c.getSolution(), q)) {
                        solveQ.add(c);
                    }
                }
            } else {
                misclassifyQ = new LinkedList<>();
                for (RetrievalResult res : knnResults) {
                    CBRCase c = res.get_case();
                    if (!oracle.isCorrectPrediction((ClassificationSolution)c.getSolution(), q)) {
                        misclassifyQ.add(c);
                    }
                }

            }
        } catch (ClassCastException cce) {
            org.apache.commons.logging.LogFactory.getLog(CBESolvesFunction.class).error(cce);
            System.exit(0);
        }

    }
}
