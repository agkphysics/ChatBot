package jcolibri.method.maintenance;

import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.method.reuse.classification.KNNClassificationConfig;

/**
 * Provides the ability to run two case base editing algorithms consecutively on
 * a set of cases. This is useful to run, for example, noise elimintaion prior
 * to redundancy removal.
 *
 * @author Lisa Cummins
 */
public class TwoStepCaseBaseEditMethod extends AbstractCaseBaseEditMethod {
    protected AbstractCaseBaseEditMethod method1;
    protected AbstractCaseBaseEditMethod method2;

    /**
     * Sets up the edit method using the two provided methods.
     *
     * @param method1
     *            The first method to run.
     * @param method2
     *            The second method to run.
     */
    public TwoStepCaseBaseEditMethod(AbstractCaseBaseEditMethod method1, AbstractCaseBaseEditMethod method2) {
        this.method1 = method1;
        this.method2 = method2;
    }

    /**
     * Runs alg1 followed by alg2 on the given cases and returns the cases
     * deleted by the combined algorithms
     *
     * @param cases
     *            The group of cases on which to perform maintenance
     * @param simConfig
     *            The KNNConfig for these cases
     * @return the list of cases deleted by the algorithm
     */
    @Override
    public Collection<CBRCase> retrieveCasesToDelete(Collection<CBRCase> cases, KNNClassificationConfig simConfig) {
        Collection<CBRCase> deletedCases = method1.retrieveCasesToDelete(cases, simConfig);
        System.out.println(method1.getClass().getName() + " Done, Deleted: " + deletedCases.size());
        System.out.println();
        for (CBRCase c : deletedCases)
            System.out.println(c.getID());
        cases.removeAll(deletedCases);

        deletedCases.addAll(method2.retrieveCasesToDelete(cases, simConfig));
        System.out.println(method2.getClass().getName() + " Done, Deleted: " + deletedCases.size());

        return deletedCases;
    }
}
