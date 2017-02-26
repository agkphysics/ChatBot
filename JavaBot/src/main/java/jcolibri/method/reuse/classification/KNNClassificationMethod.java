package jcolibri.method.reuse.classification;

import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.extensions.classification.ClassificationSolution;
import jcolibri.method.retrieve.RetrievalResult;

/**
 * Interface for providing the ability to classify a query by predicting its
 * solution from supplied cases.
 *
 * @author Derek Bridge
 * @author Lisa Cummins 16/05/07
 */
public interface KNNClassificationMethod {
    /**
     * Gets the predicted solution of the given cases according to the
     * classification type and returns a case that has the query description and
     * the predicted solution.
     *
     * @param query
     *            the query.
     * @param cases
     *            a list of cases along with similarity scores.
     * @return Returns a case with the query description as its description and
     *         the predicted solution as its solution.
     */
    public CBRCase getPredictedCase(CBRQuery query, Collection<RetrievalResult> cases);

    /**
     * Gets the predicted solution of the given cases according to the
     * classification type.
     *
     * @param cases
     *            a list of cases along with similarity scores.
     * @return Returns the predicted solution.
     */
    public ClassificationSolution getPredictedSolution(Collection<RetrievalResult> cases);
}
