/**
 * 
 */
package agk.chatbot;

import jcolibri.datatypes.Text;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * @author Aaron
 *
 */
public class LevenshteinDistance implements LocalSimilarityFunction {

    /**
     * 
     */
    public LevenshteinDistance() {}

    /* (non-Javadoc)
     * @see jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#compute(java.lang.Object, java.lang.Object)
     */
    @Override
    public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
        if (!isApplicable(caseObject, queryObject)) {
            throw new NoApplicableSimilarityFunctionException("Cannot compare strings with type "
                    + caseObject.getClass().getName() + " and " + queryObject.getClass().getName());
        }
        else if (caseObject == null || queryObject == null) return 0.0;
        
        String caseString, queryString;
        if (caseObject instanceof Text) caseString = ((Text)caseObject).toString();
        else caseString = (String)caseObject;
        if (queryObject instanceof Text) queryString = ((Text)queryObject).toString();
        else queryString = (String)queryObject;
        
        if (caseString.length() == 0 || queryString.length() == 0) return 0.0;
        
        int[][] M = new int[caseString.length()][queryString.length()];
        char[] caseChars = caseString.toCharArray();
        char[] queryChars = queryString.toCharArray();
        
        for (int i = 0; i < caseString.length(); i++) {
            for (int j = 0; j < queryString.length(); j++) {
                if (i == 0 || j == 0) M[i][j] = Math.max(i, j);
                else {
                    int same;
                    if (caseChars[i] == queryChars[j]) same = 0;
                    else same = 1;
                    M[i][j] = Math.min(M[i-1][j-1] + same, Math.min(M[i-1][j] + 1, M[i][j-1] + 1));
                }
            }
        }
        
        return 1 - (double)M[caseString.length()-1][queryString.length()-1] / (double)Math.max(caseString.length(), queryString.length());
    }

    /* (non-Javadoc)
     * @see jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#isApplicable(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isApplicable(Object caseObject, Object queryObject) {
        return (caseObject instanceof String || caseObject instanceof Text) || (queryObject instanceof String || queryObject instanceof Text);
    }

}
