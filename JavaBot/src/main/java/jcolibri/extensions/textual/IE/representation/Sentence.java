/**
 * Sentence.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 15/06/2007
 */
package jcolibri.extensions.textual.IE.representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sentence of the text. A sentence is composed by Tokens.
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 * @see jcolibri.extensions.textual.IE.representation.Sentence
 */
public class Sentence {

    protected List<Token> tokens;

    protected String text;

    /**
     * Creates a sentence object that representes the text of the parameter.
     */
    public Sentence(String text) {
        this.text = text;
        tokens = new ArrayList<>();
    }

    /**
     * Adds a token
     */
    public void addToken(Token token) {
        tokens.add(token);
    }

    /**
     * Adds tokens
     */
    public void addTokens(List<Token> tokens) {
        this.tokens.addAll(tokens);
    }

    /**
     * Returns the original text of the sentence
     */
    public String getRawContent() {
        return text;
    }

    /**
     * Returns the tokens
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Prints the content and annotations.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("  SENTENCE begin\n");
        for (Token tok : tokens)
            sb.append(tok.toString());
        sb.append("  SENTENCE end\n");
        return sb.toString();
    }

}
