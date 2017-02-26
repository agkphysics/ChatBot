/**
 * CaseComponent.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 05/01/2007
 */
package jcolibri.cbrcore;

/**
 * Interface that defines a component of a case. Cases are composed by instances
 * of this interface. These components are normal Java Beans with set/get()
 * methods for each field.
 *
 * @author Juan A. Recio-Garc�a
 */
public interface CaseComponent {

    /**
     * Returns the attribute that identifies the component. An id-attribute must
     * be unique for each component.
     */
    jcolibri.cbrcore.Attribute getIdAttribute();
}
