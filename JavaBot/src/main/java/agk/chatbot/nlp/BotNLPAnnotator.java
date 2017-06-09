package agk.chatbot.nlp;

/**
 * This interface represents a generic NLP annotator that can annotate some
 * text.
 *
 * @author Aaron
 */
public interface BotNLPAnnotator {

    /**
     * Initialises the annotator. This method should immediately return if the
     * annotator is already initialised.
     *
     * @return <code>true</code> if successfully initialised, <code>false</code>
     *         otherwise.
     */
    public boolean initAnnotator();

    /**
     * Annotates the given text and creates an {@link NLPText} object which
     * contains the NLP information.
     *
     * @param content
     *            the string to process
     * @return an <code>NLPText</code> object containing the annotated content
     */
    public NLPText processString(String content);
}
