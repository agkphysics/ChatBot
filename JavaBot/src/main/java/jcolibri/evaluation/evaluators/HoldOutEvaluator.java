/**
 * HoldOutEvaluator.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 07/05/2007
 */
package jcolibri.evaluation.evaluators;

import java.util.*;

import org.apache.commons.logging.LogFactory;

import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.evaluation.EvaluationReport;
import jcolibri.evaluation.Evaluator;
import jcolibri.exception.ExecutionException;

/**
 * This method splits the case base in two sets: one used for testing where each
 * case is used as query, and another that acts as normal case base. This
 * process is performed serveral times.
 *
 * @author Juan A. Recio Garc�a - GAIA http://gaia.fdi.ucm.es
 * @version 2.0
 */
public class HoldOutEvaluator extends Evaluator {

    protected StandardCBRApplication app;

    /**
     * Performs the Hold-Out evaluation.
     *
     * @param testPercent
     *            Percent of the case base used as queries. The case base is
     *            splited randomly in each repetition.
     * @param repetitions
     *            Number of repetitions.
     */
    public void HoldOut(int testPercent, int repetitions) {
        try {
            // Obtain the time
            long t = (new Date()).getTime();
            int numberOfCycles = 0;
            // Run the precycle to load the case base
            LogFactory.getLog(this.getClass()).info("Running precycle()");
            CBRCaseBase caseBase = app.preCycle();

            if (!(caseBase instanceof jcolibri.casebase.CachedLinealCaseBase))
                LogFactory.getLog(this.getClass()).warn("Evaluation should be executed using a cached case base");

            ArrayList<CBRCase> originalCases = new ArrayList<>(caseBase.getCases());

            int totalSteps = ((originalCases.size() * testPercent) / 100);
            totalSteps = totalSteps * repetitions;
            jcolibri.util.ProgressController.init(getClass(), "Hold Out Evaluation", totalSteps);

            // For each repetition
            for (int rep = 0; rep < repetitions; rep++) {
                ArrayList<CBRCase> querySet = new ArrayList<>();
                ArrayList<CBRCase> caseBaseSet = new ArrayList<>();
                // Split the case base
                splitCaseBase(originalCases, querySet, caseBaseSet, testPercent);

                // Clear the caseBase
                caseBase.forgetCases(originalCases);

                // Set the cases that acts as case base in this repetition
                caseBase.learnCases(caseBaseSet);

                // Run cycle for each case in querySet
                for (CBRCase c : querySet) {
                    // Run the cycle
                    LogFactory.getLog(this.getClass()).info("Running cycle() " + numberOfCycles);
                    app.cycle(c);

                    jcolibri.util.ProgressController.step(getClass());
                    numberOfCycles++;
                }
            }

            jcolibri.util.ProgressController.finish(getClass());

            // Revert case base to original state
            caseBase.forgetCases(originalCases);
            caseBase.learnCases(originalCases);

            // Run the poscycle to finish the application
            LogFactory.getLog(this.getClass()).info("Running postcycle()");
            app.postCycle();

            t = (new Date()).getTime() - t;

            // Obtain and complete the evaluation result
            report.setTotalTime(t);
            report.setNumberOfCycles(numberOfCycles);

        } catch (Exception e) {
            LogFactory.getLog(this.getClass()).error(e);
        }

    }

    @Override
    public void init(StandardCBRApplication cbrApp) {

        report = new EvaluationReport();
        app = cbrApp;
        try {
            app.configure();
        } catch (ExecutionException e) {
            LogFactory.getLog(this.getClass()).error(e);
        }
    }

    /**
     * Splits the case base in two sets: queries and case base
     *
     * @param holeCaseBase
     *            Complete original case base
     * @param querySet
     *            Output param where queries are stored
     * @param casebaseSet
     *            Output param where case base is stored
     * @param testPercent
     *            Percentage of cases used as queries
     */
    protected void splitCaseBase(Collection<CBRCase> holeCaseBase, List<CBRCase> querySet, List<CBRCase> casebaseSet,
            int testPercent) {
        querySet.clear();
        casebaseSet.clear();

        int querySetSize = (holeCaseBase.size() * testPercent) / 100;
        casebaseSet.addAll(holeCaseBase);

        for (int i = 0; i < querySetSize; i++) {
            int random = (int)(Math.random() * casebaseSet.size());
            CBRCase _case = casebaseSet.get(random);
            casebaseSet.remove(random);
            querySet.add(_case);
        }
    }
}
