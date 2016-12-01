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
public class HammingDistance implements LocalSimilarityFunction {

    /**
     *
     */
    public HammingDistance() {}

    /*
     * (non-Javadoc)
     * 
     * @see
     * jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#
     * compute(java.lang.Object, java.lang.Object)
     */
    @Override
    public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
        if (!isApplicable(caseObject, queryObject)) {
            throw new NoApplicableSimilarityFunctionException("Cannot compare strings with type "
                    + caseObject.getClass().getName() + " and " + queryObject.getClass().getName());
        }
        else if (caseObject == null || queryObject == null) return 0.0;
        
        // Convert to strings
        String caseString, queryString;
        if (caseObject instanceof Text) caseString = ((Text)caseObject).toString();
        else caseString = (String)caseObject;
        if (queryObject instanceof Text) queryString = ((Text)queryObject).toString();
        else queryString = (String)queryObject;
        
        if (caseString.length() == 0 || queryString.length() == 0) return 0.0;
        
        int dist = 0;
        int minLength = Math.min(caseString.length(), queryString.length());
        int maxLength = Math.max(caseString.length(), queryString.length());
        
        for (int i = 0; i < minLength; i++) {
            if (caseString.charAt(i) != queryString.charAt(i)) dist++;
        }
        dist += maxLength - minLength;
        
        return 1 - (double)dist / (double)maxLength;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#
     * isApplicable(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isApplicable(Object caseObject, Object queryObject) {
        return (caseObject instanceof String || caseObject instanceof Text) || (queryObject instanceof String || queryObject instanceof Text);
    }

}
