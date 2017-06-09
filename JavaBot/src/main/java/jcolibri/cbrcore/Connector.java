/**
 * Connector.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 05/01/2007
 */
package jcolibri.cbrcore;

import java.util.Collection;

import jcolibri.exception.InitializingException;

/**
 * Connector interface declares the methods required to access the cases stored
 * in a persistence media. jCOLIBRI splits the managing of cases into
 * persistence media and in-memory organization. This interface defines the
 * access to de persistence and the CBRCaseBase interface defines the in-memory
 * organization. Both interfaces are related as the CBRCaseBase manages the
 * Connector.
 *
 * Implementations should read/write cases from Data Bases, Plain Text files,
 * Ontologies, XML files, etc.
 *
 * @author Juan A. Recio-Garc�a
 * @see jcolibri.cbrcore.CBRCaseBase
 */
public interface Connector {

    /**
     * Cleanup any resource that the connector might be using, and suspends the
     * service
     *
     */
    public void close();

    /**
     * Deletes given cases for the storage media
     *
     * @param cases
     *            List of cases
     */
    public void deleteCases(Collection<CBRCase> cases);

    /**
     * Initialices the connector with the given XML file
     *
     * @param file
     *            XMl file with the settings
     * @throws InitializingException
     *             Raised if the connector can not be initialezed.
     */
    public void initFromXMLfile(java.net.URL file) throws InitializingException;

    /**
     * Returns max cases without any special consideration
     *
     * @return The list of retrieved cases
     */
    public Collection<CBRCase> retrieveAllCases();

    /**
     * Retrieves some cases depending on the filter. TODO.
     */
    public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter filter);

    /**
     * Stores given classes on the storage media
     *
     * @param cases
     *            List of cases
     */
    public void storeCases(Collection<CBRCase> cases);

}
