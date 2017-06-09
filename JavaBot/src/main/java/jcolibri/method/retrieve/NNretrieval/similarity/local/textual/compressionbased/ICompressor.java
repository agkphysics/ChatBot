package jcolibri.method.retrieve.NNretrieval.similarity.local.textual.compressionbased;

/**
 * Interface that defines a function for returning the size of a string after
 * compression.
 *
 * @author Derek Bridge 18/05/07
 *
 */
public interface ICompressor {
    /**
     * Returns the size of a string after compression.
     *
     * @param s
     *            the string to be compressed
     * @return the size of the string after compression
     */
    public int getCompressedSize(String s);
}
