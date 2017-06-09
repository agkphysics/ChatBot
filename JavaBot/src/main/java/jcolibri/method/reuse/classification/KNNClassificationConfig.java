package jcolibri.method.reuse.classification;

import jcolibri.method.retrieve.NNretrieval.NNConfig;

/**
 * This class stores the configuration for the KNN classification methods.
 *
 * @author Lisa Cummins
 */
public class KNNClassificationConfig extends NNConfig {

    /**
     * The type of classification method being used by this config object.
     */
    KNNClassificationMethod classificationMethod;

    private int K = Integer.MAX_VALUE;

    /**
     * Returns the classification method stored in this config object.
     *
     * @return the classification method stored in this config object.
     */
    public KNNClassificationMethod getClassificationMethod() {
        return classificationMethod;
    }

    /**
     * @return Returns the k.
     */
    public int getK() {
        return K;
    }

    /**
     * Sets the classification method for this config object to be
     * classificationMethod.
     *
     * @param classificationMethod
     *            the classification method to be used for this config object.
     */
    public void setClassificationMethod(KNNClassificationMethod classificationMethod) {
        this.classificationMethod = classificationMethod;
    }

    /**
     * @param k
     *            The k to set.
     */
    public void setK(int k) {
        K = k;
    }
}
