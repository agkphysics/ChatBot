/**
 * Sentence.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 15/06/2007
 */
package jcolibri.extensions.textual.IE.representation;

import java.util.ArrayList;
import java.util.List;

import jcolibri.extensions.textual.IE.representation.info.WeightedRelation;

/**
 * A token represents an elementary piece of text. It is usually a word or
 * punctuation symbol. This object stores some flags extracted by specific
 * methods:
 * <ul>
 * <li>If the token is a stop word (word without sense).
 * <li>If the token is a main name inside the sentence.
 * <li>The stemed word
 * <li>The Part-Of-Speech tag of the token.
 * <li>A list of relations with other similar tokens.
 * </ul>
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 *
 */
public class Token {

    protected boolean stopWord;
    protected boolean isMainName;
    protected String stem;
    protected String postag;
    protected List<WeightedRelation> relations;

    protected String text;

    /**
     * Creates a token from a string
     */
    public Token(String text) {
        stopWord = false;
        isMainName = false;
        stem = null;
        postag = null;
        this.text = text;
        relations = new ArrayList<>();
    }

    /**
     * Adds a relation
     */
    public void addRelation(WeightedRelation relation) {
        relations.add(relation);
    }

    /**
     * Returns the POS tag
     */
    public String getPostag() {
        return postag;
    }

    /**
     * Returns the original content of the token
     */
    public String getRawContent() {
        return text;
    }

    /**
     * Returns the relations
     */
    public List<WeightedRelation> getRelations() {
        return relations;
    }

    /**
     * Returns the stem
     */
    public String getStem() {
        return stem;
    }

    /**
     * Returns if the token is a Main Name
     */
    public boolean isMainName() {
        return isMainName;
    }

    /**
     * Returns if the token is a stop word
     */
    public boolean isStopWord() {
        return stopWord;
    }

    /**
     * Sets if the token is a Main Name
     */
    public void setMainName(boolean isMainName) {
        this.isMainName = isMainName;
    }

    /**
     * Sets the POS tag
     */
    public void setPostag(String postag) {
        this.postag = postag;
    }

    /**
     * Sets the stem
     */
    public void setStem(String stem) {
        this.stem = stem;
    }

    /**
     * Sets if the token is a stop word
     */
    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }

    /**
     * Prints the content and annotations.
     */
    @Override
    public String toString() {
        return "    [TOKEN: " + getRawContent() + ", stem: " + stem + ", POSTAG: " + postag + ", isStopWord?: "
                + stopWord + ", isMainName?:" + isMainName + "]\n";
    }

}
