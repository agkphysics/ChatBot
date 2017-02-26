package agk.chatbot.test;

import static org.junit.Assert.*;

import org.junit.Test;

import agk.chatbot.cbr.sim.TextSimilarity;
import agk.chatbot.nlp.NLPText;
import edu.mit.jwi.item.POS;

/**
 * Some tests for the similarity functions.
 * 
 * @author Aaron
 */
public class SimilarityTest {
    
    /**
     * Tests whether the WordNet interface initialises properly.
     * Test method for {@link TextSimilarity#init()}.
     * @throws Exception
     */
    @Test
    public void testInit() throws Exception {
        TextSimilarity.init();
    }

    /**
     * Test method for {@link TextSimilarity#wnSimilarity(String, String, POS)}.
     * @throws Exception 
     */
    @Test
    public final void testWnSimilarityStringStringPOS() throws Exception {
        TextSimilarity.init();
        assertEquals(0.6, TextSimilarity.wnSimilarity("house", "apartment", POS.NOUN), 0.1);
    }

    /**
     * Test method for {@link TextSimilarity#wnSimilarity(String, String, POS, POS)}.
     */
    @Test
    public final void testWnSimilarityStringStringPOSPOS() throws Exception {
        TextSimilarity.init();
        assertEquals(0.6, TextSimilarity.wnSimilarity("house", "apartment", POS.NOUN, POS.NOUN), 0.1);
    }

    /**
     * Test method for {@link TextSimilarity#isApplicable(Object, Object)}.
     */
    @Test
    public final void testIsApplicable1() throws Exception {
        TextSimilarity.init();
        TextSimilarity t = new TextSimilarity();
        assertTrue(t.isApplicable(new NLPText(), new NLPText()));
    }
    
    /**
     * Test method for {@link TextSimilarity#isApplicable(Object, Object)}.
     */
    @Test
    public void testIsApplicable2() throws Exception {
        TextSimilarity.init();
        TextSimilarity t = new TextSimilarity();
        assertFalse(t.isApplicable(new String(), new NLPText()));
    }

}
