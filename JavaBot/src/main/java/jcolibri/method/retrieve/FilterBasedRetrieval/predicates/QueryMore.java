/**
 * Equal.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 28/10/2007
 */
package jcolibri.method.retrieve.FilterBasedRetrieval.predicates;

import jcolibri.exception.NoApplicableFilterPredicateException;

/**
 * Predicate that compares if the query object is more than the case object.
 * Only applicable to enums and numbers.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 * @see jcolibri.method.retrieve.FilterBasedRetrieval.FilterBasedRetrievalMethod
 * @see jcolibri.method.retrieve.FilterBasedRetrieval.FilterConfig
 */
public class QueryMore implements FilterPredicate {
    @Override
    public boolean compute(Object caseObject, Object queryObject) throws NoApplicableFilterPredicateException {
        if ((caseObject == null) && (queryObject == null)) return true;
        else if (caseObject == null) return false;
        else if (queryObject == null) return true;
        else if (!((caseObject instanceof java.lang.Number) || (caseObject instanceof Enum)))
            throw new jcolibri.exception.NoApplicableFilterPredicateException(this.getClass(), caseObject.getClass());
        else if (!((queryObject instanceof java.lang.Number) || (queryObject instanceof Enum)))
            throw new jcolibri.exception.NoApplicableFilterPredicateException(this.getClass(), queryObject.getClass());
        else {
            double caseValue;
            double queryValue;
            if ((caseObject instanceof Number) && (queryObject instanceof Number)) {
                Number n1 = (Number)caseObject;
                caseValue = n1.doubleValue();
                Number n2 = (Number)queryObject;
                queryValue = n2.doubleValue();
            } else {
                Enum<?> enum1 = (Enum<?>)caseObject;
                caseValue = enum1.ordinal();
                Enum<?> enum2 = (Enum<?>)queryObject;
                queryValue = enum2.ordinal();
            }
            return queryValue > caseValue;
        }
    }

}
