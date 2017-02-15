/*
 * Copyright (C) 2016, 2017 Aaron Keesing
 * 
 * This file is part of CBR Chat Bot.
 * 
 * CBR Chat Bot is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * CBR Chat Bot is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * CBR Chat Bot. If not, see <http://www.gnu.org/licenses/>.
 */

package agk.chatbot.cbr;

import agk.chatbot.nlp.NLPText;
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
     * @param text
     *            the text to set
     */
    public void setText(NLPText text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text.fromString(text);
    }

    /*
     * (non-Javadoc)
     * 
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
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return text.toString();
    }

}
