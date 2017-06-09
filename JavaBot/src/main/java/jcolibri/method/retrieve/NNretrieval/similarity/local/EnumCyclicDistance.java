package jcolibri.method.retrieve.NNretrieval.similarity.local;

import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function computes the similarity between two enum values as their cyclic
 * distance.
 *
 * @author Juan A. Recio-Garc�a
 */
public class EnumCyclicDistance implements LocalSimilarityFunction {

    /**
     * Applies the similarity.
     *
     * @param o1
     *            Enum.
     * @param o2
     *            Enum.
     * @return the result to apply the similarity.
     */
    @Override
    public double compute(Object o1, Object o2) throws jcolibri.exception.NoApplicableSimilarityFunctionException {
        if ((o1 == null) || (o2 == null)) return 0;
        if (!(o1 instanceof Enum))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
        if (!(o2 instanceof Enum))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o2.getClass());

        Enum<?> e1 = (Enum<?>)o1;
        Enum<?> e2 = (Enum<?>)o2;

        double size = e1.getDeclaringClass().getEnumConstants().length;
        double distance = Math.abs(e1.ordinal() - e2.ordinal());
        double cyclicDistance = size - distance;

        if (distance <= cyclicDistance) return 1 - distance / size;
        else return 1 - cyclicDistance / size;
    }

    /** Applicable to Enum */
    @Override
    public boolean isApplicable(Object o1, Object o2) {
        if ((o1 == null) && (o2 == null)) return true;
        else if (o1 == null) return o2 instanceof Enum;
        else if (o2 == null) return o1 instanceof Enum;
        else return (o1 instanceof Enum) && (o2 instanceof Enum);
    }

}
