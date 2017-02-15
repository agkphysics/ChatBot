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

package agk.chatbot;

import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.*;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;

import java.io.FileNotFoundException;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import agk.chatbot.cbr.*;
import agk.chatbot.cbr.sim.TextSimilarity;
import agk.chatbot.nlp.*;

/**
 * This class represents the main chat bot application.
 * 
 * @author Aaron Keesing
 * @version 2.3
 */
public class ChatBot implements StandardCBRApplication {

    public static final String VERSION_STRING = "2.3";

    /**
     * A limit on the difference between the most similar retrieved case and the
     * least similar retrieved case.
     */
    private static final double SIMILARITY_TOLERANCE = 0.15;

    /**
     * The minimum similarity required for retrieved cases. If the maximum
     * retrieved similarity is less than this value, one of the filler responses
     * in {@link DefaultResponses} is output.
     */
    private static final double MIN_SIMILARITY = 0.5;

    /**
     * The maximum number of cases to retrieve.
     */
    private static final int MAX_CASES_TO_KEEP = 15;

    /**
     * The number of replies which need to be in the current thread in order to
     * be stored as successful cases when the bot exits.
     */
    private static final int THREAD_THRESHOLD = 6;

    private Connector _connector;
    private CBRCaseBase _caseBase;
    private Path corpusPath;

    private List<ChatResponse> currentThread;
    private boolean finished = false;
    private static BotNLPAnnotator annotator;

