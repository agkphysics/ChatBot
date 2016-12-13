/**
 * 
 */
package agk.chatbot;

import jcolibri.datatypes.Text;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.extensions.textual.IE.common.ThesaurusLinker;
import jcolibri.extensions.textual.IE.representation.IEText;
import jcolibri.extensions.textual.IE.representation.Token;
import jcolibri.extensions.textual.IE.representation.info.WeightedRelation;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * @author Aaron
 *
 */
public class TextSimilarity implements LocalSimilarityFunction {

    /**
     * 
     */
    public TextSimilarity() {}

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
        
        // Convert to IEText
        IEText caseText = (IEText)caseObject;
        IEText queryText = (IEText)queryObject;
        double sum = 0.0;
        
        ThesaurusLinker.linkWithWordNet(caseText, queryText);
        for (Token cTok : caseText.getAllTokens()) {
            for (WeightedRelation rel : cTok.getRelations()) {
                sum += rel.getWeight();
            }
        }
        
        return 0;
    }

    /* (non-Javadoc)
     * @see jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#isApplicable(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isApplicable(Object caseObject, Object queryObject) {
        return caseObject instanceof IEText || queryObject instanceof IEText;
    }

}
