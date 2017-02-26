/**
 * CDRSet.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 21/11/2007
 */
package jcolibri.method.retrieve.selection.compromiseDriven;

import java.util.*;

import jcolibri.cbrcore.CBRCase;

/**
 * Stores the retrieved cases and their "like" and "covered" sets.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class CDRSet extends ArrayList<CBRCase> {
    private static final long serialVersionUID = 1L;

    private Hashtable<CBRCase, HashSet<CBRCase>> likeSets;
    private Hashtable<CBRCase, HashSet<CBRCase>> coveredSets;

    /**
     * Constructor
     */
    public CDRSet() {
        likeSets = new Hashtable<>();
        coveredSets = new Hashtable<>();
    }

    /**
     * Adds a case to the covered set of another case
     */
    public void addToCoveredSet(CBRCase _case, CBRCase coveredCase) {
        HashSet<CBRCase> covered = coveredSets.get(_case);
        if (covered == null) {
            covered = new HashSet<>();
            coveredSets.put(_case, covered);
        }
        covered.add(coveredCase);
    }

    /**
     * Adds a case to the like set of another case
     */
    public void addToLikeSet(CBRCase _case, CBRCase likeCase) {
        HashSet<CBRCase> like = likeSets.get(_case);
        if (like == null) {
            like = new HashSet<>();
            likeSets.put(_case, like);
        }
        like.add(likeCase);
    }

    /**
     * Returns the covered set of a case
     */
    public Set<CBRCase> getCoveredSet(CBRCase _case) {
        return coveredSets.get(_case);
    }

    /**
     * Returns the like set of a case
     */
    public Set<CBRCase> getLikeSet(CBRCase _case) {
        return likeSets.get(_case);
    }
}
