/**
 * CritiqueOption.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 31/10/2007
 */
package jcolibri.extensions.recommendation.navigationByProposing;

import jcolibri.cbrcore.Attribute;
import jcolibri.method.retrieve.FilterBasedRetrieval.predicates.FilterPredicate;

/**
 * Utility object to encapsulte information about the user's critique to an
 * attribute. It stores: the critiqued attribute, the label shown to the user
 * and the FilterPredicate that implements the critique.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 * @see jcolibri.method.retrieve.FilterBasedRetrieval.predicates.FilterPredicate
 */
public class CritiqueOption {
    String label;
    Attribute attribute;
    FilterPredicate predicate;

    /**
     * Creates a new critiqueOption
     *
     * @param label
     * @param attribute
     * @param predicate
     */
    public CritiqueOption(String label, Attribute attribute, FilterPredicate predicate) {
        super();
        this.label = label;
        this.attribute = attribute;
        this.predicate = predicate;
    }

    /**
     * @return Returns the attribute.
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return Returns the predicate.
     */
    public FilterPredicate getPredicate() {
        return predicate;
    }

    /**
     * @param attribute
     *            The attribute to set.
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @param label
     *            The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @param predicate
     *            The predicate to set.
     */
    public void setPredicate(FilterPredicate predicate) {
        this.predicate = predicate;
    }

}
