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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

import opennlp.tools.namefind.*;
import opennlp.tools.postag.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

/**
 * This class defines an annotator that uses OpenNLP to process text. OpenNLP
 * has a lower memory footprint than CoreNLP.
 *
 * @author Aaron
 */
public class OpenNLPAnnotator implements BotNLPAnnotator {

    private SentenceDetector sentDetector;
    private POSTagger posTagger;
    private TokenNameFinder personNER;
    private TokenNameFinder locationNER;
    private TokenNameFinder organisationNER;
    private Stemmer stemmer;
    private boolean initialised = false;

    /**
     * Creates a new annotator using OpenNLP.
     */
    public OpenNLPAnnotator() {
        initAnnotator();
    }

    /**
     * Initialises the annotator with the OpenNLP models.
     */
    @Override
    public boolean initAnnotator() {
        if (!initialised) {
            try {
                URI modelsURI = getClass().getClassLoader().getResource("opennlp-models/").toURI();
                FileObject modelsDir = VFS.getManager().resolveFile(modelsURI);
                
                SentenceModel sentModel = new SentenceModel(modelsDir.resolveFile("en-sent.bin").getContent().getInputStream());
                sentDetector = new SentenceDetectorME(sentModel);

                POSModel posModel = new POSModel(modelsDir.resolveFile("en-pos-perceptron.bin").getContent().getInputStream());
                posTagger = new POSTaggerME(posModel);

                TokenNameFinderModel nerModel;
                nerModel = new TokenNameFinderModel(modelsDir.resolveFile("en-ner-person.bin").getContent().getInputStream());
                personNER = new NameFinderME(nerModel);

                nerModel = new TokenNameFinderModel(modelsDir.resolveFile("en-ner-location.bin").getContent().getInputStream());
                locationNER = new NameFinderME(nerModel);

                nerModel = new TokenNameFinderModel(modelsDir.resolveFile("en-ner-organization.bin").getContent().getInputStream());
                organisationNER = new NameFinderME(nerModel);

                stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

                initialised = true;
                System.out.println("Loaded OpenNLP tools");
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                initialised = false;
                return false;
            }
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

        String[] sents = sentDetector.sentDetect(content);
        for (String sent : sents) {
            NLPSentence s = new NLPSentence(sent);
            String[] toks = SimpleTokenizer.INSTANCE.tokenize(sent);
            String[] taggedSent = posTagger.tag(toks);
            Span[] personSpans = personNER.find(toks);
            Span[] locationSpans = locationNER.find(toks);
            Span[] orgSpans = organisationNER.find(toks);

            for (int i = 0; i < toks.length; i++) {
                NLPToken t = new NLPToken(toks[i]);
                t.setPostag(taggedSent[i]);
                t.setStem(stemmer.stem(toks[i]).toString().toLowerCase());
                t.setStopWord(NLPText.isStopWord(toks[i]));
                s.addToken(t);
            }

            for (Span span : personSpans) {
                for (int i = span.getStart(); i <= span.getEnd(); i++) {
                    s.getTokens().get(i).setMainName(true);
                    s.getTokens().get(i).setNerTag("PERSON");
                }
            }
            for (Span span : locationSpans) {
                for (int i = span.getStart(); i <= span.getEnd(); i++) {
                    s.getTokens().get(i).setMainName(true);
                    s.getTokens().get(i).setNerTag("LOCATION");
                }
            }
            for (Span span : orgSpans) {
                for (int i = span.getStart(); i <= span.getEnd(); i++) {
                    s.getTokens().get(i).setMainName(true);
                    s.getTokens().get(i).setNerTag("ORGANIZATION");
                }
            }

            txt.addSentence(s);
        }

        return txt;
    }
}
