package jcolibri.method.reuse.classification;

import java.util.Collection;

import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.retrieve.RetrievalResult;

/**
 * Provides the ability to classify a query by predicting its solution from
 * supplied cases. Classification is done by unaimous voting respecting to a
 * class. That class is configured using the constructor .
 *
 * @author Juan A. Recio Garcia 16/05/07
 */
public class UnanimousVotingMethod extends AbstractKNNClassificationMethod {

    private Object _class;

    public UnanimousVotingMethod(Object classification) {
        _class = classification;
    }

    /**
     * Predicts the class that has all the votes among the k most similar cases
     * and is equal to the class configured using the constructor. If several
     * classes receive the same highest vote, the class that has the lowest hash
     * code is taken as the prediction.
     *
     * @param cases
     *            an ordered list of cases along with similarity scores.
     * @return Returns the predicted solution.
     */
    @Override
    public ClassificationSolution getPredictedSolution(Collection<RetrievalResult> cases) {
        ClassificationSolution solution = null;

        for (RetrievalResult result : cases) {
            ClassificationSolution sol = (ClassificationSolution)result.get_case().getSolution();

            Object classif = sol.getClassification();

            if (classif.equals(_class)) solution = sol;
            else return sol;
        }

        return solution;
    }
}
