package jcolibri.extensions.maintenance_evaluation.evaluators;

import java.util.*;

import org.apache.commons.logging.LogFactory;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.extensions.maintenance_evaluation.MaintenanceEvaluator;

/**
 * This evaluation divides the case base into several random folds (indicated by
 * the user). For each fold, their cases are used as queries and the remaining
 * folds are used together as case base. Maintenance is performed on the
 * case-base before running the queries. This process is performed several
 * times.
 *
 * @author Lisa Cummins.
 * @author Juan A. Recio Garc�a - GAIA http://gaia.fdi.ucm.es
 */
public class MaintenanceNFoldEvaluator extends MaintenanceEvaluator {
    /**
     * Clears the current query and case base sets and populates the query set
     * with fold f and the case base set with the cases not contained in fold f.
     *
     * @param f
     *            the fold to use.
     * @param querySet
     *            the set of queries.
     * @param caseBaseSet
     *            the set of cases.
     */
    public static void getFolds(int f, List<CBRCase> querySet, List<CBRCase> caseBaseSet,
            ArrayList<ArrayList<CBRCase>> folds) {
        querySet.clear();
        caseBaseSet.clear();

        querySet.addAll(folds.get(f));

        for (int i = 0; i < folds.size(); i++)
            if (i != f) caseBaseSet.addAll(folds.get(i));
    }

    /**
     * Executes the N-Fold evaluation.
     *
     * @param numFolds
     *            the number of randomly generated folds.
     * @param repetitions
     *            the number of repetitions
     */
    public void NFoldEvaluation(int numFolds, int repetitions) {
        try { // Get the time
            long t = (new Date()).getTime();
            int numberOfCycles = 0;

            // Run the precycle to load the case base
            LogFactory.getLog(this.getClass()).info("Running precycle()");
            CBRCaseBase caseBase = app.preCycle();

            if (!(caseBase instanceof jcolibri.casebase.CachedLinealCaseBase))
                LogFactory.getLog(this.getClass()).warn("Evaluation should be executed using a cached case base");

            Collection<CBRCase> cases = new ArrayList<>(caseBase.getCases());

            // For each repetition
            for (int r = 0; r < repetitions; r++) { // Create the folds
                ArrayList<ArrayList<CBRCase>> folds = createFolds(cases, numFolds);

                // For each fold
                for (int f = 0; f < numFolds; f++) {
                    ArrayList<CBRCase> querySet = new ArrayList<>();
                    prepareCases(cases, querySet, f, caseBase, folds);

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

    /**
     * Divides the given cases into the given number of folds.
     *
     * @param cases
     *            the original cases.
     * @param numFolds
     *            the number of folds.
     */
    protected ArrayList<ArrayList<CBRCase>> createFolds(Collection<CBRCase> cases, int numFolds) {
        ArrayList<ArrayList<CBRCase>> folds = new ArrayList<>();
        int foldsize = cases.size() / numFolds;
        ArrayList<CBRCase> copy = new ArrayList<>(cases);

        for (int f = 0; f < numFolds; f++) {
            ArrayList<CBRCase> fold = new ArrayList<>();
            for (int i = 0; (i < foldsize) && (copy.size() > 0); i++) {
                int random = (int)(Math.random() * copy.size());
                CBRCase _case = copy.get(random);
                copy.remove(random);
                fold.add(_case);
            }
            folds.add(fold);
        }
        return folds;
    }

    /**
     * Prepares the cases for evaluation by setting up test and training sets
     *
     * @param originalCases
     *            Complete original set of cases
     * @param querySet
     *            Where queries are to be stored
     * @param fold
     *            The fold number
     * @param caseBase
     *            The case base
     */
    protected void prepareCases(Collection<CBRCase> originalCases, List<CBRCase> querySet, int fold,
            CBRCaseBase caseBase, ArrayList<ArrayList<CBRCase>> folds) {
        ArrayList<CBRCase> caseBaseSet = new ArrayList<>();

        // Obtain the query and casebase sets
        getFolds(fold, querySet, caseBaseSet, folds);

        // Clear the caseBase
        caseBase.forgetCases(originalCases);

        // Set the cases that acts as casebase in this cycle
        caseBase.learnCases(caseBaseSet);

        if (simConfig != null && editMethod != null) { // Perform
                                                       // maintenance
                                                       // on this case
                                                       // base
            editCaseBase(caseBase);
        }
    }
}
