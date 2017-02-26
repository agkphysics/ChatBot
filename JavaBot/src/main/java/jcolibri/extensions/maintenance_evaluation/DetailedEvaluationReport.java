package jcolibri.extensions.maintenance_evaluation;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.LogFactory;

import jcolibri.cbrcore.CBRQuery;
import jcolibri.evaluation.EvaluationReport;
import jcolibri.method.maintenance.QueryResult;

/**
 * This class stores the result of each query in an evaluation. It is configured
 * and filled by an Evaluator.
 *
 * @author Lisa Cummins.
 */
public class DetailedEvaluationReport extends EvaluationReport {
    /** Stores the query series info */
    protected HashMap<String, List<QueryResult>> queryData;

    /**
     * Creates a new report.
     */
    public DetailedEvaluationReport() {
        super();
        queryData = new HashMap<>();
    }

    /**
     * Adds the given query and value to the series labelled by the given label.
     *
     * @param label
     *            the label whose series the query and value are being added to.
     * @param query
     *            the query.
     * @param value
     *            the query's value.
     */
    public void addDataToSeries(String label, CBRQuery query, Double value) {
        List<QueryResult> queries = queryData.get(label);
        if (queries == null) {
            queries = new LinkedList<>();
        }
        queries.add(new QueryResult(query, value));
        queryData.put(label, queries);
    }

    /**
     * Returns the average of the data series with the given label. If the label
     * given is not the label of any data series, null will be returned and an
     * error message will be printed.
     *
     * @param label
     *            the label of the data series.
     * @return the average of the given data series.
     */
    public Double getAverageOfDataSeries(String label) {
        Vector<Double> v = data.get(label);
        if (v == null) {
            LogFactory.getLog(this.getClass()).error("Data series by this label does not exist");
            return null;
        }
        if (v.size() == 0) {
            return 0.0;
        }
        double total = 0.0;
        for (Double value : v) {
            total += value;
        }
        return total / v.size();
    }

    /**
     * Returns the average of the query data series with the given label. If the
     * label given is not the label of any data series, null will be returned
     * and an error message will be printed.
     *
     * @param label
     *            the label of the query data series.
     * @return the average of the given query data series.
     */
    public Double getAverageOfQueryDataSeries(String label) {
        List<QueryResult> results = queryData.get(label);
        if (results == null) {
            LogFactory.getLog(this.getClass()).error("Data series by this label does not exist");
            return null;
        }
        if (results.size() == 0) {
            return 0.0;
        }
        double total = 0.0;
        for (QueryResult result : results) {
            total += result.getResult();
        }
        return total / results.size();
    }

    /**
     * Returns the evaluation info identified by the given label.
     *
     * @param label
     *            identifies the evaluation series.
     * @return the evaluation info identified by the given label.
     */
    public List<QueryResult> getQuerySeries(String label) {
        return queryData.get(label);
    }

    /**
     * Returns the names of the contained evaluation series.
     *
     * @return the names of the contained evaluation series.
     */
    public String[] getQuerySeriesLabels() {
        Set<String> set = queryData.keySet();
        String[] res = new String[set.size()];
        int i = 0;
        for (String e : set)
            res[i++] = e;
        return res;
    }

    /**
     * Prints the report to the given file.
     *
     * @param filename
     *            the file to print the report to.
     */
    public void printDetailedEvaluationReport(String filename) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));

            pw.println("Results:");
            String[] seriesLabels = getSeriesLabels();
            String[] querySeriesLabels = getQuerySeriesLabels();
            String[] otherLabels = getOtherLabels();

            for (int i = 0; i < seriesLabels.length; i++) {
                pw.println(seriesLabels[i] + ":");
                Vector<Double> results = getSeries(seriesLabels[i]);
                String series = "";
                for (Double result : results)
                    series += result + ",";
                pw.println(series.substring(0, series.length() - 2));
                pw.println("Average: " + getAverageOfDataSeries(seriesLabels[i]));
                pw.println();
            }
            pw.println();

            for (int i = 0; i < querySeriesLabels.length; i++) {
                pw.println(querySeriesLabels[i] + ":");
                List<QueryResult> results = getQuerySeries(querySeriesLabels[i]);
                for (QueryResult qResult : results)
                    pw.println(qResult.getCase().getID() + ": " + qResult.getResult());
                pw.println("Average: " + getAverageOfQueryDataSeries(querySeriesLabels[i]));
                pw.println();
            }
            pw.println();

            for (int i = 0; i < otherLabels.length; i++) {
                pw.println(otherLabels[i] + ": " + getOtherData(otherLabels[i]));
                pw.println();
            }
        } catch (IOException ioe) {} finally {
            if (pw != null) pw.close();
        }
    }

    /**
     * Removes a data serie.
     *
     * @param label
     *            the label of the data series to remove.
     */
    public void removeDataSeries(String label) {
        data.remove(label);
    }

    /**
     * Removes some data.
     *
     * @param label
     *            the label of the data to remove.
     */
    public void removeOtherData(String label) {
        other.remove(label);
    }

    /**
     * Sets the given evaluation series to be the series identified by the given
     * label.
     *
     * @param label
     *            identifier of the evaluation series.
     * @param queryEvaluations
     *            the evaluation series.
     */
    public void setSeries(String label, List<QueryResult> queryEvaluations) {
        queryData.put(label, queryEvaluations);
    }

    /**
     * Returns the String representation of this report.
     *
     * @return the String representation of this report.
     */
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
            s.append("  Average: " + getAverageOfDataSeries(series[i]) + "\n\n");
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
