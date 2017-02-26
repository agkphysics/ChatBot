/**
 * FeatureInfo.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 15/06/2007
 */
package jcolibri.extensions.textual.IE.representation.info;

/**
 * This class represents a feature. It's composed by:
 * <ul>
 * <li>feature: Feature name
 * <li>value: Feature value
 * <li>begin: begin position in the text
 * <li>end: end position in the text
 * </ul>
 * <p>
 * First version developed at: Robert Gordon University - Aberdeen & Facultad
 * Inform�tica, Universidad Complutense de Madrid (GAIA)
 * </p>
 *
 * @author Juan Antonio Recio Garc�a
 * @version 2.0
 */
public class FeatureInfo {
    private String feature;
    private String value;
    private int begin;
    private int end;

    /**
     * Creates a feature info object
     */
    public FeatureInfo(String feature, String value, int begin, int end) {
        super();
        this.feature = feature;
        this.value = value;
        this.begin = begin;
        this.end = end;
    }

    /**
     * @return Returns the begin.
     */
    public int getBegin() {
        return begin;
    }

    /**
     * @return Returns the end.
     */
    public int getEnd() {
        return end;
    }

    /**
     * @return Returns the feature.
     */
    public String getFeature() {
        return feature;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param begin
     *            The begin to set.
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * @param end
     *            The end to set.
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @param feature
     *            The feature to set.
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\n(" + feature + "," + value + ")";
    }

}
