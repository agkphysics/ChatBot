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

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.extensions.textual.IE.representation.IEText;
import jcolibri.extensions.textual.IE.representation.Token;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This class represents the text similarity function for the chat bot.
 * It uses a combination of word order and semantic similarity measures to
 * determine the similarity of two sentences.
 * 
 * @author Aaron
 */
public class TextSimilarity implements LocalSimilarityFunction {

    /**
     * The threshold above which sentences with different lengths are
     * automatically assumed dissimilar.
     */
    private static final int SIZE_DIFFERENCE_THRESHOLD = 10;
    /**
     * The minimum similarity between words, to be included in the semantic
     * vector.
     */
    private static final double MIN_SIMILARITY = 0.2;
    /**
     * The weighting of the overall similarity between semantic and word-order.
     */
    private static final double DELTA = 0.75;
    
    private static IRAMDictionary dict;
    private static IStemmer stemmer;

    /**
     * Creates a new {@link TextSimilarity} object.
     */
    public TextSimilarity() {}
    
    /**
     * Initialises WordNet using trial and error to find a valid path to either
     * the WordNet database or the serialised file <code>wn.dict</code>.
     * 
     * Either this or {@link TextSimilarity#init(URL)} must be called before
     * any subsequent calls to {@link TextSimilarity#similarity(String, String, POS)}.
     * 
     * @throws IOException
     */
    public static void init() throws IOException {
        try {
            if (Files.exists(Paths.get("wordnet/dict"))) init(new File("wordnet/dict").toURI().toURL());
            else if (Files.exists(Paths.get("../../wordnet/dict"))) init(new File("../../wordnet/dict").toURI().toURL());
            else if (Files.exists(Paths.get("lib/wn.dict"))) init(new File("lib/wn.dict").toURI().toURL());
            else init(TextSimilarity.class.getClassLoader().getResource("wn.dict"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the wordnet dictionary into memory in the form of a
     * {@link RAMDictionary} object. Also loads the stemmer.
     * 
     * Either this or {@link TextSimilarity#init()} must be called before
     * any subsequent calls to {@link TextSimilarity#similarity(String, String, POS)}.
     * 
     * @param url
     *            the URL to either the file <code>wn.dict</code> or the WordNet
     *            DB.
     */
    public static void init(URL url) throws IOException {
        if (dict == null) {
            dict = new RAMDictionary(url, ILoadPolicy.IMMEDIATE_LOAD);
            try {
                dict.open();
                dict.load(true);
                if (!dict.isLoaded()) throw new Exception();
            } catch (Exception e) {
                System.err.println("Error loading wordnet into memory.");
                e.printStackTrace();
            }
            System.out.println("Test similarity between house and apartment: " + similarity("house", "apartment", POS.NOUN));
            System.out.println("Loaded JWI wordnet library.");
        }
        if (stemmer == null) {
            stemmer = new WordnetStemmer(dict);
        }
    }

    /**
     * Computes the similarity of two sentences as a real number between 0 and
     * 1.
     * 
     * @param caseObject
     *                    the case object
     * @param queryObject
     *                    the query object
     * @return
     *         a <code>double</code> from 0 to 1 inclusive that represents
     *         the similarity of the two sentences.
     */
    @Override
    public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {        
        if (!isApplicable(caseObject, queryObject)) {
            throw new NoApplicableSimilarityFunctionException("Cannot compare strings with type "
                    + caseObject.getClass().getName() + " and " + queryObject.getClass().getName());
        }
        else if (caseObject == null || queryObject == null) return 0.0;
        
        // Convert to IEText
        IEText caseText = (IEText)caseObject;
        IEText queryText = (IEText)queryObject;
        
        /*
         * The following algorithm was adapted from one developed by Li et al.
         * "Sentence Similarity Based on Semantic Nets and Corpus Statistics"
         * IEEE Transactions on Knowledge and Data Engineering 18(8):1138-1150
         * 
         * https://www.researchgate.net/publication/232645326_Sentence_Similarity_Based_on_Semantic_Nets_and_Corpus_Statistics
         */
        List<Token> sent1Set = caseText.getAllTokens();
        Set<String> s1WordSet = new HashSet<>();
        List<Token> sent2Set = queryText.getAllTokens();
        Set<String> s2WordSet = new HashSet<>();
        
        Map<String, Integer> s1WordMap = new HashMap<>();
        Map<String, Integer> s2WordMap = new HashMap<>();
        
        // If sentences are of very different length, they are very likely to not be similar at all
        if (Math.abs(sent1Set.size() - sent2Set.size()) > SIZE_DIFFERENCE_THRESHOLD) return 0.0;
        
        Set<Token> jointSet = new HashSet<>();
        Set<String> jointWordSet = new HashSet<>();
        for (int i = 0; i < sent1Set.size(); i++) {
            Token t = sent1Set.get(i);
            // Ignore words not very necessary to convey meaning.
            if (!t.isStopWord() || !t.getPostag().equals(".")) {
                if (!jointWordSet.contains(t.getRawContent().toLowerCase())) {
                    jointSet.add(t);
                }
                jointWordSet.add(t.getRawContent().toLowerCase());
                s1WordSet.add(t.getRawContent().toLowerCase());
                s1WordMap.put(t.getRawContent().toLowerCase(), i);
            }
        }
        for (int i = 0; i < sent2Set.size(); i++) {
            Token t = sent2Set.get(i);
            if (!t.isStopWord() || !t.getPostag().equals(".")) {
                if (!jointWordSet.contains(t.getRawContent().toLowerCase())) {
                    jointSet.add(t);
                }
                jointWordSet.add(t.getRawContent().toLowerCase());
                s2WordSet.add(t.getRawContent().toLowerCase());
                s2WordMap.put(t.getRawContent().toLowerCase(), i);
            }
        }
        
        double[] s1 = new double[jointSet.size()];
        double[] s2 = new double[jointSet.size()];
        double[] r1 = new double[jointSet.size()];
        double[] r2 = new double[jointSet.size()];
        
        Iterator<Token> iter = jointSet.iterator();
        for (int i = 0; i < jointSet.size(); i++) {
            Token t = iter.next();
            POS tPOS = pennToWNPOS(t.getPostag());
            
            if (s1WordSet.contains(t.getRawContent().toLowerCase())) {
                s1[i] = 1.0;
                r1[i] = s1WordMap.get(t.getRawContent().toLowerCase());
            } else if (tPOS == null) {
                s1[i] = 0.0; // Not comparable by semantics using WordNet
                r1[i] = 0.0;
            } else {
                double maxSim = 0.0;
                String maxSimWord = "";
                for (Token word : sent1Set) {
                    POS wordPOS = pennToWNPOS(word.getPostag());
                    double sim = 0.0;
                    if (wordPOS != null && wordPOS.equals(tPOS)) sim = similarity(word.getRawContent(), t.getRawContent(), wordPOS);
                    if (sim > maxSim) {
                        maxSim = sim;
                        maxSimWord = word.getRawContent().toLowerCase();
                    }
                }
                if (maxSim > MIN_SIMILARITY) {
                    s1[i] = maxSim;
                    r1[i] = s1WordMap.get(maxSimWord);
                } else {
                    s1[i] = 0.0;
                    r1[i] = 0.0;
                }
            }
            
            if (s2WordSet.contains(t.getRawContent().toLowerCase())) {
                s2[i] = 1.0;
                r2[i] = s2WordMap.get(t.getRawContent().toLowerCase());
            } else if (tPOS == null) {
                s2[i] = 0.0;
                r2[i] = 0.0;
            } else {
                double maxSim = 0.0;
                String maxSimWord = "";
                for (Token word : sent2Set) {
                    POS wordPOS = pennToWNPOS(word.getPostag());
                    double sim = 0.0;
                    if (wordPOS != null && wordPOS.equals(tPOS)) sim = similarity(word.getRawContent(), t.getRawContent(), wordPOS);
                    if (sim > maxSim) {
                        maxSim = sim;
                        maxSimWord = word.getRawContent().toLowerCase();
                    }
                }
                if (maxSim > MIN_SIMILARITY) {
                    s2[i] = maxSim;
                    r1[i] = s2WordMap.get(maxSimWord);
                } else {
                    s2[i] = 0.0;
                    r1[i] = 0.0;
                }
            }
        }
        
        double prodDiffR = 0.0;
        double prodSumR = 0.0;
        double dotProduct = 0.0;
        double prodS1 = 0.0;
        double prodS2 = 0.0;
        for (int i = 0; i < s1.length; i++) {
            dotProduct += s1[i] * s2[i];
            prodS1 += s1[i] * s1[i];
            prodS2 += s2[i] * s2[i];
            prodDiffR += (r1[i] - r2[i]) * (r1[i] - r2[i]);
            prodSumR += (r1[i] + r2[i]) * (r1[i] + r2[i]);
        }
        
        // Avoid division by zero, which would mean they're not similar anyway
        if (Math.abs(prodSumR) < 1e-6 || Math.abs(prodS1) < 1e-6 || Math.abs(prodS2) < 1e-6) return 0.0;
        
        double Sr = 1 - Math.sqrt(prodDiffR) / Math.sqrt(prodSumR);
        double Ss = dotProduct / (Math.sqrt(prodS1) * Math.sqrt(prodS2));
        
        return DELTA * Ss + (1 - DELTA) * Sr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#
     * isApplicable(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isApplicable(Object caseObject, Object queryObject) {
        return caseObject instanceof IEText || queryObject instanceof IEText;
    }

    /**
     * This function converts a POS from Penn Treebank format to WordNet POS
     * format.
     * 
     * @param pos
     *            the Penn Treebank POS
     * @return the {@link POS} object for this POS.
     */
    public static POS pennToWNPOS(String pos) {
        if (pos.startsWith("JJ")) return POS.ADJECTIVE;
        else if (pos.equals("NN") || pos.equals("NNS")) return POS.NOUN;
        else if (pos.startsWith("RB")) return POS.ADVERB;
        else if (pos.startsWith("VB")) return POS.VERB;
        else return null;
    }

    /**
     * Computes the similarity of two strings with the same part of speech. The
     * algorithm used was adapted from a paper by Yuhua Li et al.
     * "Sentence Similarity Based on Semantic Nets and Corpus Statistics".
     * IEEE Transactions on Knowledge and Data Engineering 18(8):1138-1150 - October 2006.
     * 
     * Available
     * <a href=
     * "https://www.researchgate.net/publication/232645326_Sentence_Similarity_Based_on_Semantic_Nets_and_Corpus_Statistics">
     * here</a>.
     * 
     * @param w1
     *            a non-empty string
     * @param w2
     *            a non-empty string
     * @param pos
     *            the words' part of speech tag, a {@link POS} object.
     * @return the similarity score for the two words, in the range [0, 1]
     */
    public static double similarity(String w1, String w2, POS pos) {
        List<String> w1Stems = stemmer.findStems(w1, pos);
        List<String> w2Stems = stemmer.findStems(w2, pos);
        if (w1Stems.isEmpty() || w2Stems.isEmpty()) {
            // String not found in WN
            return 0.0;
        }
        
        /* In the current corpus there are:
         * 154276 Adjectives
         * 514530 Nouns
         * 553790 Verbs
         * 196954 Adverbs
         */
        
        String w1Stem = w1Stems.get(0);
        String w2Stem = w2Stems.get(0);
        IIndexWord w1idx = dict.getIndexWord(w1Stem, pos);
        IIndexWord w2idx = dict.getIndexWord(w2Stem, pos);

        if (w1idx == null || w2idx == null) return 0.0; // Word not found in WN
        
        if (pos.equals(POS.NOUN) || pos.equals(POS.VERB)) {
            int minDist = Integer.MAX_VALUE;
            ISynsetID minSubsumer = null;
            
            /* Iterate through each possible pair of word senses to find the shortest path
             * via hierarchy. The subsumer that lies on the shortest path is used for
             * calculating depth.
             */
            boolean found = false;
            for (IWordID w1ID : w1idx.getWordIDs()) {
                if (found) break;
                for (IWordID w2ID : w2idx.getWordIDs()) {
                    ISynsetID subsumer;
                    int distance = 0;

                    ISynset w1Synset = dict.getSynset(w1ID.getSynsetID());
                    ISynset w2Synset = dict.getSynset(w2ID.getSynsetID());
                    
                    if (!dict.getSynset(w1Synset.getID())
                            .getRelatedSynsets(Pointer.HYPERNYM_INSTANCE)
                            .isEmpty()
                            || !dict.getSynset(w2Synset.getID())
                            .getRelatedSynsets(Pointer.HYPERNYM_INSTANCE)
                            .isEmpty()) {
                        // Ignore specific instances of types of words. Usually proper nouns incorrectly tagged.
                        return 0.0;
                    }
            
                    if (w2ID.equals(w1ID) || w1Synset.getWords().contains(dict.getWord(w2ID))
                            || w2Synset.getWords().contains(dict.getWord(w1ID))) {
                        // Words are in the same synset
                        subsumer = w1Synset.getID();
                        distance = 0;
                    } else {
                        List<IWord> l = new ArrayList<>(w1Synset.getWords());
                        l.retainAll(w2Synset.getWords());
                        
                        Set<ISynsetID> visitedSynsets = new HashSet<>();
                        visitedSynsets.add(w1Synset.getID());
                        
                        List<ISynsetID> hypernyms = dict.getSynset(w1Synset.getID()).getRelatedSynsets(Pointer.HYPERNYM);
                        while (!hypernyms.isEmpty()) {
                            ISynsetID hypernym = hypernyms.get(0);
                            visitedSynsets.add(hypernym);
                            hypernyms = dict.getSynset(hypernym).getRelatedSynsets(Pointer.HYPERNYM);
                        }
                        ISynsetID currentSynset = w2Synset.getID();
                        while (!visitedSynsets.contains(currentSynset)) {
                            visitedSynsets.add(currentSynset);
                            distance++;
                            hypernyms = dict.getSynset(currentSynset).getRelatedSynsets(Pointer.HYPERNYM);
                            if (pos.equals(POS.VERB) && hypernyms.isEmpty()) return 0.0; // Verbs in different trees, so not related
                            else currentSynset = hypernyms.get(0);
                        }

                        // currentSynset is now the subsumer
                        subsumer = currentSynset;

                        if (!l.isEmpty()) {
                            // Both words' synsets have words in common.
                            distance = 1;
                        } else {
                            // Different synsets
                            currentSynset = w1Synset.getID();
                            while (!currentSynset.equals(subsumer)) {
                                distance++;
                                hypernyms = dict.getSynset(currentSynset).getRelatedSynsets(Pointer.HYPERNYM);
                                currentSynset = hypernyms.get(0);
                            }
                        }
                    }
                    
                    if (distance < minDist) {
                        minDist = distance;
                        minSubsumer = subsumer;
                    }
                    if (minDist == 0) {
                        found = true;
                        break;
                    }
                }
            }
            
            if (dict.getSynset(minSubsumer).getRelatedSynsets(Pointer.HYPERNYM).isEmpty()) return 0.0;
            
            int depth = 0;
            List<ISynsetID> hypernyms = dict.getSynset(minSubsumer).getRelatedSynsets(Pointer.HYPERNYM);
            while (!hypernyms.isEmpty()) {
                depth++;
                ISynsetID hypernym = hypernyms.get(0);
                hypernyms = dict.getSynset(hypernym).getRelatedSynsets(Pointer.HYPERNYM);
            }
    
            double lf = Math.exp(-0.2 * (double)minDist); // exp(-al)
            double df1 = Math.exp(0.45 * (double)depth); // exp(bh)
            double df2 = 1.0 / df1; // exp(-bh)
    
            return lf * (df1 - df2) / (df1 + df2);
        } else {
            // Adjective or adverb
            int minDist = Integer.MAX_VALUE;
            
            /*
             * Iterate through all pairs of words and determine minimum distance
             * amongst adjectives, if they are somewhat related.
             * 
             * TODO: Implement using noun hierarchy via derived form?
             */
            for (IWordID w1ID : w1idx.getWordIDs()) {
                for (IWordID w2ID : w2idx.getWordIDs()) {
                    ISynset w1Synset = dict.getSynset(w1ID.getSynsetID());
                    ISynset w2Synset = dict.getSynset(w2ID.getSynsetID());
                    int distance = 0;
                    
                    if (w2ID.equals(w1ID) || w1Synset.getWords().contains(dict.getWord(w2ID))
                            || w2Synset.getWords().contains(dict.getWord(w1ID))) {
                        // Words are in the same synset
                        distance = 0;
                    } else {
                        List<IWord> l = new ArrayList<>(w1Synset.getWords());
                        l.retainAll(w2Synset.getWords());
                        
                        if (!l.isEmpty()) {
                            // Both words' synsets have words in common.
                            distance = 1;
                        } else {
                            Set<ISynsetID> synonyms = new HashSet<>();
                            synonyms.addAll(w1Synset.getRelatedSynsets(Pointer.SIMILAR_TO));
                            synonyms.retainAll(w2Synset.getRelatedSynsets(Pointer.SIMILAR_TO));
                            if (!synonyms.isEmpty()) {
                                // Words are synonyms in different synsets
                                distance = 2;
                            }
                        }
                    }
                    
                    if (distance < minDist) minDist = distance;
                    if (minDist == 0) return 1.0;
                }
            }
            
            return Math.exp(-0.3 * (double)minDist);
        }
    }
}
