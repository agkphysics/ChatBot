/**
 * AttributeUtils.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 09/01/2007
 */
package jcolibri.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.connector.TypeAdaptor;
import jcolibri.exception.AttributeAccessException;

/**
 * Utility methods to manage the attributes of a case.
 *
 * @author Juan A. Recio Garc�a
 * @version 1.0
 * @see jcolibri.cbrcore.Attribute
 *
 */
public class AttributeUtils {

    /**
     * Finds the belonging component of an attribute. A case is a CaseComponent
     * that can be composed by simple attributes or other CaseComponents. This
     * method traverses the CaseComponents structure of a case to find the
     * CaseComponent that an attribute belongs to.
     */
    public static CaseComponent findBelongingComponent(Attribute at, CaseComponent cc) {
        try {
            if (at.getDeclaringClass().equals(cc.getClass())) return cc;
            Attribute[] atts = getAttributes(cc.getClass());
            for (Attribute a : atts) {
                Object o = a.getValue(cc);
                if (o == null) continue;
                if (o instanceof CaseComponent) {
                    CaseComponent r = findBelongingComponent(at, (CaseComponent)o);
                    if (r != null) return r;
                }
            }
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(AttributeUtils.class).error(e);
        }
        return null;
    }

    /**
     * Similar to findBelongingComponent(Attribute, CaseComponent) as a CBRCase
     * is a CaseComponent.
     */
    public static CaseComponent findBelongingComponent(Attribute at, CBRCase c) {
        CaseComponent res = findBelongingComponent(at, c.getDescription());
        if (res != null) return res;
        res = findBelongingComponent(at, c.getSolution());
        if (res != null) return res;
        res = findBelongingComponent(at, c.getJustificationOfSolution());
        if (res != null) return res;
        res = findBelongingComponent(at, c.getResult());
        if (res != null) return res;
        return null;
    }

    /**
     * Similar to findBelongingComponent(Attribute, CaseComponent) as a CBRQuery
     * is a CaseComponent.
     */
    public static CaseComponent findBelongingComponent(Attribute at, CBRQuery q) {
        return findBelongingComponent(at, q.getDescription());
    }

    /**
     * Returns the value of an Attribute in a CaseComponent object.
     */
    public static Object findValue(Attribute at, CaseComponent cc) {
        CaseComponent belongCC = findBelongingComponent(at, cc);
        if (belongCC == null) return null;
        else try {
            return at.getValue(cc);
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(AttributeUtils.class).error(e);
        }
        return null;
    }

    /**
     * Finds the value of an Attribute in a CBRCase object.
     */
    public static Object findValue(Attribute at, CBRCase c) {
        CaseComponent cc = findBelongingComponent(at, c);
        if (cc == null) return null;
        else return findValue(at, cc);
    }

    /**
     * Finds the value of an Attribute in a CBRQuery object.
     */
    public static Object findValue(Attribute at, CBRQuery query) {
        return findValue(at, query.getDescription());
    }

    /**
     * Returns the list of attributes of a CaseComponents and all its
     * sub-caseComponents.
     */
    public static Collection<Attribute> getAttributes(CaseComponent cc) {
        if (cc == null) return null;
        Collection<Attribute> res = new ArrayList<>();
        try {
            Attribute[] ats = getAttributes(cc.getClass());
            for (Attribute a : ats)
                if (a.getType().equals(CaseComponent.class)) res.addAll(getAttributes((CaseComponent)a.getValue(cc)));
                else res.add(a);
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(AttributeUtils.class).error(e);
        }
        return res;
    }

    /**
     * Returns the list of attributes of a CaseComponents and all its
     * sub-caseComponents which values are instance of a given class
     */
    public static Collection<Attribute> getAttributes(CaseComponent cc, Class<?> _class) {
        if (cc == null) return null;
        Collection<Attribute> res = new ArrayList<>();
        try {
            Attribute[] ats = getAttributes(cc.getClass());
            for (Attribute a : ats)
                if (a.getType().equals(CaseComponent.class)) res.addAll(getAttributes((CaseComponent)a.getValue(cc)));
                else if (_class.isInstance(a.getValue(cc))) res.add(a);
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(AttributeUtils.class).error(e);
        }
        return res;
    }

    /**
     * Returns the list of attributes of a class.
     */
    public static Attribute[] getAttributes(Class<? extends CaseComponent> c) {
        Field[] fields = c.getDeclaredFields();
        Attribute[] res = new Attribute[fields.length];
        int i = 0;
        for (Field f : fields)
            res[i++] = new Attribute(f);
        return res;
    }

    /**
     * Returns the value of an Attribute in a CaseComponent object.
     */
    public static void setValue(Attribute at, CaseComponent cc, Object value) {
        CaseComponent belongCC = findBelongingComponent(at, cc);
        if (belongCC == null) return;
        else try {
            at.setValue(cc, value);
        } catch (AttributeAccessException e) {
            try {
                if (TypeAdaptor.class.isAssignableFrom(at.getType())) {
                    String content = value.toString();
                    TypeAdaptor ta = (TypeAdaptor)at.getType().newInstance();
                    ta.fromString(content);
                    at.setValue(cc, at.getType().cast(ta));
                }
            } catch (Exception e2) {
                org.apache.commons.logging.LogFactory.getLog(AttributeUtils.class).error(e2);
            }
        }
        return;
    }

    /**
     * Finds the value of an Attribute in a CBRCase object.
     */
    public static void setValue(Attribute at, CBRCase c, Object value) {
        CaseComponent cc = findBelongingComponent(at, c);
        if (cc == null) return;
        else setValue(at, cc, value);
    }

    /**
     * Finds the value of an Attribute in a CBRQuery object.
     */
    public static void setValue(Attribute at, CBRQuery query, Object value) {
        setValue(at, query.getDescription(), value);
    }
}
