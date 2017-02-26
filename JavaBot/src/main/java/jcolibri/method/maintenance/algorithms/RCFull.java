package jcolibri.method.maintenance.algorithms;

import jcolibri.method.maintenance.AbstractCaseBaseEditMethod;
import jcolibri.method.maintenance.TwoStepCaseBaseEditMethod;

/**
 * Provides the ability to run the full RC Maintenance algorithm, which consists
 * of running RENN to remove noise followed by the RC redundancy removal.
 *
 * @author Lisa Cummins
 */
public class RCFull extends TwoStepCaseBaseEditMethod {

    /**
     * Sets up the edit method using RENN noise removal and RC redundancy
     * removal.
     *
     * @param method1
     *            The first method to run.
     * @param method2
     *            The second method to run.
     */
    public RCFull(AbstractCaseBaseEditMethod method1, AbstractCaseBaseEditMethod method2) {
        super(method1, method2);
        this.method1 = new RENNNoiseReduction();
        this.method2 = new RCRedundancyRemoval();
    }
}
