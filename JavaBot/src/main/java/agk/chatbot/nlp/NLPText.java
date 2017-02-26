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

package agk.chatbot.nlp;

import java.util.*;

import jcolibri.datatypes.Text;

/**
 * This class adds some additional functionality to the IEText class. It uses
 * {@link NLPToken} tokens instead of normal tokens, and adds the ability to
 * process a string using the {@code fromString()} function.
 *
 * @author Aaron
 */
public class NLPText extends Text {

    protected static BotNLPAnnotator annotator;

    private final static List<String> STOPWORDS = Arrays.asList("a", "the", "an", "this", "that", "it", "so", "no",
            "not", "but", "and", "okay", "any", "so");

    protected List<NLPSentence> sentences;

    /**
     * Gets the current annotator object.
     *
     * @return the current annotator
     */
    public static BotNLPAnnotator getAnnotator() {
        return annotator;
    }

    /**
     * Determines whether or not the given word is a stop word.
     *
     * @param word
     *            the word to test
     * @return a boolean value indicating whether or not the given word is a
     *         stop word.
     */
    public static boolean isStopWord(String word) {
        return STOPWORDS.contains(word.toLowerCase());
    }

    /**
     * Sets the annotator to use for annotating all texts. This function
     * <b>must</b> be called at least once with a non-null argument before any
     * texts can be annotated.
     *
     * @param annotator
     *            the annotator to use
     */
    public static void setAnnotator(BotNLPAnnotator annotator) {
        NLPText.annotator = annotator;
    }

    /**
     * Creates an {@link NLPText} object containing the empty string.
     */
    public NLPText() {
        this("");
    }

    /**
     * Creates a new {@link NLPText} object with the given string as content.
     * Does not do any processing on the content.
     *
     * @param content
     *            the plain string representation of the text.
     */
    public NLPText(String content) {
        super(content);
        sentences = new ArrayList<>();
    }

    /**
     * @param s
     */
    public void addSentence(NLPSentence s) {
        sentences.add(s);
    }

    /**
     * Converts a string into an {@link NLPText} object using some NLP pipeline.
     *
     * @param content
     *            the plain string representation of the text.
     */
    @Override
    public void fromString(String content) {
        if (annotator == null) throw new IllegalStateException("Annotator not initialised.");
        NLPText processed = annotator.processString(content);
        rawContent = processed.rawContent;
        sentences = processed.sentences;
    }

    /**
     * Returns an immutable list containing all tokens from all sentences in
     * this <code>NLPText</code> object.
     *
     * @return an immutable list of tokens
     */
    public List<NLPToken> getAllTokens() {
        List<NLPToken> allToks = new ArrayList<>();
        sentences.forEach(x -> allToks.addAll(x.tokens));
        return Collections.unmodifiableList(allToks);
    }

    public List<NLPSentence> getSentences() {
        return sentences;
    }

    /**
     * Sets the raw content of the text object.
     *
     * @param content
     *            the content to set
     */
    public void setRawContent(String content) {
        rawContent = content;
    }
}
