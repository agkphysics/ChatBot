package jcolibri.method.retrieve.NNretrieval.similarity.local.textual.compressionbased;

import jcolibri.datatypes.Text;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns the similarity of two strings using text compression.
 *
 * Compression Based Dissimilarity (CDM) is defined as follows CDM(x, y) = C(xy)
 * / (C(x) + C(y)) where C(x) is the size of string x after compression (and
 * C(y) similarly) and C(xy) is the size, after compression, of the string that
 * comprises y concatenated to the end of x.
 *
 * See the following paper:
 *
 * <pre>
 * inproceedings{Keogh04,
 * author = {E. Keogh and S. Lonardi and C. Ratanamahatana},
 * title ={Towards Parameter-Free Data Mining},
 * booktitle = {Procs.\ of the 10th ACM SIGKDD, International Conference on Knowledge Discovery and Data Mining},
 * year = {2004},
 * pages = {206--215},
 * address = {New York, NY, USA},
 * }
 * </pre>
 *
 * Note that CDM is not a distance metric. It is NOT in general the case that
 * CDM(x, y) = 0 iff x = y CDM(x, y) = CDM(y, x) CDM(x, y) + CDM(y, z) >= CDM(x,
 * z) Do not use this measure in any application that requires metric
 * properties.
 *
 * Values of CDM will lies in the range (0.5, 1.0].
 *
 * We convert to a similarity measure by returning 1.0 - CDM(x, y).
 *
 * This similarity measure is effective in case-based spam filtering using GZip
 * and PPM text compression algorithms. For the details, see:
 *
 * <pre>
 * inproceedings{Delany-Bridge-2006,
 * author = "S. J. Delany and D. Bridge",
 * title = "Feature-Based and Feature-Free Textual {CBR}: A Comparison in Spam Filtering",
 * booktitle = "Procs.\ of the 17th Irish Conference on Artificial Intelligence and Cognitive Science",
 * pages = "244--253",
 * address = "Belfast, Northern Ireland",
 * year = "2006"
 * }
 * </pre>
 *
 * Its effectiveness outside of spam filtering (e.g. texts other than emails,
 * short strings, images, etc.) has not been demonstrated. To be effective, the
 * 'right' compression algorithm must be used.
 *
 * @author Derek Bridge 18/05/07
 *
 */
public class CompressionBased implements LocalSimilarityFunction {
    private ICompressor compressor;

    public static void main(String[] args) {
        CompressionBased a = new CompressionBased(new GZipCompressor());
        double res = a.compare("Hello", "Goodbye");
        System.out.println("Hello-Goodbye: " + res);
        res = a.compare("aaaaaaaaaaaaaaaaaaaaaaa", "zzzzzzzzzzzzzzzzzzzzzzzz");
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
    public CompressionBased(ICompressor compressor) {
        this.compressor = compressor;
    }

    /**
     * Applies the similarity function.
     *
     * @param x
     * @param y
     * @return the simlarity value.
     */
    public double compare(String x, String y) {
        return 1.0 - CDM(x, y);
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
    private double CDM(String x, String y) {
        return compressor.getCompressedSize(x + y) * 1.0f
                / (compressor.getCompressedSize(x) + compressor.getCompressedSize(y));
    }
}
