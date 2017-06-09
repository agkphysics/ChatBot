/**
 * MatrixCaseBase.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/11/2007
 */
package jcolibri.extensions.recommendation.collaborative;

import java.util.*;

import jcolibri.cbrcore.*;
import jcolibri.exception.AttributeAccessException;
import jcolibri.util.ProgressController;

/**
 * Specific implementation of CBRCaseBase to allow collaborative
 * recommendations. These kind of recommendations are based in the past
 * experieces of other users.<br>
 * This case base manages cases composed by a description, a solution and a
 * result. The description usually contains the information about the user that
 * made the recommendation, the solution contains the information about the
 * recommended item, and the result stores the rating value for the item (the
 * value that the user assigns to an item after evaluating it).<br>
 * This way, this case base implementation stores cases as a table:<br>
 * <table border="1" cellspacing="0" cellpadding="2" width="90">
 * <tr>
 * <td></td>
 * <td>Item1</td>
 * <td>Item2</td>
 * <td>Item3</td>
 * <td>Item4</td>
 * <td>Item5</td>
 * <td>...</td>
 * <td>ItemM</td>
 * </tr>
 * <tr>
 * <td>User1</td>
 * <td></td>
 * <td>rating12</td>
 * <td></td>
 * <td>rating14</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>User2</td>
 * <td>rating21</td>
 * <td></td>
 * <td>rating23</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td>rating2N</td>
 * </tr>
 * <tr>
 * <td>User3</td>
 * <td></td>
 * <td></td>
 * <td>rating33</td>
 * <td>rating34</td>
 * <td></td>
 * <td></td>
 * <td>rating3N</td>
 * </tr>
 * <tr>
 * <td>...</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>UserN</td>
 * <td></td>
 * <td>ratingN2</td>
 * <td></td>
 * <td></td>
 * <td>ratingN5</td>
 * <td></td>
 * <td>ratingNN</td>
 * </tr>
 * </table>
 * The values of the users column and the items row are the ids of the
 * description and solution parts of the case. These ids must be integer values.
 * The ratings are obtained from an attribute of the result part. <br>
 * Note that these cases base allows having different cases with the same
 * description (because each user can make several recommendations).
 * <p>
 * Collaborative recommenders can be separated into three steps:
 * <ol>
 * <li>Weight all users with respect to similarity with the active user.
 * <li>Select a subset of users as a set of predictors.
 * <li>Normalize ratings and compute a prediction from a weighted combination of
 * selected neighbors' ratings.
 * </ol>
 * There are several ways to compute first step. And it must be implemented by
 * subclassing this class as in PearsonMatrixCaseBase. The subclasses must
 * implement the abstract methods defined here that return the similarity among
 * neighbors. The similarity among users is stored through SimilarityTuple
 * objects.
 * <p>
 * To allow an efficient comparison, ratings must be sorted by neighbors. In the
 * above table this means that cases are organized by rows. And that is done
 * internally in this class through the organizeByDescriptionId() method. Then,
 * the ratings of each user can be accessed through the getRatingTuples()
 * method. A RatingTuple object stores the id of the item and its rating (id of
 * the solution and id of the result).
 * <p>
 * There are also 2 internal classes (CommonRatingTuple and
 * CommonRatingsIterator) that allow to obtain efficiently the common ratings of
 * two users. This will be used by subclasses when computing the neighbors
 * similarity. The code of these classes is an adaptation of the one developed
 * by Jerome Kelleher and Derek Bridge for the Collaborative Movie Recommender
 * project at University College Cork (Ireland).
 * <p>
 * Recommender 12 shows how to use these classes.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 * @see jcolibri.extensions.recommendation.collaborative.PearsonMatrixCaseBase
 * @see jcolibri.test.recommenders.rec12.MoviesRecommender
 */
public abstract class MatrixCaseBase implements CBRCaseBase {

    ///////////////// ABSTRACT METHODS ///////////////////////////////

    /**
     * Stores a rating for an item.
     *
     * @author Juan A. Recio-Garcia
     * @version 1.0
     */
    public class RatingTuple implements Comparable<RatingTuple> {
        int solutionId;
        double rating;

        public RatingTuple(int solutionId, double rating) {
            super();
            this.solutionId = solutionId;
            this.rating = rating;
        }

        @Override
        public int compareTo(RatingTuple o) {
            return solutionId - o.solutionId;
        }

        public boolean equals(RatingTuple o) {
            return solutionId == o.getSolutionId();
        }

        /**
         * @return Returns the rating.
         */
        public double getRating() {
            return rating;
        }

        /**
         * @return Returns the solutionId.
         */
        public int getSolutionId() {
            return solutionId;
        }

        @Override
        public String toString() {
            return "(" + solutionId + "->" + rating + ")";
        }
    }

    /**
     * Stores the similarity among neighbors.
     *
     * @author Juan A. Recio-Garcia
     * @version 1.0
     *
     */
    public class SimilarTuple implements Comparable<Object> {
        int similarId;
        double similarity;

