/**
 * DirectAttributeCopyMethod.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 05/01/2007
 */
package jcolibri.method.reuse;

import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.exception.AttributeAccessException;

/**
 * Copies the value of an attribute in the query to an attribute of a case
 *
 * @author Juan A. Recio-Garcia
 * @version 2.0
 *
 */
public class DirectAttributeCopyMethod {

    /**
     * Copies the value of the querySource attribute into the caseDestination
     * attribute of the cases.
     */
    public static void copyAttribute(Attribute querySource, Attribute caseDestination, CBRQuery query,
            Collection<CBRCase> cases) {
        Object queryValue = jcolibri.util.AttributeUtils.findValue(querySource, query);
        try {

            for (CBRCase c : cases) {
                CaseComponent cc = jcolibri.util.AttributeUtils.findBelongingComponent(caseDestination, c);
                caseDestination.setValue(cc, queryValue);
            }
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(DirectAttributeCopyMethod.class).error(e);
        }
    }

}
