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

package agk.chatbot.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import agk.chatbot.*;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;


/**
 * This is the main servlet for taking requests of user input and sending back
 * the bot's responses.
 * 
 * @version 2.0
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static ChatBot bot;
	static boolean initialised;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        initialised = false;
        bot = null;
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
    @Override
	public void init(ServletConfig config) throws ServletException {
	    if (bot == null) {
	        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bot = new ChatBot(Paths.get(config.getServletContext().getResource("/WEB-INF/xmlconv").toURI()));
                        bot.configure();
                        bot.preCycle();
                        initialised = true;
                    } catch (MalformedURLException | URISyntaxException | FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
	    }
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    JSONObject json = new JSONObject();
	    int status;
        response.addHeader("Content-Type", "application/json");
        try {
            status = 200;
            json.put("status", status);
            json.put("result", initialised);
        } catch (JSONException e) {
            status = 500;
            e.printStackTrace();
        }
        response.setStatus(status);
	    response.getWriter().print(json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    int status = 200;
        String result = "";
        JSONObject json = new JSONObject();
        response.addHeader("Content-Type", "application/json");
	    
        if (initialised) {
    	    if (request.getHeader("Content-Type").startsWith("application/x-www-form-urlencoded")) {
    	        String chat = request.getParameter("c");
        	    if (chat.length() == 0 || chat.length() > 140) {
        	        status = 400;
        	        result = "Bad request. Invalid chat message.";
        	    } else {
            	    try {
            	        CBRQuery query = bot.strToQuery(chat);
            	        bot.cycle(query);
            	        ChatResponse resp = bot.getLastResponse();
            	        status = 200;
            	        result = resp.getText().toString();
            	    } catch (ExecutionException e) {
            	        status = 500;
            	        result = "An error occurred.";
            	        e.printStackTrace();
            	    }
        	    }
    	    } else {
                status = 400;
                result = "Bad request. Requires Content-Type: application/x-www-form-urlencoded";
            }
        } else {
            status = 503;
            result = "Bot is still initialising.";
        }
	    
	    try {
	        json.put("status", 400);
            json.put("result", result);
	    } catch (JSONException e) {
	        status = 500;
	        e.printStackTrace();
	    }
	    response.setStatus(status);
	    response.getWriter().print(json.toString());
	}
	
    /**
     * Deinits the servlet and destroys the associated bot.
     */
    @Override
	public void destroy() {
	    try {
            bot.postCycle();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
	    bot = null;
	    System.out.println("Servlet destroyed.");
	}
}
