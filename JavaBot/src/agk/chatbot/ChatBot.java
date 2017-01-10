package agk.chatbot;

import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.*;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;

import java.nio.file.*;
import java.util.*;

import org.apache.commons.logging.*;

/**
 * This class represents the main chat bot application.
 * 
 * @author Aaron Keesing
 * @version 1.8
 */
public class ChatBot implements StandardCBRApplication {
    
    private static Log logger = LogFactory.getLog(ChatBot.class);
    
    private Connector _connector;
    private CBRCaseBase _caseBase;
    private Path corpusPath;
    
    private List<ChatResponse> currentThread;
    
    /**
     * A limit on the difference between the most similar retrieved case and
     * the least similar retrieved case.
     */
    private final double SIMILARITY_TOLERANCE = 0.15;
    /**
     * The minimum similarity required for retrieved cases.
     * If the maximum retrieved similarity is less than this value, one of the
     * filler responses in {@link ConfusedResponses} is output.
     */
    private final double MIN_SIMILARITY = 0.5;
    /**
     * The maximum number of cases to retrieve.
     */
    private final int MAX_CASES_TO_KEEP = 15;
    /**
     * The number of replies which need to be in the current thread in order to
     * be stored as successful cases when the bot exits.
     */
    private final int THREAD_THRESHOLD = 6;

    /**
     * @param args main arguments to application
     */
    public static void main(String[] args) {
    	
        Path pathToXmls = null;
        try {
	        if (args.length == 1) pathToXmls = Paths.get(args[0]);
	        else pathToXmls = Paths.get("../../TalkBank");
        } catch (InvalidPathException e) {
        	System.err.println("Not a valid path to corpus.");
        	System.exit(1);
        }
        
        ChatBot bot = new ChatBot(pathToXmls);
        System.out.println();
        System.out.println("Started bot application");
        
        TextSimilarity.init();
        NLPText.initPipeline();

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
                System.out.print("> ");
            }
            
            bot.postCycle();
            sc.close();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("Finished bot application");
        System.exit(0);
    }
    
    /**
     * Converts a string of user input to a query object which
     * can then be used in the CBR application.
     * 
     * @param line the string to convert to a query
     * @return the CBRQuery object for the converted query
     */
    private CBRQuery strToQuery(String line) {
        CBRQuery query = new CBRQuery();
        ChatResponse stmt = new ChatResponse();
        
        NLPText txt = new NLPText(line);
        txt.fromString(line);
        stmt.setText(txt);
        query.setDescription(stmt);
        
        return query;
    }
    

    /**
     * Initialise the chat bot with the path to the processed XML corpus
     * 
     * @param p the path to the corpus
     */
    public ChatBot(Path p) {
        corpusPath = p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbraplications.StandardCBRApplication#configure()
     */
    @Override
    public void configure() throws ExecutionException {
        _connector = new ProcessedXMLConnector(corpusPath);
        _caseBase = new LinealCaseBase();
        currentThread = new ArrayList<>();
        
        logger.debug("Finished configure()");
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
        System.out.println();
        
        logger.debug("Finished cycle()");
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbraplications.StandardCBRApplication#postCycle()
     */
    @Override
    public void postCycle() throws ExecutionException {
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
        _caseBase.close();
        _connector.close();
        
        logger.debug("Finished postCycle()");
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbraplications.StandardCBRApplication#preCycle()
     */
    @Override
    public CBRCaseBase preCycle() throws ExecutionException {
        long t0 = System.currentTimeMillis();
        _caseBase.init(_connector);
        long t1 = System.currentTimeMillis();
        System.out.println(String.format("Generated %d English response pairs in %.2f seconds.", _caseBase.getCases().size(), (t1 - t0) / 1000.0));
        
        logger.debug("Finished preCycle()");
        System.out.println();
        return _caseBase;
    }

}