    public static void main(String[] args) {
        Path pathToXmls = null;
        if (args.length == 1) pathToXmls = Paths.get(args[0]);

        ChatBot bot;
        try {
            bot = new ChatBot(pathToXmls, OpenNLPAnnotator.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Please provide a valid path to corpus.");
            return;
        }

        try {
            bot.configure();
            bot.preCycle();

            System.out.println("Please start the conversation (enter blank line to exit):");
            java.util.Scanner sc = new java.util.Scanner(System.in);
            String line;

            System.out.print("> ");
            while (!(line = sc.nextLine()).equals("")) {
                CBRQuery query = bot.strToQuery(line);
                bot.cycle(query);
                if (bot.finished) break;
                System.out.print("> ");
            }
            sc.close();

            bot.postCycle();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a string of user input to a query object which can then be used
     * in the CBR application.
     * 
     * @param line
     *            the string to convert to a query
     * @return the CBRQuery object for the converted query
     */
    public CBRQuery strToQuery(String line) {
        CBRQuery query = new CBRQuery();
        ChatResponse stmt = new ChatResponse();

        NLPText txt = new NLPText(line);
        txt.fromString(line);
        stmt.setText(txt);
        query.setDescription(stmt);

        return query;
    }

    /**
     * Stores the current sequence of statements in the case base, if the length
     * of the current thread is above the {@link ChatBot#THREAD_THRESHOLD}.
     * Clears the current thread after storing the cases.
     */
    private void storeAndClearThread() {
        if (currentThread.size() >= THREAD_THRESHOLD) {
            List<CBRCase> casesToStore = new ArrayList<>();
            for (int i = 0; i < currentThread.size() - 1; i++) {
                CBRCase c = new CBRCase();
                c.setDescription(currentThread.get(i));
                c.setSolution(currentThread.get(i + 1));
                casesToStore.add(c);
            }
            _caseBase.learnCases(casesToStore);
        }
        currentThread.clear();
    }

    /**
     * Initialise the chat bot with the path to the processed XML corpus
     * 
     * @param p
     *            the path to the corpus
     * @throws FileNotFoundException
     */
    public ChatBot(Path p) throws FileNotFoundException {
        if (p != null) corpusPath = p;
        else if (Files.exists(Paths.get("xmlconv"))) corpusPath = Paths.get("xmlconv");
        else if (Files.exists(Paths.get("../xmlconv"))) corpusPath = Paths.get("../xmlconv");
        else if (Files.exists(Paths.get("lib/xmlconv"))) corpusPath = Paths.get("lib/xmlconv");
        else {
            URI xmlconv;
            try {
                xmlconv = ChatBot.class.getClassLoader().getResource("xmlconv").toURI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new FileNotFoundException("No valid corpus path given or found.");
            }
            if (Files.exists(Paths.get(xmlconv))) corpusPath = Paths.get(xmlconv);
            else throw new FileNotFoundException("No valid corpus path given or found.");
        }

        System.out.println();
        System.out.println("CBR Chat Bot  Copyright (C) 2016, 2017  Aaron Keesing");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions; see the file LICENSE.txt for details.");
        System.out.println();

        System.out.println("Started bot application -- CBR Chat Bot version " + VERSION_STRING);
        System.out.println("--------------------------------------------------");
        System.out.println();
    }

    /**
     * Initialises the bot with the given path and annotator class to
     * instantiate.
     * 
     * @param p
     *            the path to the corpus
     * @param annotClass
     *            the annotator class to instantiate
     * @throws FileNotFoundException
     */
    public ChatBot(Path p, Class<? extends BotNLPAnnotator> annotClass) throws FileNotFoundException {
        this(p);
        try {
            annotator = annotClass.newInstance();
            annotator.initAnnotator();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialises the bot with the given path and annotator.
     * 
     * @param p
     *            the path to the corpus
     * @param annotator
     *            the annotator to use
     * @throws FileNotFoundException
     */
    public ChatBot(Path p, BotNLPAnnotator annotator) throws FileNotFoundException {
        this(p);
        ChatBot.annotator = annotator;
        ChatBot.annotator.initAnnotator();
    }

    /**
     * Initialises the bot with default xml path and Stanford annotator.
     * 
     * @throws FileNotFoundException
     */
    public ChatBot() throws FileNotFoundException {
        this(null, StanfordAnnotator.class);
    }

    /**
     * Initialises the WordNet dictionary, NLP pipeline, corpus connector and
     * current conversation thread.
     */
    @Override
    public void configure() throws ExecutionException {
        try {
            TextSimilarity.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecutionException(e);
        }
        System.out.println(
                "Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576
                        + " MB " + "out of " + Runtime.getRuntime().totalMemory() / 1048576);

        if (annotator == null) {
            annotator = new StanfordAnnotator();
            annotator.initAnnotator();
            System.out.println(
                    "Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576
                            + " MB " + "out of " + Runtime.getRuntime().totalMemory() / 1048576);
        }
        NLPText.setAnnotator(annotator);

        _connector = new ProcessedXMLConnector(corpusPath);
        _caseBase = new InMemoryCaseBase();
        currentThread = new ArrayList<>();
    }

    private ChatResponse getConfusedResponse(ChatResponse statement) {
        ChatResponse response = new ChatResponse();
        if (statement.getText().getAllTokens().stream().anyMatch(x -> x.getRawContent().equals("?"))) {
            response.setText(DefaultResponses.getQuestionResponse());
        } else {
            response.setText(DefaultResponses.getStatementResponse());
        }
        storeAndClearThread();
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jcolibri.cbraplications.StandardCBRApplication#cycle(jcolibri.cbrcore.
     * CBRQuery)
     */
    @Override
    public void cycle(CBRQuery query) throws ExecutionException {
        ChatResponse statement = (ChatResponse)query.getDescription();
        ChatResponse response;
        if (statement.getText().getAllTokens().stream()
                .anyMatch(x -> x.getRawContent().toLowerCase().contains("bye"))) {
            finished = true;
            response = new ChatResponse();
            response.setText(DefaultResponses.getFarewell());
            return;
        }

        NNConfig simConfig = new NNConfig();
        simConfig.setDescriptionSimFunction(new Average());
        Attribute textAttribute = new Attribute("text", ChatResponse.class);
        simConfig.addMapping(textAttribute, new TextSimilarity());

        long t0 = System.currentTimeMillis();
        List<RetrievalResult> res = MultiThreadedNNSimilarity.evaluateSimilarity(_caseBase.getCases(), query,
                simConfig);
        long t1 = System.currentTimeMillis();
        System.out.println(String.format("Similarity computed in %.2f seconds", (t1 - t0) / 1000.0));

        List<RetrievalResult> topRes = new ArrayList<>();
        // The collection is sorted because it returns a sorted list.
        double topEval = res.get(0).getEval();

        if (topEval < MIN_SIMILARITY) {
            // Similarity too low
            response = getConfusedResponse(statement);
        } else {
            for (RetrievalResult r : res) {
                if (topEval - r.getEval() < SIMILARITY_TOLERANCE && r.getEval() > MIN_SIMILARITY
                        && topRes.size() <= MAX_CASES_TO_KEEP) {
                    topRes.add(r);
                }
            }

            System.out.println(String.format("Found %d cases:", topRes.size()));
            for (RetrievalResult r : topRes)
                System.out.println(r.get_case().toString() + " " + r.getEval());
            try {
                response = (ChatResponse)topRes.stream().filter(x -> x.get_case().getSolution() != null).findFirst()
                        .get().get_case().getSolution();
            } catch (NoSuchElementException e) {
                // No non-null response
                response = getConfusedResponse(statement);
            }
            currentThread.add(statement);
            currentThread.add(response);
        }

        System.out.println(response.toString());
        System.out.println();
    }

    /**
     * This method is a useful because cycle has return type {@code void}.
     * 
     * @return the last response given by the bot
     */
    public ChatResponse getLastResponse() throws NoSuchElementException {
        if (currentThread.size() == 0) throw new NoSuchElementException();
        return currentThread.get(currentThread.size() - 1);
    }

    /**
     * Closes the corpus connector and case base.
     */
    @Override
    public void postCycle() throws ExecutionException {
        storeAndClearThread();

        _caseBase.close();
        _connector.close();

        System.out.println();
        System.out.println("Finished bot application");
        System.out.println("---------------------------------------------------");
        System.out.println();
    }

    /**
     * Generates all the statement-response pairs and stores them as cases in
     * memory.
     */
    @Override
    public CBRCaseBase preCycle() throws ExecutionException {
        long t0 = System.currentTimeMillis();
        _caseBase.init(_connector);
        long t1 = System.currentTimeMillis();
        if (_caseBase.getCases().size() == 0) {
            throw new ExecutionException("No cases were generated.");
        }
        System.out.println(String.format("Generated %d English cases in %.2f seconds.", _caseBase.getCases().size(),
                (t1 - t0) / 1000.0));
        System.out.println(
                "Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576
                        + " MB " + "out of " + Runtime.getRuntime().totalMemory() / 1048576);
        System.out.println();

        return _caseBase;
    }

    /**
     * @return the annotator
     */
    public static BotNLPAnnotator getAnnotator() {
        return annotator;
    }

    /**
     * Sets and initialises the annotator.
     * 
     * @param annotator
     *            the annotator to set
     */
    public static void setAnnotator(BotNLPAnnotator annotator) {
        ChatBot.annotator = annotator;
        annotator.initAnnotator();
        NLPText.setAnnotator(annotator);
    }
}
