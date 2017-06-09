package jcolibri.extensions.maintenance_evaluation;

import org.apache.commons.logging.LogFactory;

import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.evaluation.Evaluator;
import jcolibri.exception.ExecutionException;
import jcolibri.method.maintenance.AbstractCaseBaseEditMethod;
import jcolibri.method.reuse.classification.KNNClassificationConfig;

/**
 * This abstract class defines the common behaviour of a maintenance evaluator.
 *
 * @author Lisa Cummins.
 */
public abstract class MaintenanceEvaluator extends Evaluator {
    /**
     * The label for the percentage reduction in the case-base after maintenance
     * is performed.
     */
    public static final String PERCENT_REDUCED = "Percent reduced";
    protected StandardCBRApplication app;
    protected AbstractCaseBaseEditMethod editMethod = null;

    protected KNNClassificationConfig simConfig = null;

    /**
     * Initialise this evaluator with the CBR application to evaluate.
     *
     * @param cbrApp
     *            the CBR application that this evaluator will use.
     */
    @Override
    public void init(StandardCBRApplication cbrApp) {
        if (report == null || !(report instanceof DetailedEvaluationReport)) {
            report = new DetailedEvaluationReport();
        }

        app = cbrApp;
        try {
            app.configure();
        } catch (ExecutionException e) {
            LogFactory.getLog(this.getClass()).error(e);
        }
    }

    /**
     * Initialise this evaluator with the CBR application to evaluate and the
     * edit method and similarity configuration to perform maintenance.
     *
     * @param cbrApp
     *            the CBR application that this evaluator will use.
     * @param editMethod
     *            the maintenance algorithm to use.
     * @param simConfig
     *            the similarity configuration to use.
     */
    public void init(StandardCBRApplication cbrApp, AbstractCaseBaseEditMethod editMethod,
            KNNClassificationConfig simConfig) {
        this.init(cbrApp);
        setEditMethod(editMethod);
        setSimConfig(simConfig);
    }

    /**
     * Sets the edit method to be the given edit method.
     *
     * @param editMethod
     *            the edit method to set.
     */
    public void setEditMethod(AbstractCaseBaseEditMethod editMethod) {
        this.editMethod = editMethod;
    }

    /**
     * Sets the similarity configuration to be the given similarity
     * configuration.
     *
     * @param simConfig
     *            the similarity configuration to set.
     */
    public void setSimConfig(KNNClassificationConfig simConfig) {
        this.simConfig = simConfig;
    }

    /**
     * Edit the case base and store the percentage reduction in the report.
     *
     * @param caseBase
     *            the case base to be edited.
     */
    protected void editCaseBase(CBRCaseBase caseBase) { // Perform maintenance
                                                        // on this case base
        int sizeBefore = caseBase.getCases().size();
        LogFactory.getLog(this.getClass()).info("Editing Case-Base");
        editMethod.edit(caseBase, simConfig);
        LogFactory.getLog(this.getClass()).info("Finished Editing Case-Base");
        int sizeAfter = caseBase.getCases().size();
        double percentReduced = ((sizeBefore - sizeAfter) / (double)sizeBefore) * 100.0;
        report.addDataToSeries(PERCENT_REDUCED, percentReduced);
    }
}
