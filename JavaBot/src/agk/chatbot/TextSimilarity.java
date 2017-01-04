package agk.chatbot;

import java.io.File;
import java.io.IOException;
import java.util.*;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.extensions.textual.IE.representation.IEText;
import jcolibri.extensions.textual.IE.representation.Token;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This class represents a text similarity function for the chat bot.
 * 
 * @author Aaron
 */
public class TextSimilarity implements LocalSimilarityFunction {

    private static IRAMDictionary dict;
    private static IStemmer stemmer;

    /**
     * Initialises the {@link TextSimilarity} object. Loads the wordnet
     * dictionary into memory in the form of a {@link RAMDictionary} object.
     */
    public TextSimilarity() {
        init();
    }

    public static void init() {
        if (dict == null) {
            dict = new RAMDictionary(new File("../../wordnet/dict"));
            try {
                dict.open();
                dict.load(true);
            } catch (IOException e1) {
                System.err.println("Error loading wordnet into memory.");
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                System.err.println("Interrupted while loading wordnet into memory.");
                e1.printStackTrace();
            }
            System.out.println("Loaded JWI wordnet library.");
        }
        if (stemmer == null) {
            stemmer = new WordnetStemmer(dict);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction#
     * compute(java.lang.Object, java.lang.Object)
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
         * The following algorithm was developed by Li et al.
         * "Sentence Similarity Based on Semantic Nets and Corpus Statistics"
         * IEEE Transactions on Knowledge and Data Engineering 18(8):1138-1150
         * 
         * https://www.researchgate.net/publication/232645326_Sentence_Similarity_Based_on_Semantic_Nets_and_Corpus_Statistics
         */
        List<Token> sent1Set = caseText.getAllTokens();
        Set<String> s1WordSet = new HashSet<>();
        List<Token> sent2Set = queryText.getAllTokens();
        Set<String> s2WordSet = new HashSet<>();
        
        Set<Token> jointSet = new HashSet<>();
        Set<String> jointWordSet = new HashSet<>();
        for (Token t : sent1Set) {
            if (!jointWordSet.contains(t.getRawContent().toLowerCase())) {
                jointSet.add(t);
                jointWordSet.add(t.getRawContent().toLowerCase());
            }
            s1WordSet.add(t.getRawContent().toLowerCase());
        }
        for (Token t : sent2Set) {
            if (!jointWordSet.contains(t.getRawContent().toLowerCase())) {
                jointSet.add(t);
                jointWordSet.add(t.getRawContent().toLowerCase());
            }
            s2WordSet.add(t.getRawContent().toLowerCase());
        }
        
        double[] s1 = new double[jointSet.size()];
        double[] s2 = new double[jointSet.size()];
        
        Iterator<Token> iter = jointSet.iterator();
        for (int i = 0; i < jointSet.size(); i++) {
            Token t = iter.next();
            POS tPOS = pennToWNPOS(t.getPostag());
            final double THRESHOLD = 0.3;
            
            if (s1WordSet.contains(t.getRawContent().toLowerCase())) {
                s1[i] = 1.0;
            } else if (tPOS == null) {
                s1[i] = 0.0; // Not comparable by semantics using WordNet
            } else {
                double maxSim = 0.0;
                for (Token word : sent1Set) {
                    POS wordPOS = pennToWNPOS(word.getPostag());
                    double sim = 0.0;
                    if (wordPOS != null && wordPOS.equals(tPOS)) sim = similarity(word.getRawContent(), t.getRawContent(), wordPOS);
                    if (sim > maxSim) maxSim = sim;
                }
                if (maxSim > THRESHOLD) s1[i] = maxSim;
                else s1[i] = 0.0;
            }
            
            if (s2WordSet.contains(t.getRawContent().toLowerCase())) {
                s2[i] = 1.0;
            } else if (tPOS == null) {
                s2[i] = 0.0;
            } else {
                double maxSim = 0.0;
                for (Token word : sent2Set) {
                    POS wordPOS = pennToWNPOS(word.getPostag());
                    double sim = 0.0;
                    if (wordPOS != null && wordPOS.equals(tPOS)) sim = similarity(word.getRawContent(), t.getRawContent(), wordPOS);
                    if (sim > maxSim) maxSim = sim;
                }
                if (maxSim > THRESHOLD) s2[i] = maxSim;
                else s2[i] = 0.0;
            }
        }
        
        double dotProduct = 0.0;
        double prodS1 = 0.0;
        double prodS2 = 0.0;
        for (int i = 0; i < s1.length; i++) {
            dotProduct += s1[i] * s2[i];
            prodS1 += s1[i] * s1[i];
            prodS2 += s2[i] * s2[i];
        }
        
        return dotProduct / (Math.sqrt(prodS1) * Math.sqrt(prodS2));
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
        init();
        
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
             * TODO: Implement using noun hierarchy via derived form
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
