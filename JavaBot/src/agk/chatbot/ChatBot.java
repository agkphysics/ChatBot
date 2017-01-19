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

import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.*;
import jcolibri.exception.ExecutionException;
import jcolibri.extensions.textual.IE.representation.Token;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * This class represents the main chat bot application.
 * 
 * @author Aaron Keesing
 * @version 2.0
 */
public class ChatBot implements StandardCBRApplication {
    
    public static final String VERSION_STRING = "2.0";

    /**
     * A limit on the difference between the most similar retrieved case and
     * the least similar retrieved case.
     */
    private static final double SIMILARITY_TOLERANCE = 0.15;
    
    /**
     * The minimum similarity required for retrieved cases.
     * If the maximum retrieved similarity is less than this value, one of the
     * filler responses in {@link ConfusedResponses} is output.
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

    public static void main(String[] args) {
        Path pathToXmls = null;
        if (args.length == 1) pathToXmls = Paths.get(args[0]);
        
        ChatBot bot;
        try {
            bot = new ChatBot(pathToXmls);
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
            while (!(line = sc.nextLine()).equals("") && !bot.finished) {
                CBRQuery query = bot.strToQuery(line);
                bot.cycle(query);
                System.out.print("> ");
            }
            
            bot.postCycle();
            sc.close();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("Finished bot application");
        System.out.println("------------------------------------------------");
        System.out.println();
    }
    
    /**
     * Converts a string of user input to a query object which
     * can then be used in the CBR application.
     * 
     * @param line
     *             the string to convert to a query
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
     * Stores the current sequence of statements in the case base, if the
     * length of the current thread is above the {@link ChatBot#THREAD_THRESHOLD}.
     */
    private void storeCurrentThread() {
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
    }

    /**
     * Initialise the chat bot with the path to the processed XML corpus
     * 
     * @param p
     *          the path to the corpus
     * @throws FileNotFoundException
     */
    public ChatBot(Path p) throws FileNotFoundException {
        if (p != null) corpusPath = p;
        else if (Files.exists(Paths.get("xmlconv"))) corpusPath = Paths.get("xmlconv");
        else if (Files.exists(Paths.get("../xmlconv"))) corpusPath = Paths.get("../xmlconv");
        else if (Files.exists(Paths.get("lib/xmlconv"))) corpusPath = Paths.get("lib/xmlconv");
        else throw new FileNotFoundException("No valid corpus path given or found.");
        
        System.out.println();
        System.out.println("Started bot application -- Chat Bot version " + VERSION_STRING);
        System.out.println("-----------------------------------------------");
        System.out.println();
    }

    /**
     * Initialises the WordNet dictionary, NLP pipeline, corpus connector and
     * current conversation thread.
     */
    @Override
    public void configure() throws ExecutionException {
        try {
            TextSimilarity.init();
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new ExecutionException(e1);
        }
        NLPText.initPipeline();
        
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        _connector = new ProcessedXMLConnector(corpusPath);
        _caseBase = new InMemoryCaseBase();
        currentThread = new ArrayList<>();
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
        NNConfig simConfig = new NNConfig();
        simConfig.setDescriptionSimFunction(new Average());
        Attribute textAttribute = new Attribute("text", ChatResponse.class);
        simConfig.addMapping(textAttribute, new TextSimilarity());
        
        long t0 = System.currentTimeMillis();
        Collection<RetrievalResult> res = MultiThreadedNNSimilarity.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
        long t1 = System.currentTimeMillis();
        System.out.println(String.format("Similarity computed in %.2f seconds", (t1 - t0) / 1000.0));
        
        // The collection is sorted because it returns a sorted list.
        Iterator<RetrievalResult> resIter = res.iterator();
        List<RetrievalResult> topRes = new ArrayList<>();
        topRes.add(resIter.next());
        
        double topEval = topRes.get(0).getEval();
        
        ChatResponse response;
        if (topEval < MIN_SIMILARITY) {
            response = new ChatResponse();
            response.setText(ConfusedResponses.STATEMENT_ONE);
            storeCurrentThread();
            currentThread.clear();
        } else {
            for (RetrievalResult r = resIter.next();
            		topEval - r.getEval() < SIMILARITY_TOLERANCE && r.getEval() > MIN_SIMILARITY && topRes.size() <= MAX_CASES_TO_KEEP;
            		r = resIter.next()) {
            	topRes.add(r);
            }
            
            System.out.println(String.format("Found %d cases:", topRes.size()));
            for (RetrievalResult r : topRes) System.out.println(r.get_case().toString() + " " + r.getEval());
            response = (ChatResponse)topRes.iterator().next().get_case().getSolution();
        }
        currentThread.add((ChatResponse)query.getDescription());
        currentThread.add(response);
        System.out.println(response.toString());
        
        for (Token t : ((ChatResponse)query.getDescription()).getText().getAllTokens()) {
            String word = t.getRawContent().toLowerCase();
            if (word.contains("bye") || word.contains("goodbye")) {
                finished = true;
            }
        }
        System.out.println();
    }
    
    /**
     * This method is a useful because cycle has return type {@code void}.
     * 
     * @return
     *         the last response given by the bot
     */
    public ChatResponse getLastResponse() {
        return currentThread.get(currentThread.size() - 1);
    }

    /**
     * Closes the corpus connector and case base.
     */
    @Override
    public void postCycle() throws ExecutionException {
        storeCurrentThread();
        
        _caseBase.close();
        _connector.close();
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
        System.out.println(String.format("Generated %d English response pairs in %.2f seconds.", _caseBase.getCases().size(), (t1 - t0) / 1000.0));
        System.out.println();
        
        return _caseBase;
    }

}
