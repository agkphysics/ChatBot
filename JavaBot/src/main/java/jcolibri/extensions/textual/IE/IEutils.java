/**
 * IEutils.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 21/06/2007
 */
package jcolibri.extensions.textual.IE;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.exception.AttributeAccessException;
import jcolibri.extensions.textual.IE.representation.IEText;

/**
 * Utility functions for the IE extension.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public class IEutils {
    public static void addTexts(CaseComponent cc, Collection<IEText> list) {
        if (cc == null) return;
        Attribute[] attrs = jcolibri.util.AttributeUtils.getAttributes(cc.getClass());
        for (int i = 0; i < attrs.length; i++) {
            try {
                Object o = attrs[i].getValue(cc);
                if (o instanceof IEText) list.add((IEText)o);
                else if (o instanceof CaseComponent) addTexts((CaseComponent)o, list);
            } catch (AttributeAccessException e) {
                org.apache.commons.logging.LogFactory.getLog(IEutils.class).error(e);
            }
        }
    }

    public static Collection<IEText> getTexts(CBRCase _case) {
        ArrayList<IEText> res = new ArrayList<>();
        addTexts(_case.getDescription(), res);
        addTexts(_case.getSolution(), res);
        addTexts(_case.getJustificationOfSolution(), res);
        addTexts(_case.getResult(), res);
        return res;
    }

    public static Collection<IEText> getTexts(CBRQuery query) {
        ArrayList<IEText> res = new ArrayList<>();
        addTexts(query.getDescription(), res);
        return res;
    }

}
