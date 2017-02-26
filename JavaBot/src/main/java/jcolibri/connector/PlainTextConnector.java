package jcolibri.connector;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import jcolibri.cbrcore.*;
import jcolibri.connector.plaintextutils.PlainTextTypeConverter;
import jcolibri.exception.*;
import jcolibri.util.FileIO;

/**
 * <p>
 * Implements a generic PlainText Connector.
 * </p>
 * It manages the persistence of the cases automatically into textual files.
 * Features:
 * <ul>
 * <li>By default it only can manage a few data types, although developers can
 * add their own ones implementing the TypeAdaptor interface.<br>
 * Supported types and the type extension mechanism is explained in
 * PlainTextTypeConverter.
 * <li>Only works with one file.
 * </ul>
 * <p>
 * This connector uses the property in the initFromXMLfile() parameter to obtain
 * the configuration file. This file is a xml that follows the Schema defined in
 * <a href=
 * "PlainTextConnector.xsd">/doc/configfilesSchemas/PlainTextConnector.xsd</a>:
 * <p>
 * <img src="PlainTextConnectorSchema.jpg">
 * <p>
 * This class does not implement any cache mechanims, so cases are read and
 * written directly. This can be very inefficient in some operations (mainly in
 * reading)
 * <p>
 * Some methods will fail when executing the connector with a case base file
 * inside a jar file. The retrieve() methods will work properly but the methods
 * that write in the file will fail. Extract the file to the file system and run
 * the connector with that location to solve these problems.
 * <p>
 * For an example see Test6.
 *
 * @author Juan Antonio Recio Garc�a
 * @version 2.0
 * @see jcolibri.connector.plaintextutils.PlainTextTypeConverter
 * @see jcolibri.connector.TypeAdaptor
 * @see jcolibri.test.test6.Test6
 */
public class PlainTextConnector implements Connector {

    /* Text file path. */
    protected String PROP_FILEPATH = "";

    /* Columns separator. */
    protected String PROP_DELIM = "";

    private Class<?> descriptionClass;
    private Class<?> solutionClass;
    private Class<?> justOfSolutionClass;
    private Class<?> resultClass;

    List<Attribute> descriptionMaps;
    List<Attribute> solutionMaps;
    List<Attribute> justOfSolutionMaps;
    List<Attribute> resultMaps;

    @Override
    public void close() {
        // does nothing
    }

    /**
     * Deletes cases from the case base. It only uses the case name (primary
     * key) to remove the row. Note that this method is very inefficient because
     * it reads all the database, removes the rows in memory, and writes it
     * again into the text file.
     *
     * @param cases
     *            Cases to delete
     */
    @Override
    public void deleteCases(Collection<CBRCase> cases) {
        try {
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(FileIO.findFile(PROP_FILEPATH).openStream()));
            ArrayList<String> lines = new ArrayList<>();
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || (line.length() == 0)) {
                    lines.add(line);
                    continue;
                }

