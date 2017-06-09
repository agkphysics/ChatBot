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

package agk.chatbot.nlp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class defines an annotator that uses Stanford CoreNLP to process text.
 *
 * @author Aaron
 */
public class StanfordAnnotator implements BotNLPAnnotator {

    private static AnnotationPipeline pipeline;

    /**
     * Creates a new annotator using CoreNLP.
     */
    public StanfordAnnotator() {
        initAnnotator();
    }

    /**
     * Initialises the Stanford CoreNLP pipeline.
     *
     * This method looks for the file <code>gate-EN-twitter.model</code> in a
     * number of common directories, such as <code>lib/</code>. If it cannot
     * find the file, it uses a fallback POS tagger model distributed with
     * Stanford CoreNLP.
     */
    @Override
    public boolean initAnnotator() {
        if (pipeline == null) {
            String posModel;
            if (Files.exists(Paths.get("lib/gate-EN-twitter.model"))
                    || NLPText.class.getClassLoader().getResource("lib/gate-EN-twitter.model") != null) {
                posModel = "lib/gate-EN-twitter.model";
            } else if (Files.exists(Paths.get("gate-EN-twitter.model"))
                    || NLPText.class.getClassLoader().getResource("gate-EN-twitter.model") != null) {
                posModel = "gate-EN-twitter.model";
            } else {
                posModel = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
                System.out.println("Warning: gate-EN-twitter.model not found, reverting to fallback POS tag model.");
            }

            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            props.setProperty("pos.model", posModel);
            props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
            pipeline = new StanfordCoreNLP(props);
            System.out.println("Loaded Stanford CoreNLP pipeline.");
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see agk.chatbot.nlp.BotNLPAnnotator#processString(java.lang.String)
     */
    @Override
    public NLPText processString(String content) {
        NLPText txt = new NLPText(content);
        Annotation doc = new Annotation(content);
        pipeline.annotate(doc);

        for (CoreMap sentence : doc.get(SentencesAnnotation.class)) {
            String text = sentence.get(TextAnnotation.class);
            NLPSentence s = new NLPSentence(text);

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
                t.setStopWord(NLPText.isStopWord(word));
                s.addToken(t);
            }
            txt.addSentence(s);
        }

        return txt;
    }

}
