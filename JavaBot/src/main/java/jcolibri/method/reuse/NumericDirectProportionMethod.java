/**
 * NumericDirectProportionMethod.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 09/01/2007
 */
package jcolibri.method.reuse;

import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.exception.AttributeAccessException;
import jcolibri.util.AttributeUtils;

/**
 * Computes the value of an attribute related to a description attribute as
 * proportional to the actual values of these attributes in a retrieved case.
 *
 * @author Juan A. Recio-Garcia
 *
 */
public class NumericDirectProportionMethod {

    /**
     * This method computes the proportion of the values of a source attibute in
     * a query and a case, and modifies the destination attribute in the case
     * with that proportion.
     */
    public static void directProportion(Attribute source, Attribute destination, CBRQuery query,
            Collection<CBRCase> cases) {
        Object qs = AttributeUtils.findValue(source, query);
        if (qs == null) return;
        if (!(qs instanceof Number)) return;

        Number qsn = (Number)qs;

        for (CBRCase c : cases) {
            try {
                Object cs = AttributeUtils.findValue(source, c);
                Object cdcomp = AttributeUtils.findBelongingComponent(destination, c);
                Object cd = destination.getValue(cdcomp);
                if ((cs == null) || (cd == null)) return;
                if (!(cs instanceof Number) || !(cd instanceof Number)) return;

                Number csn = (Number)cs;
                Number cdn = (Number)cd;

                Double dres = (cdn.doubleValue() / csn.doubleValue()) * qsn.doubleValue();

                if (cd instanceof Double) destination.setValue(cdcomp, dres);
                else if (cd instanceof Integer) destination.setValue(cdcomp, new Integer(dres.intValue()));
                else if (cd instanceof Float) destination.setValue(cdcomp, new Float(dres.floatValue()));
                else if (cd instanceof Byte) destination.setValue(cdcomp, new Byte(dres.byteValue()));
                else if (cd instanceof Short) destination.setValue(cdcomp, new Short(dres.shortValue()));

            } catch (AttributeAccessException e) {
                org.apache.commons.logging.LogFactory.getLog(NumericDirectProportionMethod.class).error(e);
            }

        }
    }
}