        public SimilarTuple(int similarId, double similarity) {
            super();
            this.similarId = similarId;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(Object o) {
            return (int)(1000 * (((SimilarTuple)o).similarity - similarity));
        }

        /**
         * @return Returns the similarId.
         */
        public int getSimilarId() {
            return similarId;
        }

        /**
         * @return Returns the similarity.
         */
        public double getSimilarity() {
            return similarity;
        }

        @Override
        public String toString() {
            return "[" + similarId + "," + similarity + "]";
        }
    }

    /**
     * Class that given two users returns an iterator over the common ratings.
     *
     *
     */
    class CommonRatingsIterator implements Iterator<Object> {
        int idA;
        int idB;
        Collection<RatingTuple> rtsA;
        Collection<RatingTuple> rtsB;
        Iterator<RatingTuple> iterA;
        Iterator<RatingTuple> iterB;

        RatingTuple rka;
        RatingTuple rkb;
        boolean moreRatings;
        CommonRatingTuple current;

        public CommonRatingsIterator(int idA, int idB, Collection<RatingTuple> rtsA, Collection<RatingTuple> rtsB) {
            super();
            this.idA = idA;
            this.idB = idB;
            this.rtsA = rtsA;
            this.rtsB = rtsB;

            // initialise the iterators over the Correlatable's ratings
            iterA = rtsA.iterator();
            iterB = rtsB.iterator();
            if (iterA.hasNext() && iterB.hasNext()) {
                rka = iterA.next();
                rkb = iterB.next();
                moreRatings = true; // MUST be before the first call to
                                    // getNextMatch!
                current = getNextMatch();
            } else {
                current = null;
                moreRatings = false;
            }
        }

        /**
         * Return true iff there are more common ratings in this
         * CommonRatingsIterator.
         *
         * @return true iff more matches exist; false otherwise.
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Return the next match in this CommonRatingsIterator. This is in the
         * form of a CommonRatingTuple. This allows both ratings concerned be
         * returned to the client code, thus saving a scan through the entire
         * ratings collection.
         *
         * @return a RatingTuple of the next two common ratings.
         * @throws NoSuchElementException
         *             if no more common ratings exist.
         */
        @Override
        public Object next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            CommonRatingTuple temp = current;
            current = getNextMatch();
            return temp;
        }

        /**
         * Throws UnsupportedOperationException if called.
         */
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("remove not supported in " + "CommonRatingsIterator");
        }

