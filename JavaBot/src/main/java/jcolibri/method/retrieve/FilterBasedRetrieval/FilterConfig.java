/**
 * FilterConfig.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 28/10/2007
 */
package jcolibri.method.retrieve.FilterBasedRetrieval;

import java.util.Collection;
import java.util.HashMap;

import jcolibri.cbrcore.Attribute;
import jcolibri.method.retrieve.FilterBasedRetrieval.predicates.FilterPredicate;

/**
 * Configuration object for the FilterBasedRetrievalMethod. It contains a Map
 * with pairs <Attribute,Predicate>.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 */
public class FilterConfig {
    private HashMap<Attribute, FilterPredicate> config;

    /**
     * Creates the FilterConfig object
     */
    public FilterConfig() {
        config = new HashMap<>();
    }

    /**
     * Adds a new predicate
     *
     * @param attribute
     *            that the predicate is associated to
     * @param predicate
     *            for the attribute
     */
    public void addPredicate(Attribute attribute, FilterPredicate predicate) {
        config.put(attribute, predicate);
    }

    /**
     * Returns a list of attributres that have a predicate defined.
     */
    public Collection<Attribute> getDefinedAttributes() {
        return config.keySet();
    }

    /**
     * Gets the predicate for a given attribute
     *
     * @param attribute
     *            of the predicated
     */
    public FilterPredicate getPredicate(Attribute attribute) {
        return config.get(attribute);
    }
}
