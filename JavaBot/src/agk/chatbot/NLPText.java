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

import java.nio.file.*;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;

import jcolibri.extensions.textual.IE.representation.IEText;

/**
 * This class adds some additional functionality to the IEText class.
 * It uses {@link NLPToken} tokens instead of normal tokens, and adds the
 * ability to process a string using the {@code fromString()} function.
 * 
 * @author Aaron
 */
public class NLPText extends IEText {
    
    protected static AnnotationPipeline pipeline = null;

    /**
     * Creates an {@link NLPText} object containing the empty string.
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
     * 
     * This method looks for the file <code>gate-EN-twitter.model</code> in a
     * number of common directories, such as <code>lib/</code>. If it cannot
     * find the file, it uses a fallback POS tagger model distributed with
     * Stanford CoreNLP.
     */
    public static void initPipeline() {
        if (pipeline == null) {
            String posModel = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
            if (Files.exists(Paths.get("lib/gate-EN-twitter.model")) || NLPText.class.getClassLoader().getResource("lib/gate-EN-twitter.model") != null) {
                posModel = "lib/gate-EN-twitter.model";
            } else if (Files.exists(Paths.get("gate-EN-twitter.model")) || NLPText.class.getClassLoader().getResource("gate-EN-twitter.model") != null) {
                posModel = "gate-EN-twitter.model";
            } else {
                System.out.println("Warning: gate-EN-twitter.model not found, reverting to fallback POS tag model.");
            }
            
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            props.setProperty("pos.model", posModel);
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
    
    private final static List<String> STOPWORDS = Arrays.asList(
        "a", "the", "an", "who", "what", "when", "where", "how", "this",
        "that", "it", "so", "no", "not", "but", "and", "okay"
    );
    
    /**
     * Determines whether or not the given word is a stop word.
     * 
     * @param word
     *             the word to test
     * @return a boolean value indicating whether or not the given word is a
     *         stop word.
     */
    public static boolean isStopWord(String word) {
        return STOPWORDS.contains(word.toLowerCase());
    }
}
