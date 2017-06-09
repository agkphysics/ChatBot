/**
 * EvaluationReport.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 07/05/2007
 */
package jcolibri.evaluation;

import java.util.*;

/**
 * This class stores the result of an evaluation. It is configured and filled by
 * an Evaluator. This info is also used to represent graphically the result of
 * an evaluation. The stored information can be:
 * <ul>
 * <li>Several series of data. The lengh of the series is the number of executed
 * cycles. And several series can be stored using diferent labels.
 * <li>Any other information. The put/getOtherData() methods allow to store any
 * other kind of data.
 * <li>Number of cycles.
 * <li>Total evaluation time.
 * <li>Time per cycle.
 * </ul>
 *
 * @author Juan A. Recio Garc�a
 * @version 2.0
 */
public class EvaluationReport {
    private long totalTime;
    private int numberOfCycles;

    /** Stores the series info */
    protected Hashtable<String, Vector<Double>> data;

    /** Stores other info */
    protected Hashtable<String, String> other;

    /** Default constructor */
    public EvaluationReport() {
        data = new Hashtable<>();
        other = new Hashtable<>();
    }

    public void addDataToSeries(String label, Double value) {
        Vector<Double> v = data.get(label);
        if (v == null) {
            v = new Vector<>();
            data.put(label, v);
        }
        v.add(value);
    }

    /**
     * Checks if the evaluation series are correct. This is: all them must have
     * the same length
     */
    public boolean checkData() {
        boolean ok = true;
        int l = -1;
        for (Enumeration<Vector<Double>> iter = data.elements(); iter.hasMoreElements() && ok;) {
            Vector<Double> v = iter.nextElement();
            if (l == -1) l = v.size();
            else ok = (l == v.size());
        }
        return ok;
    }

    public int getNumberOfCycles() {
        return numberOfCycles;
    }

    public String getOtherData(String label) {
        return other.get(label);
    }

    public String[] getOtherLabels() {
        Set<String> set = other.keySet();
        String[] res = new String[set.size()];
        int i = 0;
        for (String e : set)
            res[i++] = e;
        return res;
    }

    /**
     * Gets the evaluation info identified by the label
     *
     * @param label
     *            Identifies the evaluation serie.
     */
    public Vector<Double> getSeries(String label) {
        return data.get(label);
    }

    /**
     * Returns the name of the contained evaluation series
     */
    public String[] getSeriesLabels() {
        Set<String> set = data.keySet();
        String[] res = new String[set.size()];
        int i = 0;
        for (String e : set)
            res[i++] = e;
        return res;
    }

    public double getTimePerCycle() {
        return (double)totalTime / (double)numberOfCycles;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void putOtherData(String label, String data) {
        other.put(label, data);
    }

    public void setNumberOfCycles(int numberOfCycles) {
        this.numberOfCycles = numberOfCycles;
    }

    /**
     * Stes the evaluation info
     *
     * @param label
     *            Identifier of the info
     * @param evaluation
     *            Evaluation result
     */
    public void setSeries(String label, Vector<Double> evaluation) {

        data.put(label, evaluation);
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("Series:\n");
        String[] series = getSeriesLabels();
        for (int i = 0; i < series.length; i++) {
            s.append("  " + series[i] + ": \n    ");
            Vector<Double> v = getSeries(series[i]);
            for (Double d : v)
                s.append(d + ",");
            s.append("\n");
        }

        s.append("\nOther data:\n");
        String[] other = getOtherLabels();
        for (int i = 0; i < other.length; i++) {
            s.append("  " + other[i] + ": " + getOtherData(other[i]) + "\n");
        }

        s.append("\nNumber of Cycles: " + getNumberOfCycles());
        s.append("\nTime per Cycle:   " + getTimePerCycle() + " ms");
        s.append("\nTotal time:       " + getTotalTime() + " ms");

        return s.toString();
    }
}