        /**
         * Return the next common rating between the two users. Return null if
         * no matches are left.
         *
         * @return a RatingTuple of the next match in the two user collections
         *         of ratings; null if no matches exist.
         */
        private CommonRatingTuple getNextMatch() {
            CommonRatingTuple match = null;
            while (moreRatings && match == null) {
                while (rka.compareTo(rkb) < 0 && moreRatings) {
                    if (iterA.hasNext()) {
                        rka = iterA.next();
                    } else {
                        moreRatings = false;
                    }
                }
                if (rka.compareTo(rkb) == 0) {
                    /*
                     * Match found.
                     */
                    match = new CommonRatingTuple(idA, rka.getRating(), idB, rkb.getRating(), rka.getSolutionId());

                    if (iterA.hasNext() && iterB.hasNext()) {
                        rka = iterA.next();
                        rkb = iterB.next();
                    } else {
                        moreRatings = false;
                    }
                }
                while (rka.compareTo(rkb) > 0 && moreRatings) {
                    if (iterB.hasNext()) {
                        rkb = iterB.next();
                    } else {
                        moreRatings = false;
                    }
                }
            }
            return match;
        }
    }

    //////////////////////////////////////////////////////////////////

    /**
     * Stores the ratings of two different users for the same item.
     *
     */
    class CommonRatingTuple {
        int id1;
        double rating1;
        int id2;
        double rating2;
        int ratedId;

        /**
         * Constructor
         *
         * @param id1
         *            of user1
         * @param rating1
         *            of user1
         * @param id2
         *            of user 2
         * @param rating2
         *            of user2
         * @param ratedId
         *            is the id of the rated object.
         */
        public CommonRatingTuple(int id1, double rating1, int id2, double rating2, int ratedId) {
            super();
            this.id1 = id1;
            this.rating1 = rating1;
            this.id2 = id2;
            this.rating2 = rating2;
            this.ratedId = ratedId;
        }

        public int getId1() {
            return id1;
        }

        public int getId2() {
            return id2;
        }

        public int getRatedId() {
            return ratedId;
        }

        public double getRating1() {
            return rating1;
        }

        public double getRating2() {
            return rating2;
        }
    }

    // Connector
    private jcolibri.cbrcore.Connector connector;

    // Cases list
    private java.util.Collection<CBRCase> cases;

    // Attribute of the result that stores the ratings
    private Attribute value;

    /**
     * Table that stores the list of ratingTuples for each description id (user)
     */
    HashMap<Integer, ArrayList<RatingTuple>> byDescriptionId;

    /** Table that organizes description components by id */
    HashMap<Integer, CaseComponent> descriptions;

    /** Table that organizes solution components by id */
    HashMap<Integer, CaseComponent> solutions;

    /**
     * Constructor. The value parameter defines the attribute of the result that
     * stores the ratings.
     *
     * @param value
     */
    public MatrixCaseBase(Attribute value) {
        this.value = value;
    }

    /**
     * Closes the case base
     */
    @Override
    public void close() {
        connector.close();
    }

    /**
     * Forgets cases. It does nothing in this implementation
     */
    @Override
    public void forgetCases(Collection<CBRCase> cases) {}

    /**
     * Returns the stored cases
     */
    @Override
    public Collection<CBRCase> getCases() {
        return cases;
    }

    /**
     * Returns selected cases. It does nothing in this implementation.
     */
    @Override
    public Collection<CBRCase> getCases(CaseBaseFilter filter) {
        return null;
    }

    /**
     * Utility method to obtain the description object given its id.
     *
     * @param id
     *            of the description object
     * @return the description object
     */
    public CaseComponent getDescription(Integer id) {
        return descriptions.get(id);
    }

    /**
     * Utility method to obtain all the descriptions stored in this case base
     *
     * @return a set of ids.
     */
    public Set<Integer> getDescriptions() {
        return descriptions.keySet();
    }

    /**
     * Returns the ratings that a user has done.
     *
     * @param descriptionId
     *            of the user
     * @return collection of rating tuples (solution id + rating value)
     */
    public Collection<RatingTuple> getRatingTuples(int descriptionId) {
        return byDescriptionId.get(descriptionId);
    }

    /**
     * Returns the similarity of two neighbors.
     *
     * @param id1
     *            of the first neighbor
     * @param id2
     *            of the second neighbor.
     * @return a double between 0 and 1
     */
    public abstract double getSimil(Integer id1, Integer id2);

    /**
     * Returns the list of similar neighbors for a given one
     *
     * @param id
     *            of the neighbor
     * @return a list of SimilarTuple (other neighbor id + similarity value).
     */
    public abstract Collection<SimilarTuple> getSimilar(Integer id);

    /**
     * Utility method to obtain the solution object given its id.
     *
     * @param id
     *            of the solution object
     * @return the solution object
     */
    public CaseComponent getSolution(Integer id) {
        return solutions.get(id);
    }

    /**
     * Utility method to obtain all the solutions stored in this case base
     *
     * @return a set of ids.
     */
    public Set<Integer> getSolutions() {
        return solutions.keySet();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //////////// Internal Classes //////////////////
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initializes the case base. Organizes ratings by the description id of the
     * cases and calls the computeSimilarities() abstract method.
     */
    @Override
    public void init(Connector connector) {
        this.connector = connector;
        ProgressController.init(MatrixCaseBase.class, "Organizing Collaborative Case Base",
                ProgressController.UNKNOWN_STEPS);
        org.apache.commons.logging.LogFactory.getLog(this.getClass()).info("Loading cases from connector");
        cases = this.connector.retrieveAllCases();
        organizeByDescriptionId();
        computeSimilarities();
        ProgressController.finish(MatrixCaseBase.class);
    }

    /**
     * Adds new cases to the case base, reorganizing the cases base and
     * re-computing neighbors similarities.
     */
    @Override
    public void learnCases(Collection<CBRCase> cases) {
        this.cases.addAll(cases);
        connector.storeCases(cases);
        organizeByDescriptionId();
        computeSimilarities();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Following code is an adaptation of the Collaborative Movie Recommender //
    // Project implemented by Jerome Kelleher at University College Cork
    /////////////////////////////////////////////////////////////////////////////////// (Ireland)
    /////////////////////////////////////////////////////////////////////////////////// //
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * Computes the similarity of neighbors (each neighbor is defined by the
     * description)
     */
    protected abstract void computeSimilarities();

    /**
     * Organizes cases by the description id.
     */
    private void organizeByDescriptionId() {
        org.apache.commons.logging.LogFactory.getLog(this.getClass()).info("Organizing cases");
        try {
            byDescriptionId = new HashMap<>();
            descriptions = new HashMap<>();
            solutions = new HashMap<>();
            for (CBRCase c : cases) {
                Integer descId = (Integer)c.getID();
                ArrayList<RatingTuple> list = byDescriptionId.get(descId);
                if (list == null) {
                    list = new ArrayList<>();
                    byDescriptionId.put(descId, list);
                    descriptions.put(descId, c.getDescription());
                }
                Integer solId = (Integer)c.getSolution().getIdAttribute().getValue(c.getSolution());
                solutions.put(solId, c.getSolution());
                Number resId = (Number)value.getValue(c.getResult());
                list.add(new RatingTuple(solId, resId.doubleValue()));
                ProgressController.step(MatrixCaseBase.class);

            }
            for (ArrayList<RatingTuple> list : byDescriptionId.values())
                java.util.Collections.sort(list);
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass()).error(e);

        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////

}
