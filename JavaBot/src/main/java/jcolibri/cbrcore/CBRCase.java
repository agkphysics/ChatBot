/**
 * CBRCase.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 05/01/2007
 */

package jcolibri.cbrcore;

/**
 * Interface that represents any Case structure of jCOLIBRI. It is composed by
 * several CaseComponents:
 * <ul>
 * <li>A description (inherited from CBRQuery).
 * <li>A solution.
 * <li>A justification of a Solution.
 * <li>A result.
 * </ul>
 * <p>
 * These components of a case where decided after a revision of the following
 * books:
 * <ul>
 * <li>I. Watson. Applying case-based reasoning: techniques for enterprise
 * systems. Morgan Kaufmann Publishers Inc., San Francisco, CA, USA, 1998.
 * <li>K.-D. Althoff, E. Auriol, R. Barletta, and M. Manago. A Review of
 * Industrial Case-Based Reasoning Tools. AI Intelligence, Oxford, 1995.
 * </ul>
 *
 * @see jcolibri.cbrcore.CaseComponent
 */
public class CBRCase extends CBRQuery {

    CaseComponent solution;
    CaseComponent justificationOfSolution;
    CaseComponent result;

    /**
     * Returns the justificationOfSolution.
     */
    public CaseComponent getJustificationOfSolution() {
        return justificationOfSolution;
    }

    /**
     * Returns the result.
     */
    public CaseComponent getResult() {
        return result;
    }

    /**
     * Returns the solution.
     */
    public CaseComponent getSolution() {
        return solution;
    }

    /**
     * Sets the Justification of Solution component.
     *
     * @param justificationOfSolution
     *            The justificationOfSolution to set.
     */
    public void setJustificationOfSolution(CaseComponent justificationOfSolution) {
        this.justificationOfSolution = justificationOfSolution;
    }

    /**
     * Sets the Result component
     */
    public void setResult(CaseComponent result) {
        this.result = result;
    }

    /**
     * Sets the solution component
     */
    public void setSolution(CaseComponent solution) {
        this.solution = solution;
    }

    @Override
    public String toString() {
        return super.toString() + "[Solution: " + solution + "][Sol.Just.: " + justificationOfSolution + "][Result: "
                + result + "]";
    }

}
