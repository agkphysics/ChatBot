/**
 * 
 */
package agk.chatbot;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.datatypes.Text;
import talkbank.schema.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aaron
 *
 */
public class ChatResponse implements CaseComponent {
    
    String id;
    Text text;

    /**
     * 
     */
    public ChatResponse() {
        text = new Text();
    }
    
    /**
     * @return the text
     */
    public Text getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(Text text) {
        this.text = text;
    }
    
    public void setText(String text) {
        try {
            this.text.fromString(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
