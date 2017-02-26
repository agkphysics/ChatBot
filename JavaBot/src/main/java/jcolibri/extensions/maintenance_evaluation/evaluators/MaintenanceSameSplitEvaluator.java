package jcolibri.extensions.maintenance_evaluation.evaluators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import org.apache.commons.logging.LogFactory;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.extensions.maintenance_evaluation.MaintenanceEvaluator;
import jcolibri.util.FileIO;

/**
 * This evaluation splits the case base in two sets: one used for testing where
 * each case is used as query, and another that acts as normal case base. It
 * uses queries from a file so that the evaluation can be repeated with the same
 * test/training set split. The generateSplit() method does the initial random
 * split and saves the query set in a file. Later, the HoldOutfromFile() method
 * uses that file to load the query set and perform the evaluation.
 *
 * @author Juan A. Recio Garc�a & Lisa Cummins
 */
public class MaintenanceSameSplitEvaluator extends MaintenanceEvaluator {

    /**
     * Splits the case base in two sets: queries and case base, with the queries
     * contained in the given file
     *
     * @param wholeCaseBase
     *            Complete original case base
     * @param querySet
     *            Output param where queries are stored
     * @param casebaseSet
     *            Output param where case base is stored
     * @param filename
     *            File which contains the queries
     */
    public static void splitCaseBaseFromFile(Collection<CBRCase> wholeCaseBase, List<CBRCase> querySet,
            List<CBRCase> casebaseSet, String filename) {
        querySet.clear();
        casebaseSet.clear();

        casebaseSet.addAll(wholeCaseBase);

        try {
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(FileIO.findFile(filename).getFile()));
            String line = "";
            while ((line = br.readLine()) != null) {
                CBRCase c = null;
                int pos = 0;
                boolean found = false;
                for (Iterator<CBRCase> iter = casebaseSet.iterator(); iter.hasNext() && (!found);) {
                    c = iter.next();
                    if (c.getID().toString().equals(line)) found = true;
                    else pos++;
                }
                if (c == null) {
                    System.out.println("Case " + line + " not found into case base");
                    continue;
                }

                casebaseSet.remove(pos);
                querySet.add(c);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Perform HoldOut evaluation using the queries contained in the specified
     * file.
     *
     * @param file
     *            the file containing the queries.
     */
    public void HoldOutfromFile(String file) {
        try { // Obtain the time
            long t = (new Date()).getTime();
            int numberOfCycles = 0;

            // Run the precycle to load the case base
            LogFactory.getLog(this.getClass()).info("Running precycle()");
            CBRCaseBase caseBase = app.preCycle();

            if (!(caseBase instanceof jcolibri.casebase.CachedLinealCaseBase))
                LogFactory.getLog(this.getClass()).warn("Evaluation should be executed using a cached case base");

            ArrayList<CBRCase> originalCases = new ArrayList<>(caseBase.getCases());
            ArrayList<CBRCase> querySet = new ArrayList<>();

            prepareCases(originalCases, querySet, file, caseBase);

            int totalSteps = querySet.size();
            jcolibri.util.ProgressController.init(getClass(), "Same Split - Hold Out Evaluation", totalSteps);

            // Run cycle for each case in querySet
            for (CBRCase c : querySet) {
                // Run the cycle
                LogFactory.getLog(this.getClass()).info("Running cycle() " + numberOfCycles);

                // report.storeQueryNum();

                app.cycle(c);

                jcolibri.util.ProgressController.step(getClass());
                numberOfCycles++;
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

    /**
     * Prepares the cases for evaluation by setting up test and training sets
     *
     * @param originalCases
     *            Complete original set of cases
     * @param querySet
     *            Where queries are to be stored
     * @param caseBase
     *            The case base
     */
    protected void prepareCases(Collection<CBRCase> originalCases, List<CBRCase> querySet, String file,
            CBRCaseBase caseBase) {
        ArrayList<CBRCase> caseBaseSet = new ArrayList<>();

        // Split the case base
        splitCaseBaseFromFile(originalCases, querySet, caseBaseSet, file);

        // Clear the caseBase
        caseBase.forgetCases(originalCases);

        // Set the cases that acts as case base in this repetition
        caseBase.learnCases(caseBaseSet);

        if (simConfig != null && editMethod != null) { // Perform
                                                       // maintenance
                                                       // on this case
                                                       // base
            editCaseBase(caseBase);
        }
    }
}
