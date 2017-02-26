/**
 * WeightedRelation.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 15/06/2007
 */
package jcolibri.extensions.textual.IE.representation.info;

import jcolibri.extensions.textual.IE.representation.Token;

/**
 * <p>
 * This class represents a weighted relation between two tokens.
 * </p>
 * <p>
 * First version developed at: Robert Gordon University - Aberdeen & Facultad
 * Inform�tica, Universidad Complutense de Madrid (GAIA)
 * </p>
 *
 * @author Juan Antonio Recio Garc�a
 * @version 2.0
 */
public class WeightedRelation {

    Token _origin;

    Token _destination;

    double _weight;

    /**
     * Constructor
     *
     * @param origin
     *            Origin Token
     * @param destination
     *            Destination Token
     * @param weight
     *            Relation weight [0..1]
     */
    public WeightedRelation(Token origin, Token destination, double weight) {
        _origin = origin;
        _destination = destination;
        _weight = weight;
    }

    /**
     * Returns the destination token.
     */
    public Token getDestination() {
        return _destination;
    }

    /**
     * Returns the origin token.
     */
    public Token getOrigin() {
        return _origin;
    }

    /**
     * Returns the relation weight.
     */
    public double getWeight() {
        return _weight;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String res = "Relation: ";
        res += _origin + " --" + _weight + "--> " + _destination;
        return res;
    }
}
