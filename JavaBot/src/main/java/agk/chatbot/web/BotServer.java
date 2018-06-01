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

package agk.chatbot.web;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.*;

import agk.chatbot.ChatBot;
import agk.chatbot.cbr.ChatResponse;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;

/**
 * This class implements an extremely simple web server interface to the bot.
 *
 * @author Aaron
 * @version {@value agk.chatbot.ChatBot#VERSION}
 */
public class BotServer implements HttpHandler {

    /**
     * The port that the server runs on.
     */
    public static final int PORT = 8000;

    static ChatBot bot;
    static volatile boolean initialised = false;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bot = new ChatBot();
                    bot.configure();
                    bot.preCycle();
                    initialised = true;
                    System.out.println("Bot initialised.");
                } catch (Exception e) {
                    System.out.println("An error occured while initialising the bot.");
                    e.printStackTrace();
                }
            }
        }).start();

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.out.println("Error while trying to create server on port " + PORT);
            e.printStackTrace();
            return;
        }
        server.createContext("/", new BotServer());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on " + server.getAddress().toString());
    }

    /**
     * Initialises a defauilt BotServer object.
     */
    public BotServer() {}

    /**
     * Handles GET and POST requests from users and sends back a JSON object
     * containing a status and result.
     */
    @Override
    public void handle(HttpExchange t) throws IOException {
        int status = 200;
        String result = "";
        OutputStream os = t.getResponseBody();
        JSONObject json = new JSONObject();
        Headers responseHeaders = t.getResponseHeaders();
        Headers requestHeaders = t.getRequestHeaders();
        responseHeaders.add("Content-Type", "application/json");

        if (t.getRequestMethod().equals("GET")) {
            System.out.println("GET request recieved");
            result = Boolean.toString(initialised);
            status = 200;
        } else if (t.getRequestMethod().equals("POST")) {
            System.out.println("POST request recieved");
            if (initialised) {
                StringWriter strWriter = new StringWriter();
                org.apache.commons.io.IOUtils.copy(t.getRequestBody(), strWriter, Charset.forName("UTF-8"));
                Map<String, String> map = queryToMap(strWriter.toString());
                String chat = map.get("c");

                if (requestHeaders.getFirst("Content-Type").startsWith("application/x-www-form-urlencoded")) {
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
                    result = "Bad request. Requires Content-Type: application/x-www-form-urlencoded";
                    status = 400;
                }
            } else {
                status = 503;
                result = "Bot is still initialising.";
            }
        } else {
            status = 501;
            result = "Method not implemented.";
        }

        try {
            json.put("status", status);
            json.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String response = json.toString();
        t.sendResponseHeaders(status, response.length());
        os.write(response.getBytes());
        os.close();
    }

    /**
     * Converts a URL-encoded query into a {@link Map} object of key-value
     * pairs.
     *
     * @param query
     *            the GET or POST query to map to (key, value) pairs
     * @return a {@link Map} of <code>&lt;String, String&gt;</code> pairs that
     *         contains the decoded items in the query.
     */
    public Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (String item : query.split("&")) {
            String key = item.substring(0, item.indexOf("="));
            String value = item.substring(item.indexOf("=") + 1, item.length());
            map.put(key, value);
        }
        return map;
    }
}
