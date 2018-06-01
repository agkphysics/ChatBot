/*
 * Copyright (C) 2016-2018 Aaron Keesing
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

package agk.chatbot.nlp;

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
     * @param text
     *            the raw text of the token
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
     * @param nerTag
     *            the NER tag to set
     */
    public void setNerTag(String nerTag) {
        this.nerTag = nerTag;
    }
}
