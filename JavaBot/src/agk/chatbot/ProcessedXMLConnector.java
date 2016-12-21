/**
 *
 */
package agk.chatbot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import jcolibri.cbrcore.*;
import jcolibri.exception.InitializingException;
import jcolibri.extensions.textual.IE.representation.IEText;
import jcolibri.extensions.textual.IE.representation.Paragraph;
import jcolibri.extensions.textual.IE.representation.Sentence;
import jcolibri.extensions.textual.IE.representation.Token;

/**
 * This class implements the connector between the processed chat corpus and the
 * chat bot.
 *
 * @author Aaron
 */
public class ProcessedXMLConnector implements Connector {

    private List<File> xmlFiles;
    
    /**
     *
     */
    public ProcessedXMLConnector(Path pathToXmls) {
    	System.out.println("Using corpus at " + pathToXmls.toAbsolutePath().normalize().toString());
        
        xmlFiles = new ArrayList<>();
        
        try {
            Files.walkFileTree(pathToXmls, new FileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) xmlFiles.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        System.out.println("Found " + xmlFiles.size() + " processed xml files.");
    }
    
    public static Collection<CBRCase> convertFileToCases(File file) {
        List<CBRCase> cases = new ArrayList<>();
        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            Element root = doc.getDocumentElement();
            NodeList pairs = doc.getElementsByTagName("ResponsePair");
            
            for (int i = 0; i < pairs.getLength(); i++) {
                Element pair = (Element)pairs.item(i);
                
                ChatResponse stmt = new ChatResponse();
                ChatResponse resp = new ChatResponse();
                CBRCase c = new CBRCase();
                
                stmt.setId(root.getAttribute("id"));
                resp.setId(root.getAttribute("id"));
                
                IEText txt;
                String utterance;
                Sentence sent;
                Paragraph para;
                
                List<Token> tokens = new ArrayList<>();
                List<String> words = new ArrayList<>();
                
                Element statement = (Element)pair.getElementsByTagName("statement").item(0);
                NodeList toks = statement.getElementsByTagName("word");
                // Convert statement
                for (int j = 0; j < toks.getLength(); j++) {
                    Element w = (Element)toks.item(j);
                    String pos = w.getAttribute("tag");
                    String stem = w.getAttribute("stem");
                	Token t = new Token(w.getTextContent());
                	t.setPostag(pos);
                	t.setStem(stem);
                	tokens.add(t);
                	words.add(w.getTextContent());
                }
                utterance = String.join(" ", words);
                sent = new Sentence(utterance);
                para = new Paragraph(utterance);
                txt = new IEText(utterance);
                sent.addTokens(tokens);
                para.addSentence(sent);
                txt.addParagraph(para);
                stmt.setText(txt);            
                
                tokens.clear();
                words.clear();
                
                Element response = (Element)pair.getElementsByTagName("response").item(0);
                toks = response.getElementsByTagName("word");
                // Convert response
                for (int j = 0; j < toks.getLength(); j++) {
                    Element w = (Element)toks.item(j);
                    String pos = w.getAttribute("tag");
                    String stem = w.getAttribute("stem");
                    Token t = new Token(w.getTextContent());
                    t.setPostag(pos);
                    t.setStem(stem);
                    tokens.add(t);
                    words.add(w.getTextContent());
                }
                utterance = String.join(" ", words);
                sent = new Sentence(utterance);
                para = new Paragraph(utterance);
                txt = new IEText(utterance);
                sent.addTokens(tokens);
                para.addSentence(sent);
                txt.addParagraph(para);
                resp.setText(txt);
                
                c.setDescription(stmt);
                c.setSolution(resp);
                cases.add(c);
            }
        } catch (DOMException | ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        
        return cases;
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#close()
     */
    @Override
    public void close() {
        xmlFiles.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#deleteCases(java.util.Collection)
     */
    @Override
    public void deleteCases(Collection<CBRCase> cases) {}

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#initFromXMLfile(java.net.URL)
     */
    @Override
    public void initFromXMLfile(URL file) throws InitializingException {}

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#retrieveAllCases()
     */
    @Override
    public Collection<CBRCase> retrieveAllCases() {
        Collection<CBRCase> coll = Collections.synchronizedCollection(new ArrayList<>());
        
        ExecutorService executor = Executors.newWorkStealingPool();
        
        for (File file : xmlFiles) {
        	executor.submit(new Runnable() {
				@Override
				public void run() {
					coll.addAll(convertFileToCases(file));
				}
			});
//            coll.addAll(convertFileToCases(file));
        }
        
        executor.shutdown();
        try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return coll;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#retrieveSomeCases(jcolibri.cbrcore.
     * CaseBaseFilter)
     */
    @Override
    public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter filter) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#storeCases(java.util.Collection)
     */
    @Override
    public void storeCases(Collection<CBRCase> cases) {}
}
