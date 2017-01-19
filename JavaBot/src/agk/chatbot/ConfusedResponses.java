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

/**
 * This class holds a number of phrases that can be used to change the subject
 * of a conversation if the bot doesn't find (m)any similar cases.
 * 
 * @author aaron
 */
final class ConfusedResponses {
    
    final static String QUESTION_ONE = "I'm not sure, but what are you up to these days?";
    final static String QUESTION_TWO = "I don't know. How are you?";
    
    final static String STATEMENT_ONE = "I'm not sure about that.";
    final static String STATEMENT_TWO = "I don't know.";
    final static String STATEMENT_THREE = "I don't understand.";
}
