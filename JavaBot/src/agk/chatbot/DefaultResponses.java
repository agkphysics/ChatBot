/* Copyright (C) 2016, 2017 Aaron Keesing
 * 
 * This file is part of CBR Chat Bot.
 * 
 * CBR Chat Bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CBR Chat Bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CBR Chat Bot.  If not, see <http://www.gnu.org/licenses/>.
 */

package agk.chatbot;

import java.util.Random;

/**
 * This class holds a number of phrases that can be used to change the subject
 * of a conversation if the bot doesn't find (m)any similar cases.
 * 
 * @author aaron
 */
final class DefaultResponses {
    
    static Random random = new Random();
    
    final static String QUESTION_1 = "I'm not sure, but what are you up to these days?";
    final static String QUESTION_2 = "I don't know. How are you?";
    
    final static String STATEMENT_1 = "I'm not sure about that.";
    final static String STATEMENT_2 = "I don't know.";
    final static String STATEMENT_3 = "I don't understand.";
    final static String STATEMENT_4 = "I can't understand that.";
    
    final static String FAREWELL_1 = "Goodbye.";
    final static String FAREWELL_2 = "Okay chat with you later.";
    
    final static String[] QUESTION_RESPONSES = {
            QUESTION_1, QUESTION_2, STATEMENT_1, STATEMENT_2
    };
    
    final static String[] STATEMENT_RESPONSES = {
            STATEMENT_3, STATEMENT_4
    };
    
    final static String[] FAREWELL_RESPONSES = {
            FAREWELL_1, FAREWELL_2
    };
    
    /**
     * Gets a random response to a given unknown question.
     * 
     * @return
     *         a random response to a given unknown question
     */
    static String getQuestionResponse() {
        int r = random.nextInt(QUESTION_RESPONSES.length);
        return QUESTION_RESPONSES[r];
    }
    
    /**
     * Gets a random response to a given unknown statement.
     * 
     * @return
     *         the response
     */
    static String getStatementResponse() {
        int r = random.nextInt(STATEMENT_RESPONSES.length);
        return STATEMENT_RESPONSES[r];
    }
    
    /**
     * Gets a random farewell.
     * 
     * @return
     *         the farewell
     */
    static String getFarewell() {
        int r = random.nextInt(FAREWELL_RESPONSES.length);
        return FAREWELL_RESPONSES[r];
    }
}
