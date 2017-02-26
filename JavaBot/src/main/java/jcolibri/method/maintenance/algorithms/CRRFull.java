package jcolibri.method.maintenance.algorithms;

import jcolibri.method.maintenance.AbstractCaseBaseEditMethod;
import jcolibri.method.maintenance.TwoStepCaseBaseEditMethod;

/**
 * Provides the ability to run the full CRR Maintenance algorithm, which
 * consists of running BBNR to remove noise followed by the CRR redundancy
 * removal.
 *
 * @author Lisa Cummins
 */
public class CRRFull extends TwoStepCaseBaseEditMethod {

    /**
     * Sets up the edit method using BBNR noise removal and CRR redundancy
     * removal.
     *
     * @param method1
     *            The first method to run.
     * @param method2
     *            The second method to run.
     */
    public CRRFull(AbstractCaseBaseEditMethod method1, AbstractCaseBaseEditMethod method2) {
        super(method1, method2);
        this.method1 = new BBNRNoiseReduction();
        this.method2 = new CRRRedundancyRemoval();
    }
}
