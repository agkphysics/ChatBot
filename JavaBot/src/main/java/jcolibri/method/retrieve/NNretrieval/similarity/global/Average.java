/**
 * Average.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.method.retrieve.NNretrieval.similarity.global;

import jcolibri.method.retrieve.NNretrieval.similarity.StandardGlobalSimilarityFunction;

/**
 * This function computes the average of the similarites of its subattributes.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public class Average extends StandardGlobalSimilarityFunction {

    @Override
    public double computeSimilarity(double[] values, double[] weigths, int ivalue) {
        double acum = 0;
        double weigthsAcum = 0;
        for (int i = 0; i < ivalue; i++) {
            acum += values[i] * weigths[i];
            weigthsAcum += weigths[i];
        }
        return acum / weigthsAcum;
    }

}
