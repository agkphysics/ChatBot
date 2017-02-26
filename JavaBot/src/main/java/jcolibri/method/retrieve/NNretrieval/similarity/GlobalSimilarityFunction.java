/**
 * LocalSimilarityFunction.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.method.retrieve.NNretrieval.similarity;

import jcolibri.cbrcore.*;
import jcolibri.method.retrieve.NNretrieval.NNConfig;

/**
 * Defines the methods of a global similarity function. A global similarity
 * function is applied to compound attributes by the NN algorithm.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public interface GlobalSimilarityFunction {

    /**
     * Computes the global simliarity between two compound attributes. It
     * requires the NNConfig object that stores the configuration of its
     * contained attributes.
     *
     * @param componentOfCase
     *            compound attribute of the case
     * @param componentOfQuery
     *            compound attribute of the query
     * @param _case
     *            case being compared
     * @param _query
     *            query being compared
     * @param numSimConfig
     *            Similarity functions configuration
     * @return a value between [0..1]
     */
    public double compute(CaseComponent componentOfCase, CaseComponent componentOfQuery, CBRCase _case, CBRQuery _query,
            NNConfig numSimConfig);
}
