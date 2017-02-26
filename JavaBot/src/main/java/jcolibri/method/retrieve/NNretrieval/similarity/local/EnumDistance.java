package jcolibri.method.retrieve.NNretrieval.similarity.local;

import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns the similarity of two enum values as the their distance
 * sim(x,y)=|ord(x) - ord(y)|
 *
 * @author Juan A. Recio-Garc�a
 */
public class EnumDistance implements LocalSimilarityFunction {

    /**
     * Applies the similarity function.
     *
     * @param o1
     *            StringEnum or String
     * @param o2
     *            StringEnum or String
     * @return the result of apply the similarity function.
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
        double diff = Math.abs(e1.ordinal() - e2.ordinal());

        return 1 - (diff / size);
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
