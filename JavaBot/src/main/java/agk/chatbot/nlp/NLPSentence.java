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

import java.util.*;

/**
 * This class represents a sentence that has been processed using NLP tools. It
 * also contains a word set and a map from words to indices for the text
 * similarity function.
 *
 * @author Aaron
 */
public class NLPSentence {

    protected Set<String> wordSet;
    protected Map<String, Integer> wordMap;
    protected List<NLPToken> tokens;
    protected String text;

    /**
     * Initialise the sentence with the given content.
     *
     * @param text
     *            the content of the sentence
     */
    public NLPSentence(String text) {
        this.text = text;
        wordMap = new HashMap<>();
        wordSet = new HashSet<>();
        tokens = new ArrayList<>();
    }

    /**
     * Adds a token to the sentence and updates the word set and map.
     *
     * @param token
     *            the <code>Token</code> object to add
     */
    public void addToken(NLPToken token) {
        tokens.add(token);
        wordSet.add(token.getRawContent().toLowerCase());
        wordMap.put(token.getRawContent().toLowerCase(), wordSet.size());
    }

    public void addTokens(List<NLPToken> tokens) {
        for (NLPToken t : tokens)
            addToken(t);
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the list of tokens in this sentence.
     *
     * @return the tokens in this sentence
     */
    public List<NLPToken> getTokens() {
        return tokens;
    }

    /**
     * @return a map from words to indices
     */
    public Map<String, Integer> getWordMap() {
        return wordMap;
    }

    /**
     * @return the word set
     */
    public Set<String> getWordSet() {
        return wordSet;
    }
}
