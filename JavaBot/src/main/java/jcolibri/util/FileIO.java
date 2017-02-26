/**
 * FileIO.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 03/01/2007
 */
package jcolibri.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility functions to transparently access to files in the file system, jar
 * file, classpath...
 *
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public class FileIO {

    /**
     * Returns the URL that localizes a file. It tries the following locations:
     * <ul>
     * <li>The path recieved by parameter
     * <li>"bin/"+path
     * <li>"src/"+path
     * <li>"/"+path
     * <li>path inside a jar
     * <li>"/"+path inside a jar
     * </ul>
     */
    @SuppressWarnings("deprecation")
    public static URL findFile(String file) {

        File f;

        try {
            f = new File(file);
            if (f.exists()) return f.toURL();
        } catch (MalformedURLException e1) {}

        try {
            f = new File("bin/" + file);
            if (f.exists()) return f.toURL();
        } catch (MalformedURLException e1) {}

        try {
            f = new File("src/" + file);
            if (f.exists()) return f.toURL();
        } catch (MalformedURLException e1) {}

        file = file.replace('\\', '/');
        try {
            URL url = FileIO.class.getResource(file);
            if (url != null) return url;
        } catch (Exception e) {}

        try {
            URL url = FileIO.class.getResource("/" + file);
            if (url != null) return url;
        } catch (Exception e) {}

        org.apache.commons.logging.LogFactory.getLog(FileIO.class).warn("File not found: " + file);
        return null;
    }

    /**
     * Tries to return an input stream of the file
     */
    public static InputStream openFile(String file) {
        URL url = null;
        try {
            url = new URL(file);
            return url.openStream();
        } catch (Exception e) {}

        url = findFile(file);
        try {
            return url.openStream();
        } catch (Exception e) {}

        file = file.replace('\\', '/');
        file = file.substring(file.indexOf('!') + 1);
        try {
            return FileIO.class.getResourceAsStream(file);
        } catch (Exception e) {}

        try {
            return FileIO.class.getResourceAsStream("/" + file);
        } catch (Exception e) {}

        org.apache.commons.logging.LogFactory.getLog(FileIO.class).warn("Error opening stream for: " + file);
        return null;
    }
}
