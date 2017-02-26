package jcolibri.method.maintenance.solvesFunctions;

import java.util.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.maintenance.SolvesFunction;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.method.reuse.classification.KNNClassificationConfig;
import jcolibri.method.revise.classification.BasicClassificationOracle;
import jcolibri.method.revise.classification.ClassificationOracle;

/**
 * Provides the ICF algorithm solves function which will decide which cases
 * solve a query.
 *
 * @author Lisa Cummins
 * @author Derek Bridge 22/05/07
 */
public class ICFSolvesFunction extends SolvesFunction {
    /**
     * Sets the cases that both solve q or contribute to its misclassification.
     * In the case of ICF we only record cases that solve q. According to the
     * ICF definition, a case solves a query if it is of the same class as the
     * query and if there are no classes more similar to the query with a
     * different class.
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
        solveQ = new LinkedList<>();
        misclassifyQ = null;

        knnConfig.setK(RetrievalResult.RETRIEVE_ALL);
        Collection<RetrievalResult> orderedRetrievedCases = NNScoringMethod.evaluateSimilarity(cases, q, knnConfig);
        orderedRetrievedCases = SelectCases.selectTopKRR(orderedRetrievedCases, knnConfig.getK());

        ClassificationOracle oracle = new BasicClassificationOracle();
        boolean disagreeingCaseFound = false;
        Iterator<RetrievalResult> iter = orderedRetrievedCases.iterator();
        while (!disagreeingCaseFound && iter.hasNext()) {
            CBRCase c = iter.next().get_case();
            ClassificationSolution cSol = (ClassificationSolution)c.getSolution();
            if (oracle.isCorrectPrediction(cSol, q)) {
                solveQ.add(c);
            } else {
                disagreeingCaseFound = true;
            }
        }
    }
}
