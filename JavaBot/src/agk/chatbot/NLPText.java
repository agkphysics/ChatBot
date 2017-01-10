package agk.chatbot;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import jcolibri.extensions.textual.IE.common.StopWordsDetector;
import jcolibri.extensions.textual.IE.representation.IEText;

/**
 * This class adds some additional functionality to the IEText class.
 * It uses {@link NLPToken} tokens instead of normal tokens, and adds the
 * ability to process a string using the {@code fromString()} function.
 * 
 * @author Aaron
 */
public class NLPText extends IEText {
    
    protected static StanfordCoreNLP pipeline = null;

    /**
     * 
     */
    public NLPText() {
        this("");
    }

    /**
     * Creates a new {@link NLPText} object with the given string.
     * 
     * @param content the plain string representation of the text.
     */
    public NLPText(String content) {
        super(content);
    }
    
    /**
     * Initialises the Stanford CoreNLP pipeline.
     */
    public static void initPipeline() {
        if (pipeline == null) {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            props.setProperty("pos.model", "lib/gate-EN-twitter.model");
            props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
            props.setProperty("ssplit.isOneSentence", "true");
            pipeline = new StanfordCoreNLP(props);
            System.out.println("Loaded Stanford CoreNLP pipeline.");
        }
    }
    
    /**
     * Converts a string into an {@link NLPText} object using some NLP pipeline.
     * 
     * @param content the plain string representation of the text.
     */
    @Override
    public void fromString(String content) {
        rawContent = content;
        
        Annotation doc = new Annotation(content);
        pipeline.annotate(doc);
        
        jcolibri.extensions.textual.IE.representation.Paragraph p = new jcolibri.extensions.textual.IE.representation.Paragraph(content);
        for (CoreMap sentence : doc.get(SentencesAnnotation.class)) {
            String text = sentence.get(TextAnnotation.class);
            jcolibri.extensions.textual.IE.representation.Sentence s = new jcolibri.extensions.textual.IE.representation.Sentence(text);
            
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class);
                String ner = token.get(NamedEntityTagAnnotation.class);
                
                NLPToken t = new NLPToken(word);
                t.setNerTag(ner);
                t.setPostag(pos);
                t.setStem(lemma);
                t.setMainName(!ner.equals("O"));
                t.setStopWord(isStopWord(word));
                s.addToken(t);
            }
            p.addSentence(s);
        }
        addParagraph(p);
    }
    
    private static List<String> stopwords = Arrays.asList(
        "a", "the", "an", "who", "what", "when", "where", "how", "this",
        "that", "it", "so", "no", "not"
    );
    
    private boolean isStopWord(String word) {
        return stopwords.contains(word.toLowerCase());
    }
}
