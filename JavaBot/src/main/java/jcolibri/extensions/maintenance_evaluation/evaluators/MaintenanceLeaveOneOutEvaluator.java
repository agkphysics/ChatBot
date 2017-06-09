package jcolibri.extensions.maintenance_evaluation.evaluators;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.LogFactory;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.exception.ExecutionException;
import jcolibri.extensions.maintenance_evaluation.MaintenanceEvaluator;

/**
 * This evalutation takes each case in turn to be the query. It maintains the
 * case-base (the remaining cases) and then uses that as a training set to
 * evaluate the query.
 *
 * @author Lisa Cummins.
 * @author Juan A. Recio Garc�a - GAIA http://gaia.fdi.ucm.es
 */
public class MaintenanceLeaveOneOutEvaluator extends MaintenanceEvaluator {
    /**
     * Performs the Leave-One-Out evaluation. For each case in the case base,
     * remove that case from the case base, maintain the case-base and the use
     * the case as a query for that cycle.
     */
    public void LeaveOneOut() {
        try {
            java.util.ArrayList<CBRCase> aux = new java.util.ArrayList<>();

            long t = (new Date()).getTime();
            int numberOfCycles = 0;

            // Run the precycle to load the case base
            LogFactory.getLog(this.getClass()).info("Running precycle()");
            CBRCaseBase caseBase = app.preCycle();

            if (!(caseBase instanceof jcolibri.casebase.CachedLinealCaseBase))
                LogFactory.getLog(this.getClass()).warn("Evaluation should be executed using a cached case base");

            prepareCases(caseBase);

            ArrayList<CBRCase> cases = new ArrayList<>(caseBase.getCases());

            jcolibri.util.ProgressController.init(getClass(), "LeaveOneOut Evaluation", cases.size());

            // For each case in the case base
            for (CBRCase _case : cases) { // Delete the case in the case base
                aux.clear();
                aux.add(_case);
                caseBase.forgetCases(aux);

                // Run the cycle
                LogFactory.getLog(this.getClass()).info("Running cycle() " + numberOfCycles);

                app.cycle(_case);

                // Recover case base
                caseBase.learnCases(aux);

                numberOfCycles++;
                jcolibri.util.ProgressController.step(getClass());
            }

            // Run PostCycle
            LogFactory.getLog(this.getClass()).info("Running postcycle()");
            app.postCycle();

            jcolibri.util.ProgressController.finish(getClass());

            t = (new Date()).getTime() - t;

            // complete evaluation report
            report.setTotalTime(t);
            report.setNumberOfCycles(numberOfCycles);

        } catch (ExecutionException e) {
            LogFactory.getLog(this.getClass()).error(e);
        }
    }

    /**
     * Prepares the cases for evaluation
     *
     * @param caseBase
     *            the case base
     */
    protected void prepareCases(CBRCaseBase caseBase) {
        if (simConfig != null && editMethod != null) { // Perform
                                                       // maintenance
                                                       // on this case
                                                       // base
            editCaseBase(caseBase);
        }
    }
}
