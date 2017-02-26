package jcolibri.method.retrieve.NNretrieval.similarity.local;

import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns 1 if both String are the same despite case letters, 0
 * in the other case
 */
public class EqualsStringIgnoreCase implements LocalSimilarityFunction {

    /**
     * Applies the similarity funciton.
     *
     * @param s
     *            String
     * @param t
     *            String
     * @return the result of apply the similarity function.
     */
    @Override
    public double compute(Object s, Object t) throws jcolibri.exception.NoApplicableSimilarityFunctionException {
        if ((s == null) || (t == null)) return 0;
        if (!(s instanceof java.lang.String))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), s.getClass());
        if (!(t instanceof java.lang.String))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), t.getClass());

        return (((String)s).equalsIgnoreCase(((String)t)) ? 1 : 0);
    }

    /** Applicable to String */
    @Override
    public boolean isApplicable(Object o1, Object o2) {
        if ((o1 == null) && (o2 == null)) return true;
        else if (o1 == null) return o2 instanceof String;
        else if (o2 == null) return o1 instanceof String;
        else return (o1 instanceof String) && (o2 instanceof String);
    }
}
