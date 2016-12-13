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
import jcolibri.method.retrieve.NNretrieval.similarity.local.textual.LuceneTextSimilarity;
import jcolibri.method.retrieve.NNretrieval.similarity.local.textual.compressionbased.*;
import jcolibri.method.retrieve.selection.SelectCases;
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

/**
 * @author Aaron Keesing
 *
 */
public class ChatBot implements StandardCBRApplication {
    
    private static Log logger = LogFactory.getLog(ChatBot.class);
    
    private Connector _connector;
    private CBRCaseBase _caseBase;
    private Path corpusPath;
    
    private POSTagger posTagger;
    private TokenNameFinder nameFinder;
    private Stemmer stemmer;

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

        try {
            bot.configure();
            bot.preCycle();
            
            System.out.println("Please start the conversation (enter blank line to exit):");
            java.util.Scanner sc = new java.util.Scanner(System.in);
            String line;
            
            while (!(line = sc.nextLine()).equals("")) {
                CBRQuery query = bot.strToQuery(line);                
                bot.cycle(query);
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
    
    private CBRQuery strToQuery(String line) {
        CBRQuery query = new CBRQuery();
        ChatResponse stmt = new ChatResponse();
        
        IEText txt = new IEText(line);
        Paragraph p = new Paragraph(line);
        Sentence s = new Sentence(line);
        
        String[] sent = SimpleTokenizer.INSTANCE.tokenize(line);
        String[] taggedSent = posTagger.tag(sent);
        Span[] nameSpans = nameFinder.find(sent);
        
        for (int i = 0; i < sent.length; i++) {
            Token t = new Token(sent[i]);
            t.setPostag(taggedSent[i]);
            t.setStem(stemmer.stem(sent[i]).toString().toLowerCase());
            s.addToken(t);
        }
        
        for (Span span : nameSpans) {
            for (int i = span.getStart(); i <= span.getEnd(); i++) s.getTokens().get(i).setMainName(true);
        }
        
        p.addSentence(s);
        txt.addParagraph(p);
        
        stmt.setText(txt);
        query.setDescription(stmt);
        
        return query;
    }
    

    /**
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
            
            TokenNameFinderModel nameModel = new TokenNameFinderModel(new FileInputStream("opennlp-models/en-ner-person.bin"));
            nameFinder = new NameFinderME(nameModel);
            
            stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
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
        //simConfig.addMapping(textAttribute, new StringSimilarity("levenschtein"));
        simConfig.addMapping(textAttribute, new DiceCoefficient());
        
        Collection<RetrievalResult> res = NNScoringMethod.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
        res = SelectCases.selectTopKRR(res, 10);
        
        System.out.println("Found cases:");
        for (RetrievalResult r : res) System.out.println(r.get_case().toString() + " " + r.getEval());
        ChatResponse response = (ChatResponse)res.iterator().next().get_case().getSolution();
        while (response == null || response.getText().toString().equals("")) {
            response = (ChatResponse)res.iterator().next().get_case().getSolution();
        }
        System.out.println(response.toString());
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
        long startTime = System.currentTimeMillis();
        _caseBase.init(_connector);
        long finishTime = System.currentTimeMillis();
        System.out.println("Generated " + _caseBase.getCases().size() + " English response pairs in " + (finishTime - startTime)/1000.0 + " seconds.");
        
        ThesaurusLinker.loadWordNet();
        System.out.println("Loaded WordNet");
        
        logger.debug("Finished preCycle()");
        System.out.println();
        return _caseBase;
    }

}