                StringTokenizer st = new StringTokenizer(line, PROP_DELIM);
                String caseId = st.nextToken();
                for (Iterator<CBRCase> cIter = cases.iterator(); cIter.hasNext();) {
                    CBRCase _case = cIter.next();
                    if (!caseId.equals(_case.getID().toString())) lines.add(line);
                }
            }
            br.close();

            BufferedWriter bw = null;
            bw = new BufferedWriter(new FileWriter(FileIO.findFile(PROP_FILEPATH).getFile(), false));
            for (ListIterator<String> lIter = lines.listIterator(); lIter.hasNext();) {
                line = lIter.next();
                bw.write(line);
                bw.newLine();
            }
            bw.close();

        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass())
                    .error("Error deleting cases " + e.getMessage());
        }
    }

    @Override
    public void initFromXMLfile(URL file) throws InitializingException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file.openStream());

            /** File containing cases */

            PROP_FILEPATH = doc.getElementsByTagName("FilePath").item(0).getTextContent();

            /** Text separator */

            PROP_DELIM = doc.getElementsByTagName("Delimiters").item(0).getTextContent();

            /** classes that compose the case */

            descriptionClass = Class.forName(doc.getElementsByTagName("DescriptionClassName").item(0).getTextContent());

            try {
                solutionClass = Class.forName(doc.getElementsByTagName("SolutionClassName").item(0).getTextContent());
            } catch (Exception e) {}

            try {
                justOfSolutionClass = Class
                        .forName(doc.getElementsByTagName("JustificationOfSolutionClassName").item(0).getTextContent());
            } catch (Exception e) {}

            try {
                resultClass = Class.forName(doc.getElementsByTagName("ResultClassName").item(0).getTextContent());
            } catch (Exception e) {}

            /** Mappings */

            descriptionMaps = findMaps(doc.getElementsByTagName("DescriptionMappings").item(0), descriptionClass);

            if (solutionClass != null)
                solutionMaps = findMaps(doc.getElementsByTagName("SolutionMappings").item(0), solutionClass);

            if (justOfSolutionClass != null)
                justOfSolutionMaps = findMaps(doc.getElementsByTagName("JustificationOfSolutionMappings").item(0),
                        justOfSolutionClass);

            if (resultClass != null)
                resultMaps = findMaps(doc.getElementsByTagName("ResultMappings").item(0), resultClass);

        } catch (Exception e) {
            throw new InitializingException(e);
        }

    }

    /**
     * Retrieves all cases from the text file. It maps data types using the
     * PlainTextTypeConverter class.
     *
     * @return Retrieved cases.
     */
    @Override
    public Collection<CBRCase> retrieveAllCases() {
        LinkedList<CBRCase> cases = new LinkedList<>();
        try {
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(FileIO.openFile(PROP_FILEPATH)));
            String line = "";
            while ((line = br.readLine()) != null) {

                if (line.startsWith("#") || (line.length() == 0)) continue;
                StringTokenizer st = new StringTokenizer(line, PROP_DELIM);

                CBRCase _case = new CBRCase();

                CaseComponent description = (CaseComponent)descriptionClass.newInstance();
                fillComponent(description, st, descriptionMaps, true);
                _case.setDescription(description);

                if (solutionClass != null) {
                    CaseComponent solution = (CaseComponent)solutionClass.newInstance();
                    fillComponent(solution, st, solutionMaps, false);
                    _case.setSolution(solution);
                }
                if (justOfSolutionClass != null) {
                    CaseComponent justificationOfSolution = (CaseComponent)justOfSolutionClass.newInstance();
                    fillComponent(justificationOfSolution, st, justOfSolutionMaps, false);
                    _case.setJustificationOfSolution(justificationOfSolution);
                }
                if (resultClass != null) {
                    CaseComponent result = (CaseComponent)resultClass.newInstance();
                    fillComponent(result, st, resultMaps, false);
                    _case.setResult(result);
                }

                cases.add(_case);
            }
            br.close();
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass())
                    .error("Error retrieving cases " + e.getMessage());
        }
        return cases;
    }

    @Override
    public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Stores the cases in the data base. Note that this method does not control
     * that the case name (== primary key) is repeated, so developers must be
     * careful with this.
     *
     * @param cases
     *            Cases to store.
     * @throws UnImplementedException
     */
    @Override
    public void storeCases(Collection<CBRCase> cases) {
        try {
            BufferedWriter br = null;
            br = new BufferedWriter(new FileWriter(FileIO.findFile(PROP_FILEPATH).getFile(), true));
            char separator = PROP_DELIM.charAt(0);

            for (CBRCase _case : cases) {
                br.newLine();
                StringBuffer line = new StringBuffer();

                CaseComponent description = _case.getDescription();
                writeComponent(description, descriptionMaps, line, separator, true);

                CaseComponent solution = _case.getSolution();
                if (solution != null) {
                    line.append(separator);
                    writeComponent(solution, solutionMaps, line, separator, false);
                }

                CaseComponent justOfSolution = _case.getJustificationOfSolution();
                if (justOfSolution != null) {
                    line.append(separator);
                    writeComponent(justOfSolution, justOfSolutionMaps, line, separator, false);
                }

                CaseComponent result = _case.getResult();
                if (result != null) {
                    line.append(separator);
                    writeComponent(result, resultMaps, line, separator, false);
                }

                br.write(line.toString());
            }
            br.close();
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass()).error(e);
        }
    }

    private void fillComponent(CaseComponent component, StringTokenizer st, List<Attribute> maps, boolean includeId) {
        try {
            Class<?> type;
            Object value;

            if (includeId) {
                Attribute idAttribute = component.getIdAttribute();
                type = idAttribute.getType();
                value = PlainTextTypeConverter.convert(st.nextToken(), type);
                idAttribute.setValue(component, value);
            }

            for (Attribute at : maps) {
                type = at.getType();
                value = PlainTextTypeConverter.convert(st.nextToken(), type);
                at.setValue(component, value);
            }

        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass())
                    .error("Error creating case: " + e.getMessage());
        }
    }

    private List<Attribute> findMaps(Node n, Class<?> _class) {
        List<Attribute> res = new ArrayList<>();
        NodeList childs = n.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node c = childs.item(i);
            if (c.getNodeName().equals("Map")) {
                String attributeName = c.getTextContent();
                res.add(new Attribute(attributeName, _class));
            }
        }
        return res;
    }

    private void writeComponent(CaseComponent comp, List<Attribute> maps, StringBuffer line, char separator,
            boolean includeId) {
        try {
            if (includeId) line.append(comp.getIdAttribute().getValue(comp));
            for (Attribute a : maps) {
                line.append(separator);
                line.append(a.getValue(comp));
            }
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(this.getClass()).error(e);
        }
    }

}
