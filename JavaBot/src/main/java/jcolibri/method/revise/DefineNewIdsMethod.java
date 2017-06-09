/**
 * DefineNewIdsMethod.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 07/01/2007
 */
package jcolibri.method.revise;

import java.util.HashMap;

import jcolibri.cbrcore.*;

/**
 * Defines new ids for the case components of a case. This way it can be stored
 * in the persistence media without overwriting an existing case.
 *
 * @author Juan A. Recio-Garcia
 *
 */
public class DefineNewIdsMethod {

    /**
     * Changes the values of the ID attributes of a case with new ones. This
     * method traverses the CaseComponent tree of a case modifing the values of
     * the ids attributes with new objects.
     *
     * @param _case
     *            to modify the ids
     * @param componentsKeys
     *            stores the new values of the IDs attributes
     * @throws jcolibri.exception.ExecutionException
     */
    public static void defineNewIdsMethod(CBRCase _case, HashMap<Attribute, Object> componentsKeys)
            throws jcolibri.exception.ExecutionException {
        defineNewIds(_case.getDescription(), componentsKeys);
        defineNewIds(_case.getSolution(), componentsKeys);
        defineNewIds(_case.getJustificationOfSolution(), componentsKeys);
        defineNewIds(_case.getResult(), componentsKeys);
    }

    private static void defineNewIds(CaseComponent cc, HashMap<Attribute, Object> componentsKeys)
            throws jcolibri.exception.ExecutionException {
        if (cc == null) return;
        Attribute keyAtt = cc.getIdAttribute();
        Object newkeyvalue = componentsKeys.get(keyAtt);

        try {
            if (newkeyvalue != null) keyAtt.setValue(cc, newkeyvalue);

            for (java.lang.reflect.Field f : cc.getClass().getDeclaredFields()) {
                Attribute at = new Attribute(f);
                Object o = at.getValue(cc);
                if (o instanceof CaseComponent) defineNewIds((CaseComponent)o, componentsKeys);
            }
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(DefineNewIdsMethod.class).error(e);
        }

    }
}
