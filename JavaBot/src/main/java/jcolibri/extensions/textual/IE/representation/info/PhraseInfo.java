/**
 * PhraseInfo.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 15/06/2007
 */
package jcolibri.extensions.textual.IE.representation.info;

/**
 * This class represents a feature. It's composed by:
 * <ul>
 * <li>phrase: Feature name
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
public class PhraseInfo {
    private String phrase;
    private int begin;
    private int end;

    /**
     * Creates a phrase info object
     */
    public PhraseInfo(String phrase, int begin, int end) {
        super();
        this.phrase = phrase;
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
     * @return Returns the phrase.
     */
    public String getPhrase() {
        return phrase;
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
     * @param phrase
     *            The phrase to set.
     */
    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    @Override
    public String toString() {
        return "\n" + phrase + " (" + begin + "," + end + ")";
    }
}
