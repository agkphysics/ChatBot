/**
 * PearsonMatrixCaseBase.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/11/2007
 */
package jcolibri.extensions.recommendation.collaborative;

import java.util.*;
import java.util.Map.Entry;

import jcolibri.cbrcore.Attribute;

/**
 * Extension of the MatrixCaseBase that computes similarities among neighbors
 * using the Pearson Correlation. <br>
 * It uses a minCorrelateItems Factor to weight similar neighbors that have few
 * common correlate items.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 * @see jcolibri.test.recommenders.rec12.MoviesRecommender
 */
public class PearsonMatrixCaseBase extends MatrixCaseBase {
    /**
     * Factor that decreases the similarity between users who have fewer than
     * this number of co-rated items
     */
    int minCorrelateItemsFactor;

    // stores the similarity lists
    private HashMap<Integer, Collection<SimilarTuple>> similLists;

    // table to store the similarities
    private HashMap<Integer, HashMap<Integer, Double>> similarities;

    // stores the averages
    private HashMap<Integer, Double> averages;

    /**
     * Return the square of the parameter number.
     *
     * @param n
     *            the number to be squared.
     * @return n*n.
     */
    private static double square(double n) {
        return n * n;
    }

    /**
     * Constructor
     *
     * @param value
     *            is the attribute of the result part of the case that contains
     *            the rating
     * @param minCorrelateItemsFactor
     *            factor that decreases the similarity between users who have
     *            fewer than this number of co-rated items
     */
    public PearsonMatrixCaseBase(Attribute value, int minCorrelateItemsFactor) {
        super(value);
        this.minCorrelateItemsFactor = minCorrelateItemsFactor;
    }

    /**
     * returns the ratings average for a given user
     *
     * @param id
     *            is the user
     * @return the ratings average
     */
    public double getAverage(int id) {
        return averages.get(id);
    }

    @Override
    /**
     * Returns the similarity between two users
     */
    public double getSimil(Integer id1, Integer id2) {
        return similarities.get(id1).get(id2);
    }

    @Override
    /**
     * Returns a list of similar users to a given one in decreasing order
     */
    public Collection<SimilarTuple> getSimilar(Integer id) {
        return similLists.get(id);
    }

    @Override
    /**
     * Computes the similarity between users
     */
    protected void computeSimilarities() {
        computeAverages();
        computeSimilarityByDescriptionId();
        computeSimilarityLists();
    }

    // computes the averages
    private void computeAverages() {
        org.apache.commons.logging.LogFactory.getLog(this.getClass()).info("Computing Averages");
        averages = new HashMap<>();
        for (Integer i : byDescriptionId.keySet()) {
            ArrayList<RatingTuple> list = byDescriptionId.get(i);
            double acum = 0;
            for (RatingTuple rt : list)
                acum += rt.getRating();
            double size = list.size();
            averages.put(i, acum / size);
        }
    }

    /**
     * Computes the Pearson Correlation between users in a smart and efficient
     * way. This code is an adaptation of the one developed by Jerome Kelleher
     * and Derek Bridge for the Collaborative Movie Recommender project at
     * University College Cork (Ireland).
     */
    private void computeSimilarityByDescriptionId() {
        org.apache.commons.logging.LogFactory.getLog(this.getClass()).info("Computing similarities");
        similarities = new HashMap<>();
        HashSet<Integer> keyCopy = new HashSet<>(byDescriptionId.keySet());
        for (Integer me : byDescriptionId.keySet()) {
            keyCopy.remove(me);
            for (Integer you : keyCopy) {

                Iterator<?> ratings = new CommonRatingsIterator(me, you, byDescriptionId.get(me),
                        byDescriptionId.get(you));
                double sumX = 0.0;
                double sumXSquared = 0.0;
                double sumY = 0.0;
                double sumYSquared = 0.0;
                double sumXY = 0.0;
                double numDataPoints = 0;
                // X corresponds to active, Y to predictor.
                while (ratings.hasNext()) {
                    CommonRatingTuple rt = (CommonRatingTuple)ratings.next();
                    double x = rt.getRating1();
                    double y = rt.getRating2();
                    numDataPoints++;
                    sumX += x;
                    sumY += y;
                    sumXSquared += square(x);
                    sumYSquared += square(y);
                    sumXY += (x * y);
                }
                // update AbstractMovieRecommender by the correct comparison
                // count.
                // Modified to remove comparisons required for search
                // AbstractMovieRecommender.addToComparisonCount(numDataPoints);

                double correlation = 0.0;
                if (numDataPoints != 0) {
                    double numerator = sumXY - ((sumX * sumY) / numDataPoints);
                    double sqrt = (sumXSquared - (square(sumX) / numDataPoints))
                            * (sumYSquared - (square(sumY) / numDataPoints));
                    double denominator = Math.sqrt(sqrt);

                    // output 0 here according to Herlocker's recommendations,
                    // also watch for negative square roots (extremely rare)
                    correlation = denominator == 0.0 || sqrt < 0.0 ? 0.0 : numerator / denominator;
                    correlation = correlation * numDataPoints / minCorrelateItemsFactor;
                }

                HashMap<Integer, Double> mySimilList = similarities.get(me);
                if (mySimilList == null) {
                    mySimilList = new HashMap<>();
                    similarities.put(me, mySimilList);
                }
                mySimilList.put(you, correlation);

                HashMap<Integer, Double> yourSimilList = similarities.get(you);
                if (yourSimilList == null) {
                    yourSimilList = new HashMap<>();
                    similarities.put(you, yourSimilList);
                }
                yourSimilList.put(me, correlation);

            }
        }
    }

    private void computeSimilarityLists() {
        similLists = new HashMap<>();
        for (Integer key : similarities.keySet()) {
            ArrayList<SimilarTuple> list = new ArrayList<>();
            HashMap<Integer, Double> similMap = similarities.get(key);
            for (Entry<Integer, Double> entry : similMap.entrySet())
                list.add(new SimilarTuple(entry.getKey(), entry.getValue()));
            java.util.Collections.sort(list);
            similLists.put(key, list);
        }
    }

}
