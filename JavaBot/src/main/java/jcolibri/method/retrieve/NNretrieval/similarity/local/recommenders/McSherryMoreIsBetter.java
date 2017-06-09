package jcolibri.method.retrieve.NNretrieval.similarity.local.recommenders;

import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns the similarity of two numbers following the McSherry -
 * More is Better formulae
 *
 * sim(c.a,q.a)= 1 - ((max(a) - c.a) / (max(a)-min(a)))
 *
 * min(a) and max(a) must be defined by the designer. q.a is not taken into
 * account.
 */
public class McSherryMoreIsBetter implements LocalSimilarityFunction {

    /** InrecaLessIsBetter. */
    double maxValue;
    double minValue;

    /**
     * Constructor. max and min values are ignored for enum types.
     *
     */

    public McSherryMoreIsBetter(double maxAttributeValue, double minAttributeValue) {
        maxValue = maxAttributeValue;
        minValue = minAttributeValue;
    }

    /**
     * Applies the similarity function.
     *
     * @param caseObject
     *            is a Number
     * @param queryObject
     *            is a Number
     * @return result of apply the similarity function.
     */
    @Override
    public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
        if ((caseObject == null)) return 0;
        if (!((caseObject instanceof java.lang.Number) || (caseObject instanceof Enum)))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(),
                    caseObject.getClass());

        double caseValue;
        double max;
        double min;
        if (caseObject instanceof Number) {
            Number n1 = (Number)caseObject;
            caseValue = n1.doubleValue();
            max = maxValue;
            min = minValue;
        } else {
            Enum<?> enum1 = (Enum<?>)caseObject;
            caseValue = enum1.ordinal();
            max = caseObject.getClass().getEnumConstants().length;
            min = 0;
        }

        return 1 - ((max - caseValue) / (max - min));

    }

    /** Applicable to any Number subinstance */
    @Override
    public boolean isApplicable(Object o1, Object o2) {
        if ((o1 == null) && (o2 == null)) return true;
        else if (o1 == null) return (o2 instanceof Number) || (o2 instanceof Enum);
        else if (o2 == null) return (o1 instanceof Number) || (o1 instanceof Enum);
        else return ((o1 instanceof Number) && (o2 instanceof Number))
                || ((o1 instanceof Enum) && (o2 instanceof Enum));
    }

}
