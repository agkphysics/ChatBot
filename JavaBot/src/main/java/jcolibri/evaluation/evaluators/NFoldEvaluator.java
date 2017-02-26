/**
 * NFoldEvaluator.java jCOLIBRI2 framework.
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
 * This evaluation method divides the case base into several random folds
 * (indicated by the user). For each fold, their cases are used as queries and
 * the remaining folds are used together as case base. This process is performed
 * several times.
 *
 * @author Juan A. Recio Garc�a - GAIA http://gaia.fdi.ucm.es
 * @version 2.0
 */
public class NFoldEvaluator extends Evaluator {

    protected StandardCBRApplication app;

    protected ArrayList<ArrayList<CBRCase>> _folds;

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
     * Executes the N-Fold evaluation.
     *
     * @param folds
     *            Number of folds (randomly generated).
     * @param repetitions
     *            Number of repetitions
     */
    public void NFoldEvaluation(int folds, int repetitions) {
        try {
            // Get the time
            long t = (new Date()).getTime();
            int numberOfCycles = 0;

            // Run the precycle to load the case base
            LogFactory.getLog(this.getClass()).info("Running precycle()");
            CBRCaseBase caseBase = app.preCycle();

            if (!(caseBase instanceof jcolibri.casebase.CachedLinealCaseBase))
                LogFactory.getLog(this.getClass()).warn("Evaluation should be executed using a cached case base");

            Collection<CBRCase> cases = new ArrayList<>(caseBase.getCases());

            // For each repetition
            for (int r = 0; r < repetitions; r++) {
                // Create the folds
                createFolds(cases, folds);

                // For each fold
                for (int f = 0; f < folds; f++) {
                    ArrayList<CBRCase> querySet = new ArrayList<>();
                    ArrayList<CBRCase> caseBaseSet = new ArrayList<>();
                    // Obtain the query and casebase sets
                    getFolds(f, querySet, caseBaseSet);

                    // Clear the caseBase
                    caseBase.forgetCases(cases);

                    // Set the cases that acts as casebase in this cycle
                    caseBase.learnCases(caseBaseSet);

                    // Run cycle for each case in querySet (current fold)
                    for (CBRCase c : querySet) {
                        LogFactory.getLog(this.getClass()).info("Running cycle() " + numberOfCycles);
                        app.cycle(c);

                        numberOfCycles++;
                    }
                }

            }

            // Revert case base to original state
            caseBase.forgetCases(cases);
            caseBase.learnCases(cases);

            // Run the poscycle to finish the application
            LogFactory.getLog(this.getClass()).info("Running postcycle()");
            app.postCycle();

            // Complete the evaluation result
            report.setTotalTime(t);
            report.setNumberOfCycles(numberOfCycles);

        } catch (Exception e) {
            LogFactory.getLog(this.getClass()).error(e);
        }

    }

    protected void createFolds(Collection<CBRCase> cases, int folds) {
        _folds = new ArrayList<>();
        int foldsize = cases.size() / folds;
        ArrayList<CBRCase> copy = new ArrayList<>(cases);

        for (int f = 0; f < folds; f++) {
            ArrayList<CBRCase> fold = new ArrayList<>();
            for (int i = 0; (i < foldsize) && (copy.size() > 0); i++) {
                int random = (int)(Math.random() * copy.size());
                CBRCase _case = copy.get(random);
                copy.remove(random);
                fold.add(_case);
            }
            _folds.add(fold);
        }
    }

    protected void getFolds(int f, List<CBRCase> querySet, List<CBRCase> caseBaseSet) {
        querySet.clear();
        caseBaseSet.clear();

        querySet.addAll(_folds.get(f));

        for (int i = 0; i < _folds.size(); i++)
            if (i != f) caseBaseSet.addAll(_folds.get(i));
    }

}
