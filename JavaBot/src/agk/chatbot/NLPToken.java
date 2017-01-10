package agk.chatbot;

import jcolibri.extensions.textual.IE.representation.Token;

/**
 * This class represents an extension of the Token class which provides an NER
 * tag field.
 * 
 * @author Aaron
 */
public class NLPToken extends Token {
    
    protected String nerTag;

    /**
     * @param text the raw text of the token
     */
    public NLPToken(String text) {
        super(text);
        nerTag = "O";
    }
    
    /**
     * Gets the NER tag of this token.
     * 
     * @return the NER tag
     */
    public String getNerTag() {
        return nerTag;
    }

    /**
     * Sets the NER tag of this token.
     * 
     * @param nerTag the NER tag to set
     */
    public void setNerTag(String nerTag) {
        this.nerTag = nerTag;
    }
}
