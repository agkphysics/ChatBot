/**
 * CollaborativeRetrievalMethod.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 11/11/2007
 */
package jcolibri.extensions.recommendation.collaborative;

import java.util.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.extensions.recommendation.collaborative.MatrixCaseBase.RatingTuple;
import jcolibri.extensions.recommendation.collaborative.MatrixCaseBase.SimilarTuple;
import jcolibri.method.retrieve.RetrievalResult;

/**
 * This method returns cases depending on the recommendations of other users.
 * <br>
 * It uses a PearsonMatrix Case base to compute the similarity among neighbors.
 * Then, cases are scored according to a rating that is estimated using the
 * following formula:<br>
 * <img src="collaborativerating.jpg"/>
 * <p>
 * See:
 * <p>
 * J. Kelleher and D. Bridge. An accurate and scalable collaborative
 * recommender. Arti cial Intelligence Review, 21(3-4):193-213, 2004.
 *
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 * @see jcolibri.test.recommenders.rec12.MoviesRecommender
 */
public class CollaborativeRetrievalMethod {

    /**
     * Returns a list of cases scored following the collaborative recommendation
     * formulae.
     *
     * @param cb
     *            is the case base that contains the cases
     * @param id
     *            of the user
     * @param kItems
     *            is the number of items/ratings to return
     * @param kUsers
     *            defines the number of users taken into account to score the
     *            cases.
     */
    public static Collection<RetrievalResult> getRecommendation(PearsonMatrixCaseBase cb, CBRQuery query, int kUsers) {
        ArrayList<RetrievalResult> result = new ArrayList<>();

        int id = (Integer)query.getID();
        Collection<SimilarTuple> simil = cb.getSimilar(id);

        if (simil == null) {
            org.apache.commons.logging.LogFactory.getLog(CollaborativeRetrievalMethod.class)
                    .error("Id " + id + " does not exists");
            return result;
        }

        ArrayList<SimilarTuple> select = new ArrayList<>();
        int i = 0;
        for (Iterator<SimilarTuple> iter = simil.iterator(); (iter.hasNext() && i < kUsers); i++)
            select.add(iter.next());

        /////// debug
        System.out.println("\nQuery: " + cb.getDescription(id));
        System.out.println(cb.getRatingTuples(id).size() + " Ratings: " + cb.getRatingTuples(id));
        System.out.println("\nSimilar ratings:");
        for (SimilarTuple st : select) {
            System.out.print(st.getSimilarity() + " <--- ");
            System.out.println(cb.getDescription(st.getSimilarId()));
            System.out.println(cb.getRatingTuples(st.getSimilarId()).size() + " Ratings: "
                    + cb.getRatingTuples(st.getSimilarId()));
        }
        /////////////

        for (Integer solId : cb.getSolutions()) {
            double mean = cb.getAverage(id);
            double acum = 0;
            double simacum = 0;
            for (SimilarTuple st : select) {
                int other = st.getSimilarId();
                double rating = findRating(cb, other, solId);
                double otherMean = cb.getAverage(other);
                acum += ((rating - otherMean) * st.getSimilarity());
                simacum += st.getSimilarity();
            }
            double res = mean + (acum / simacum);

            CBRCase c = new CBRCase();
            c.setDescription(cb.getDescription(id));
            c.setSolution(cb.getSolution(solId));

            result.add(new RetrievalResult(c, res));
        }

        java.util.Collections.sort(result);

        return result;
    }

    private static double findRating(PearsonMatrixCaseBase cb, int descId, int solId) {
        for (RatingTuple rt : cb.getRatingTuples(descId)) {
            if (rt.getSolutionId() == solId) return rt.getRating();
        }
        return 0;
    }
}
