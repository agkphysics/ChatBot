/**
 * Attribute.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.cbrcore;

import java.lang.reflect.Field;

import jcolibri.exception.AttributeAccessException;

/**
 * This class identifies an attribute of a CaseComponent (Java Bean). Attributes
 * are part of CaseComponents and CaseComponentes build a case. Note that each
 * CaseComponent must be a Java Bean, so this class represents a field of a Java
 * Bean (with its getXXX() and setXXX() methods).
 *
 * @see jcolibri.cbrcore.CaseComponent
 * @author Juan A. Recio-Garc�a
 */
public class Attribute {

    private Field field;

    /**
     * Creates an Attribute using the Field obtained with Reflection
     * (getClass.getDeclaredField(name)). It is recommended to use the other
     * constructor.
     */
    public Attribute(Field f) {
        field = f;
    }

    /**
     * Creates an attribute. The attribute name must be an existing field of the
     * class and have the get/set() methods.
     *
     * @param attributeName
     *            Name of the field
     * @param _class
     *            Class that the attribute belongs to.
     */
    public Attribute(String attributeName, Class<?> _class) {
        try {
            field = _class.getDeclaredField(attributeName);
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass())
                    .error(e + "Attribute: " + attributeName + " Class:" + _class.getName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Attribute)) return false;
        return (field.getName().equals(((Attribute)o).getName()))
                && (field.getDeclaringClass().equals(((Attribute)o).getDeclaringClass()));
    }

    /**
     * Returns the class that this attribute belongs to.
     */
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    /**
     * Returns the name of the field.
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Returns the type of the attribute.
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * Returns the value of the attribute for a concrete object. Of course, the
     * object must be instance of the class that this attribute belongs to.
     *
     * @param obj
     *            Instance to obtain the attribute from
     * @throws AttributeAccessException
     */
    public Object getValue(Object obj) throws AttributeAccessException {
        Object res = null;
        try {
            res = field.get(obj);
            return res;
        } catch (Exception e) {}

        try {
            java.beans.PropertyDescriptor pd = new java.beans.PropertyDescriptor(field.getName(),
                    field.getDeclaringClass());
            res = pd.getReadMethod().invoke(obj, (Object[])null);
            return res;
        } catch (Exception e) {}
        throw new AttributeAccessException(
                "Error getting value from object: " + obj + ", attribute: " + field.getName());

    }

    /**
     * Returns the hashCode of the attribute.
     */
    @Override
    public int hashCode() {
        return field.getName().hashCode();
    }

    /**
     * Sets the value of the attribute in a concrete object.
     *
     * @param obj
     *            Object that defines the attribute to set.
     * @param value
     *            Value to set.
     * @throws AttributeAccessException
     */
    public void setValue(Object obj, Object value) throws AttributeAccessException {
        try {
            field.set(obj, value);
        } catch (Exception e) {}

        try {
            java.beans.PropertyDescriptor pd = new java.beans.PropertyDescriptor(field.getName(),
                    field.getDeclaringClass());
            Object[] args = { value };
            pd.getWriteMethod().invoke(obj, args);
        } catch (Exception e) {
            throw new AttributeAccessException(
                    "Error setting value from object: " + obj + ", attribute: " + field.getName());
        }

    }

}
