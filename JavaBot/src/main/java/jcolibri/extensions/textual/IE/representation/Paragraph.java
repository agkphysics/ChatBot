/**
 * Paragraph.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 15/06/2007
 */
package jcolibri.extensions.textual.IE.representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a paragraph of the text. It is composed by sentences.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 * @see jcolibri.extensions.textual.IE.representation.Sentence
 */
public class Paragraph {

    protected List<Sentence> sentences;

    String text;

    /**
     * Creates a paragraph object that representes the text of the parameter.
     */
    public Paragraph(String text) {
        this.text = text;
        sentences = new ArrayList<>();
    }

    /**
     * Adds a sentence
     */
    public void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }

    /**
     * Adds sentences
     */
    public void addSentences(List<Sentence> sentences) {
        this.sentences.addAll(sentences);
    }

    /**
     * Returns the original text of the paragraph
     */
    public String getRawContent() {
        return text;
    }

    /**
     * Returns the sentences
     */
    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * Prints the content and annotations.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PARAGRAPH begin\n");
        for (Sentence sent : sentences)
            sb.append(sent.toString());
        sb.append("PARAGRAPH end\n");
        return sb.toString();
    }

}
