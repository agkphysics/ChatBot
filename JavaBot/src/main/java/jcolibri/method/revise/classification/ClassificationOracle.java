package jcolibri.method.revise.classification;

import jcolibri.cbrcore.*;
import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.reuse.classification.KNNClassificationConfig;

/**
 * Interface that defines functions to decide if a query has been correctly
 * classified or not and to assign a cost to incorrectly classified queries.
 *
 * @author Derek Bridge
 * @author Lisa Cummins 16/05/07
 */
public interface ClassificationOracle {

    /**
     * Calculates the cost of the prediction made by the given case-base and
     * similarity configuration for the solution of the query.
     *
     * @param query
     *            the query to be tested.
     * @param caseBase
     *            the case base to use to find the predicted solution.
     * @param knnConfig
     *            the similarity configuration.
     * @return the cost of the prediction made.
     */
    public double getPredictionCost(CBRQuery query, CBRCaseBase caseBase, KNNClassificationConfig knnConfig);

    /**
     * Calculates the cost of the given solution as a prediction for the
     * solution of the given case.
     *
     * @param predictedSolution
     *            the predicted solution.
     * @param testCase
     *            the test case (query and correct solution).
     * @return the cost of the prediction made.
     */
    public double getPredictionCost(ClassificationSolution predictedSolution, CBRCase testCase);

    /**
     * Calculates the cost of the given test solution while bein compared to the
     * given correct solution.
     *
     * @param predictedSolution
     *            the predicted solution.
     * @param correctSolution
     *            the correct solution.
     * @return the cost of the prediction made.
     */
    public double getPredictionCost(ClassificationSolution predictedSolution, ClassificationSolution correctSolution);

    /**
     * Checks if the query is correctly classified by the given case-base and
     * similarity configuration.
     *
     * @param query
     *            the query to be tested.
     * @param caseBase
     *            the case base to use to find the predicted solution.
     * @param knnConfig
     *            the similarity configuration.
     * @return true if the query is correctly classified by the given case-base
     *         and similarity configuration, fasle otherwise.
     */
    public boolean isCorrectPrediction(CBRQuery query, CBRCaseBase caseBase, KNNClassificationConfig knnConfig);

    /**
     * Checks if the predicted solution is the correct solution for the given
     * test case.
     *
     * @param predictedSolution
     *            the predicted solution.
     * @param testCase
     *            the test case (query and correct solution).
     * @return true if the predicted solution is the correct solution for the
     *         given test case, false if not.
     */
    public boolean isCorrectPrediction(ClassificationSolution predictedSolution, CBRCase testCase);

    /**
     * Checks if the predicted solution and the correct solution are the same.
     *
     * @param predictedSolution
     *            the predicted solution.
     * @param correctSolution
     *            the correct solution.
     * @return true if the predicted solution and the correct solution are the
     *         same, false if not.
     */
    public boolean isCorrectPrediction(ClassificationSolution predictedSolution,
            ClassificationSolution correctSolution);
}
