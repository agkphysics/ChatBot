/**
 * FilterBasedRetrievalMethod.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 28/10/2007
 */
package jcolibri.method.retrieve.FilterBasedRetrieval;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.method.retrieve.FilterBasedRetrieval.predicates.FilterPredicate;
import jcolibri.util.AttributeUtils;

/**
 * Retrieves cases according boolean predicates (less, more, equal, ...) over
 * the attributes.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class FilterBasedRetrievalMethod {
    /**
     * Retrieves cases that match with the given predicates in filter config
     *
     * @param cases
     *            to retrieve from
     * @param query
     *            to compare
     * @param filterConfig
     *            contains the filter predicates
     * @return a list of cases
     */
    public static Collection<CBRCase> filterCases(Collection<CBRCase> cases, CBRQuery query,
            FilterConfig filterConfig) {
        if (filterConfig == null) return cases;
        Collection<CBRCase> res = new ArrayList<>();
        for (CBRCase c : cases)
            if (filter(c.getDescription(), query.getDescription(), filterConfig)) res.add(c);
        return res;
    }

    /**
     * Filters a case component
     *
     * @param cc
     * @param qc
     * @param config
     * @return
     */
    private static boolean filter(CaseComponent cc, CaseComponent qc, FilterConfig config) {
        try {
            for (Attribute att : AttributeUtils.getAttributes(cc)) {
                if (CaseComponent.class.isAssignableFrom(att.getType())) {
                    if (!filter((CaseComponent)att.getValue(cc), (CaseComponent)att.getValue(qc), config)) return false;
                } else if (att.equals(cc.getIdAttribute())) continue;
                else {
                    FilterPredicate predicate = config.getPredicate(att);
                    if (predicate == null) continue;
                    if (!predicate.compute(att.getValue(cc), att.getValue(qc))) return false;
                }
            }
            return true;
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(FilterBasedRetrievalMethod.class).error(e);
            return false;
        }
    }
}
