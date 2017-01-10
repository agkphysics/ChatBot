package agk.chatbot;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;

/**
 * @author Aaron
 *
 */
public class ChatResponse implements CaseComponent {
    
    private String id;
    private NLPText text;

    /**
     * 
     */
    public ChatResponse() {
        text = new NLPText();
    }
    
    /**
     * @return the text
     */
    public NLPText getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(NLPText text) {
        this.text = text;
    }
    
    public void setText(String text) {
        this.text.fromString(text);
    }

    /* (non-Javadoc)
     * @see jcolibri.cbrcore.CaseComponent#getIdAttribute()
     */
    @Override
    public Attribute getIdAttribute() {
        return new Attribute("id", ChatResponse.class);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return text.toString();
    }

}
