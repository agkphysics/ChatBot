package jcolibri.method.retrieve.NNretrieval.similarity.local.textual.compressionbased;

import jcolibri.datatypes.Text;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns the similarity of two strings using text compression.
 *
 * Normalised Compression Distance (NCD) is defined as follows NCD(x, y) =
 * (C(xy) - min(C(X), C(y)) / max(C(x), C(y)) where C(x) is the size of string x
 * after compression (and C(y) similarly) and C(xy) is the size, after
 * compression, of the string that comprises y concatenated to the end of x.
 *
 * See the following paper
 *
 * <pre>
 * inproceedings{Li03, author = "M. Li and X. Chen and X. Li and B. Ma and P.
 *                      Vitanyi", title = "The Similarity Metric", booktitle =
 *                      {Procs.\ of the 14th Annual ACM-SIAM Symposium on
 *                      Discrete Algorithms}, pages = {863--872}, address =
 *                      {Baltimore, Maryland}, year = "2003", }
 * </pre>
 *
 * Note that NCD is not a distance metric. It is NOT in general the case that
 * NCD(x, y) = 0 iff x = y NCD(x, y) = NCD(y, x) NCD(x, y) + NCD(y, z) >= NCD(x,
 * z) Do not use this measure in any application that requires metric
 * properties.
 *
 * Values of NCD will lies in the range [0.0, 1.0 + e] where e is some small
 * value.
 *
 * We convert to a similarity measure by returning 1.0 - min(NCD(x, y), 1.0).
 *
 * This similarity measure is effective in case-based spam filtering using the
 * GZip text compression algorithm. For the details, see:
 *
 * <pre>
 * inproceedings{Delany-Bridge-2007, author = "S. J. Delany and D. Bridge",
 *                                    title = "Catching the Drift: Using
 *                                    Feature-Free Case-Based Reasoning for Spam
 *                                    Filtering", booktitle = "Procs.\ of the
 *                                    7th International Conference on Case Based
 *                                    Reasoning", address = "Belfast, Northern
 *                                    Ireland", year = "2007" }
 * </pre>
 *
 * Its effectiveness outside of spam filtering (e.g. texts other than emails,
 * short strings, images, etc.) has not been demonstrated. To be effective, the
 * 'right' compression algorithm must be used.
 *
 * @author Derek Bridge 18/05/07
 *
 */
public class NormalisedCompression implements LocalSimilarityFunction {
    private ICompressor compressor;

    public static void main(String[] args) {
        NormalisedCompression a = new NormalisedCompression(new GZipCompressor());
        double res = a.compare("Hello", "Goodbye");
        System.out.println("Hello-Goodbye: " + res);
        res = a.compare("aaaaaaaaaaaaaaaaaaaaaaaaaa", "zzzzzzzzzzzzzzzzzzz");
        System.out.println("a-z: " + res);
        res = a.compare("Hello", "Hello");
        System.out.println("Hello-Hello: " + res);
        res = a.compare("abcdefghijkl", "bcdefghijk");
        System.out.println("abcdefghijkl-bcd: " + res);
    }

    /**
     * @param compressor
     *            an object that encapsulates the compression algorithm to be
     *            used.
     */
    public NormalisedCompression(ICompressor compressor) {
        this.compressor = compressor;
    }

    /**
     * Applies the similarity function.
     *
     * @param x
     * @param y
     * @return the similarity value.
     */
    public double compare(String x, String y) {
        return 1.0 - Math.min(NCD(x, y), 1.0);
    }

    /**
     * Applies the similarity function.
     *
     * @param o1
     *            String
     * @param o2
     *            String
     * @return result of apply the similarity function.
     */
    @Override
    public double compute(Object o1, Object o2) throws jcolibri.exception.NoApplicableSimilarityFunctionException {
        if ((o1 == null) || (o2 == null)) return 0;
        if (!((o1 instanceof java.lang.String) || (o1 instanceof Text)))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
        if (!((o2 instanceof java.lang.String) || (o1 instanceof Text)))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o2.getClass());

        if (o1 instanceof Text) o1 = ((Text)o1).toString();
        if (o2 instanceof Text) o2 = ((Text)o2).toString();

        String s1 = (String)o1;
        String s2 = (String)o2;
        double res = compare(s1, s2);
        return res;
    }

    @Override
    public boolean isApplicable(Object o1, Object o2) {
        if ((o1 == null) && (o2 == null)) return true;
        else if (o1 == null) return (o2 instanceof String) || (o2 instanceof Text);
        else if (o2 == null) return (o1 instanceof String) || (o1 instanceof Text);
        else return ((o1 instanceof String) && (o2 instanceof String))
                || ((o1 instanceof Text) && (o2 instanceof Text));
    }

    /**
     * Applies the dissimilarity function.
     *
     * @param x
     * @param y
     * @return
     */
    private double NCD(String x, String y) {
        int cxy = compressor.getCompressedSize(x + y);
        int cx = compressor.getCompressedSize(x);
        int cy = compressor.getCompressedSize(y);
        return (cxy - Math.min(cx, cy)) * 1.0 / Math.max(cx, cy);
    }
}
