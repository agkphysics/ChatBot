/**
 * StoreCasesMethod.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 05/01/2007
 */
package jcolibri.method.retain;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;

/**
 * Stores cases in the case base.
 *
 * @author Juan A. Recio-Garcia
 *
 */
public class StoreCasesMethod {

    /**
     * Simple method that add a case to the case base invoking
     * caseBase->learnCases().
     */
    public static void storeCase(CBRCaseBase caseBase, CBRCase _case) {
        Collection<CBRCase> cases = new ArrayList<>();
        cases.add(_case);
        caseBase.learnCases(cases);
    }

    /**
     * Simple method that adds some cases to the case base invoking
     * caseBase->learnCases().
     */
    public static void storeCases(CBRCaseBase caseBase, Collection<CBRCase> cases) {
        caseBase.learnCases(cases);
    }

}
