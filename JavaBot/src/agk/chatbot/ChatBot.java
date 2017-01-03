/**
 *
 */
package agk.chatbot;

import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.*;
import jcolibri.exception.ExecutionException;
import jcolibri.extensions.textual.IE.common.*;
import jcolibri.extensions.textual.IE.representation.*;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.textual.CosineCoefficient;
import jcolibri.method.retrieve.NNretrieval.similarity.local.textual.DiceCoefficient;
import jcolibri.method.retrieve.selection.SelectCases;

import opennlp.tools.lemmatizer.SimpleLemmatizer;
import opennlp.tools.namefind.*;
import opennlp.tools.postag.*;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.logging.*;

import edu.cmu.lti.imw.InMemoryWordNet;
import edu.cmu.lti.imw.InMemoryWordNetAPI;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.demo.SimilarityCalculationDemo;
import edu.mit.jwi.*;
import edu.mit.jwi.item.*;

/**
 * This class represents the main chat bot application.
 * 
 * @author Aaron Keesing
 * @version 1.7
 */
public class ChatBot implements StandardCBRApplication {
    
    private static Log logger = LogFactory.getLog(ChatBot.class);
    
    private Connector _connector;
    private CBRCaseBase _caseBase;
    private Path corpusPath;
    
    private POSTagger posTagger;
    private TokenNameFinder personNER;
    private TokenNameFinder locationNER;
    private TokenNameFinder organisationNER;
    private Stemmer stemmer;
    private static IRAMDictionary dict;

    /**
     * @param args main arguments to application
     */
    public static void main(String[] args) {
    	
        Path pathToXmls = null;
        try {
	        if (args.length == 1) pathToXmls = Paths.get(args[0]);
	        else pathToXmls = Paths.get("../../TalkBank");
        } catch (InvalidPathException e) {
        	System.err.println("Not a valid path.");
        	System.exit(1);
        }
        
        ChatBot bot = new ChatBot(pathToXmls);
        System.out.println();
        System.out.println("Started bot application");
        
        TextSimilarity.init();
        
//        dict = new RAMDictionary(new File("../../wordnet/dict"));
//		try {
//			dict.open();
//			dict.load(true);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		System.out.println("Loaded JWI wordnet library.");
//		
//		
//		long t0 = System.currentTimeMillis();
//		int num = 0;
//		for (POS pos : POS.values()) {
//			for (Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.hasNext();) {
//				for (IWordID id : i.next().getWordIDs()) {
//					dict.getWord(id).getLemma();
//					num++;
//				}
//			}
//		}
//		long t1 = System.currentTimeMillis();
//		System.out.println(String.format("Traversed %d wordnet lemmas in %.2f seconds", num, (t1 - t0) / 1000.0));
//		System.out.println();
//
        double dist = TextSimilarity.similarity("flat", "apartment", POS.NOUN);
        System.out.println(dist);

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
     * This function converts a string of user input to a query object which
     * can then be used in the CBR application.
     * 
     * @param line the string to convert to a query
     * @return the CBRQuery object for the converted query
     */
    private CBRQuery strToQuery(String line) {
        CBRQuery query = new CBRQuery();
        ChatResponse stmt = new ChatResponse();
        
        IEText txt = new IEText(line);
        Paragraph p = new Paragraph(line);
        Sentence s = new Sentence(line);
        
        String[] sent = SimpleTokenizer.INSTANCE.tokenize(line);
        String[] taggedSent = posTagger.tag(sent);
        Span[] personSpans = personNER.find(sent);
        Span[] locationSpans = locationNER.find(sent);
        Span[] orgSpans = organisationNER.find(sent);
        
        for (int i = 0; i < sent.length; i++) {
            Token t = new Token(sent[i]);
            t.setPostag(taggedSent[i]);
            if ("``''(),--.:$".contains(t.getPostag())) continue; // TODO: Handle punctuation
            t.setStem(stemmer.stem(sent[i]).toString().toLowerCase());
            s.addToken(t);
        }
        
        List<Span> allSpans = new ArrayList<>();
        allSpans.addAll(Arrays.asList(personSpans));
        allSpans.addAll(Arrays.asList(locationSpans));
        allSpans.addAll(Arrays.asList(orgSpans));
        
        for (Span span : allSpans) {
            for (int i = span.getStart(); i <= span.getEnd(); i++) s.getTokens().get(i).setMainName(true);
        }
        
        p.addSentence(s);
        txt.addParagraph(p);
        
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
        
        try {
            POSModel posModel = new POSModel(new FileInputStream("opennlp-models/en-pos-perceptron.bin"));
            posTagger = new POSTaggerME(posModel);
            
            TokenNameFinderModel nerModel;
            nerModel = new TokenNameFinderModel(new FileInputStream("opennlp-models/en-ner-person.bin"));
            personNER = new NameFinderME(nerModel);
            
            nerModel = new TokenNameFinderModel(new FileInputStream("opennlp-models/en-ner-location.bin"));
            locationNER = new NameFinderME(nerModel);
            
            nerModel = new TokenNameFinderModel(new FileInputStream("opennlp-models/en-ner-organization.bin"));
            organisationNER = new NameFinderME(nerModel);
            
            stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
            System.out.println("Loaded OpenNLP tools");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
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
//        simConfig.addMapping(textAttribute, new DiceCoefficient());
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
        final double simTolerance = 0.15;
        final double minSimilarity = 0.3;
        final int maxNumFound = 20;
        for (RetrievalResult r = resIter.next();
        		topEval - r.getEval() < simTolerance && r.getEval() > minSimilarity && topRes.size() <= maxNumFound;
        		r = resIter.next()) {
        	topRes.add(r);
        }
        
        System.out.println(String.format("Found %d cases:", topRes.size()));
        for (RetrievalResult r : topRes) System.out.println(r.get_case().toString() + " " + r.getEval());
        ChatResponse response = (ChatResponse)topRes.iterator().next().get_case().getSolution();
        while (response == null || response.getText().toString().equals("")) {
            response = (ChatResponse)res.iterator().next().get_case().getSolution();
        }
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
