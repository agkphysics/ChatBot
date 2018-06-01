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

/**
 * This interface represents a generic NLP annotator that can annotate some
 * text.
 *
 * @author Aaron
 */
public interface BotNLPAnnotator {

    /**
     * Initialises the annotator. This method should immediately return if the
     * annotator is already initialised.
     *
     * @return <code>true</code> if successfully initialised, <code>false</code>
     *         otherwise.
     */
    public boolean initAnnotator();

    /**
     * Annotates the given text and creates an {@link NLPText} object which
     * contains the NLP information.
     *
     * @param content
     *            the string to process
     * @return an <code>NLPText</code> object containing the annotated content
     */
    public NLPText processString(String content);
}
