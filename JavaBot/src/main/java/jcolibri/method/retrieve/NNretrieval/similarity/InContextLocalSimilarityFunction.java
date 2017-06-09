/**
 * InContextLocalSimilarityFunction.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 26/06/2007
 */
package jcolibri.method.retrieve.NNretrieval.similarity;

import jcolibri.cbrcore.*;

/**
 * Extension of the LocalSimilarityFunction for measures that need data about
 * other attributes of the case or current CaseComponent.<br>
 * Through the inherited class-attributes subclasses can use information about
 * the "context" (other neighbour attributes) of the compared attribute.<br>
 * The context information is set by the StandardGlobalSimilarityFunction in a
 * transparent way. <br>
 * An example is
 * jcolibri.method.retrieve.NNretrieval.similarity.local.textual.LuceneTextSimilarity
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 * @see jcolibri.method.retrieve.NNretrieval.similarity.StandardGlobalSimilarityFunction
 * @see jcolibri.method.retrieve.NNretrieval.similarity.local.textual.LuceneTextSimilarity
 */
public abstract class InContextLocalSimilarityFunction implements LocalSimilarityFunction {
    /**
     * component of the case that this attribute belongs to
     */
    protected CaseComponent componentOfCase;
    /**
     * component of the query that this attribute belongs to
     */
    protected CaseComponent componentOfQuery;
    /**
     * case that this attribute belongs to
     */
    protected CBRCase _case;
    /**
     * query that this attribute belongs to
     */
    protected CBRQuery _query;
    /**
     * name of the attribute
     */
    protected String attribute;

    /**
     * Method used by the StandardGlobalSimilarityFunction (or any other future
     * implementation of the GlobalSimilarityFunction) to set the context of
     * this LocalSimilarityFunction.
     *
     * @param componentOfCase
     *            that this attribute belongs to
     * @param componentOfQuery
     *            that this attribute belongs to
     * @param _case
     *            that this attribute belongs to
     * @param _query
     *            that this attribute belongs to
     * @param attributeName
     *            is the name of the attribute
     */
    public void setContext(CaseComponent componentOfCase, CaseComponent componentOfQuery, CBRCase _case,
            CBRQuery _query, String attributeName) {
        this.componentOfCase = componentOfCase;
        this.componentOfQuery = componentOfQuery;
        this._case = _case;
        this._query = _query;
        attribute = attributeName;
    }

}
