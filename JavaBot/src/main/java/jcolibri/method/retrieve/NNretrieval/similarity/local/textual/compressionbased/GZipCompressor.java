package jcolibri.method.retrieve.NNretrieval.similarity.local.textual.compressionbased;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class that implements the ICompressor interface, i.e. it defines a function
 * for returning the size of an object (in this case, a String) after
 * compression (in this casse, by GZip).
 *
 * @author Derek Bridge 18/05/07
 *
 */
public class GZipCompressor implements ICompressor {
    /**
     * Returns the size of a string after compression by GZip.
     *
     * @param s
     *            the string to be compressed
     * @return the size of the string after compression by GZip
     */
    @Override
    public int getCompressedSize(String s) {

        ByteArrayOutputStream baos = null;
        BufferedOutputStream out = null;
        try {
            baos = new ByteArrayOutputStream();
            out = new BufferedOutputStream(new GZIPOutputStream(baos));
            byte[] bytes = s.getBytes();
            for (int i = 0; i < bytes.length; i++) {
                out.write(bytes[i]);
            }
            out.flush();
        } catch (Exception e) {

        }
        try {
            if (out != null) out.close();
        } catch (Exception e) {}
        return baos.size();

    }
}
