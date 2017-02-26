/**
 * Launcher.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 10/01/2007
 */
package jcolibri.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * This class launches a jCOLIBRI application loading dinamically its libraries.
 * It has a method that parses the ".classpath" file of the eclipse project to
 * find the libraries that a project needs. Then it loads the libraries and
 * executes an application. This eases the process of invoking a jCOLIBRI
 * application.
 *
 * @author Juan A. Recio Garcia
 * @version 2.0
 */
public class Launcher {

    /**
     * Loads a resource pointed by a URL into the Class Loader.
     */
    public static void addURLtoClassLoader(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            // Please don't read this code. It is a dark way to invoke a private
            // method using reflection.
            // The clear way consists on creating your own subclass of
            // URLClassLoader.
            Class<?>[] parameters = new Class<?>[] { URL.class };
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        } // end try catch
    }// end method

    /**
     * Main method used to launch any application loading the libraries from a
     * eclipse classpath file. The first argument must be the class to launch
     * and the second (optional) is the name of the eclipse classpath file. If
     * this second argument is not specified it tries with ".classpath".
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: jcolibri.util.Launcher MainClass [eclipse_classpath_file]");
            System.exit(0);
        }

        try {
            String classpathfile = ".classpath";
            if (args.length == 2) classpathfile = args[1];

            System.out.println("Loading class path from file: " + classpathfile);

            URL[] libraries = getClassPath(classpathfile);
            for (int i = 0; i < libraries.length; i++) {
                addURLtoClassLoader(libraries[i]);
                System.out.println("Adding library: " + libraries[i]);
            }

            Class<?> mainClass = Class.forName(args[0]);
            org.apache.commons.logging.LogFactory.getLog(Launcher.class).info("Executing class: " + args[0]);
            Method mainMethod = mainClass.getMethod("main", args.getClass());

            String[] newargs = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
                newargs[i - 1] = args[1];

            Object[] methodparams = new Object[1];
            methodparams[0] = newargs;
            mainMethod.invoke(null, methodparams);
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(Launcher.class).error("Launching class " + e.getMessage());
            e.printStackTrace();
        }
        org.apache.commons.logging.LogFactory.getLog(Launcher.class).info("Launch finished");
        // System.exit(0);
    }

    /**
     * Parses a eclipse classpath file returning a list of URLs to the libraries
     * in that file.
     */
    protected static URL[] getClassPath(String classpathfile) throws Exception {

        java.util.ArrayList<URL> CLASSPATH = new java.util.ArrayList<>();
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(classpathfile);

            NodeList nl = doc.getElementsByTagName("classpathentry");
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                NamedNodeMap nnm = n.getAttributes();
                if (nnm.getNamedItem("kind").getNodeValue().equals("lib")) {
                    String lib = nnm.getNamedItem("path").getNodeValue();
                    File f = new File(lib);
                    CLASSPATH.add(f.toURI().toURL());
                }
            }
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(Launcher.class)
                    .error("Error obtaining classpath. Revise your classpath file \n" + e.getMessage());
            throw e;
        }

        return CLASSPATH.toArray(new URL[] {});
    }

}
