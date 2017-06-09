/**
 * TextualSimUtils.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 26/06/2007
 */
package jcolibri.method.retrieve.NNretrieval.similarity.local.textual;

import java.util.Collection;
import java.util.Set;

import jcolibri.extensions.textual.IE.representation.Token;
import jcolibri.extensions.textual.IE.representation.info.WeightedRelation;

/**
 * Utilities to compute textual similarities
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 *
 */
public class TextualSimUtils {
    /**
     * Represents a string with an asssociated weight.
     *
     * @author Juan A. Recio-Garcia
     * @version 1.0
     *
     */
    public static class WeightedString {
        String string;
        double weight;

        public WeightedString(String string, double weight) {
            super();
            this.string = string;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof WeightedString)) return false;
            else {
                WeightedString ws = (WeightedString)o;
                return string.equals(ws.getString());
            }
        }

        /**
         * @return Returns the string.
         */
        public String getString() {
            return string;
        }

        /**
         * @return Returns the weight.
         */
        public double getWeight() {
            return weight;
        }

        @Override
        public int hashCode() {
            return string.hashCode();
        }

    }

    /**
     * Expands the tokens collections recived containing the tokens of a case
     * and a query. The expansion means that new tokens are added to the
     * returned sets depending on the WeightedRelations between the tokens of
     * the case and the query
     *
     * @param caseTokens
     *            Input tokens of the case
     * @param queryToken
     *            Input tokens of the query
     * @param caseSet
     *            Output set containing the tokens of the case represented in
     *            WeightedString objects that contain a string (the token) and a
     *            weight
     * @param querySet
     *            Output set containing the tokens of the query represented in
     *            WeightedString objects that contain a string (the token) and a
     *            weight
     */
    public static void expandTokensSet(Collection<Token> caseTokens, Collection<Token> queryToken,
            Set<WeightedString> caseSet, Set<WeightedString> querySet) {
        caseSet.clear();
        querySet.clear();
        for (Token qTok : queryToken) {
            if (qTok.isStopWord()) continue;
            for (WeightedRelation rel : qTok.getRelations()) {
                Token destToken = rel.getDestination();
                if (caseTokens.contains(destToken)) {
                    String newStem = qTok.getStem() + "_RELATED_" + destToken.getStem();
                    WeightedString ws = new WeightedString(newStem, rel.getWeight());
                    caseSet.add(ws);
                    querySet.add(ws);
                }
            }
            querySet.add(new WeightedString(qTok.getStem(), 1));
        }
        for (Token cTok : caseTokens)
            if (!cTok.isStopWord()) caseSet.add(new WeightedString(cTok.getStem(), 1));
    }

    /**
     * Returns the "size" of a collection having into account that each
     * WeightedString object has an associated weight.
     */
    public static double getSize(Collection<WeightedString> col) {
        double res = 0;
        for (WeightedString ws : col)
            res += ws.getWeight();
        return res;
    }
}
