/**
 * IDIndexedCaseBase.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 28/11/2006
 */
package jcolibri.casebase;

import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.exception.AttributeAccessException;

/**
 * This is a modification of LinealCaseBase that also keeps an index of cases
 * using their IDs. Internally it uses a hash table that relates each ID with
 * its corresponding case. It adds the method: getCase(Object ID)
 *
 * @author Juan A. Recio-Garc�a
 *
 */
public class IDIndexedLinealCaseBase implements CBRCaseBase {

    private jcolibri.cbrcore.Connector connector;
    private java.util.Collection<CBRCase> cases;
    private java.util.HashMap<Object, CBRCase> index;

    /*
     * (non-Javadoc)
     *
     * @see jcolibri.cbrcore.CBRCaseBase#close()
     */
    @Override
    public void close() {
        connector.close();

    }

    /*
     * (non-Javadoc)
     *
     * @see jcolibri.cbrcore.CBRCaseBase#forgetCases(java.util.Collection)
     */
    @Override
    public void forgetCases(Collection<CBRCase> cases) {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the case that corresponds with the id parameter.
     */
    public CBRCase getCase(Object id) {
        return index.get(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see jcolibri.cbrcore.CBRCaseBase#getCases()
     */
    @Override
    public Collection<CBRCase> getCases() {
        return cases;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jcolibri.cbrcore.CBRCaseBase#getCases(jcolibri.cbrcore.CaseBaseFilter)
     */
    @Override
    public Collection<CBRCase> getCases(CaseBaseFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see jcolibri.cbrcore.CBRCaseBase#init()
     */
    @Override
    public void init(Connector connector) {
        this.connector = connector;
        cases = this.connector.retrieveAllCases();
        indexCases(cases);

    }

    /*
     * (non-Javadoc)
     *
     * @see jcolibri.cbrcore.CBRCaseBase#learnCases(java.util.Collection)
     */
    @Override
    public void learnCases(Collection<CBRCase> cases) {
        connector.storeCases(cases);
        indexCases(cases);
        this.cases.addAll(cases);

    }

    /**
     * Private method that executes the indexing of cases.
     *
     * @param cases
     */
    private void indexCases(Collection<CBRCase> cases) {
        index = new java.util.HashMap<>();
        for (CBRCase c : cases) {
            try {
                Object o = c.getDescription().getIdAttribute().getValue(c.getDescription());
                index.put(o, c);
            } catch (AttributeAccessException e) {}
        }
    }

}
