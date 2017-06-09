/**
 * CachedLinealCaseBase.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/05/2007
 */

package jcolibri.casebase;

import java.util.Collection;

import jcolibri.cbrcore.*;
import jcolibri.exception.InitializingException;

/**
 * Cached case base that only persists cases when closing. learn() and forget()
 * are not synchronized with the persistence until close() is invoked.
 * <p>
 * This class presents better performance that LinelCaseBase as only access to
 * the persistence once. This case base is used for evaluation.
 *
 * @author Juan A. Recio-Garc�a
 * @see jcolibri.casebase.LinealCaseBase
 */
public class CachedLinealCaseBase implements CBRCaseBase {

    private jcolibri.cbrcore.Connector connector;
    private java.util.Collection<CBRCase> originalCases;
    private java.util.Collection<CBRCase> workingCases;

    /**
     * Closes the case base saving or deleting the cases of the persistence
     * media
     */
    @Override
    public void close() {
        java.util.ArrayList<CBRCase> casestoRemove = new java.util.ArrayList<>(originalCases);
        casestoRemove.removeAll(workingCases);
        org.apache.commons.logging.LogFactory.getLog(this.getClass())
                .info("Deleting " + casestoRemove.size() + " cases from storage media");
        connector.deleteCases(casestoRemove);

        java.util.ArrayList<CBRCase> casestoStore = new java.util.ArrayList<>(workingCases);
        casestoStore.removeAll(originalCases);
        org.apache.commons.logging.LogFactory.getLog(this.getClass())
                .info("Storing " + casestoStore.size() + " cases into storage media");

        connector.storeCases(casestoStore);

        connector.close();

    }

    /**
     * Forgets cases. It only removes the cases from the storage media when
     * closing.
     */
    @Override
    public void forgetCases(Collection<CBRCase> cases) {
        workingCases.removeAll(cases);

    }

    /**
     * Returns working cases.
     */
    @Override
    public Collection<CBRCase> getCases() {
        return workingCases;
    }

    /**
     * TODO.
     */
    @Override
    public Collection<CBRCase> getCases(CaseBaseFilter filter) {
        // TODO
        return null;
    }

    /**
     * Initializes the Case Base with the cases read from the given connector.
     */
    @Override
    public void init(Connector connector) throws InitializingException {
        this.connector = connector;
        originalCases = this.connector.retrieveAllCases();
        workingCases = new java.util.ArrayList<>(originalCases);
    }

    /**
     * Learns cases that are only saved when closing the Case Base.
     */
    @Override
    public void learnCases(Collection<CBRCase> cases) {
        workingCases.addAll(cases);

    }

}
