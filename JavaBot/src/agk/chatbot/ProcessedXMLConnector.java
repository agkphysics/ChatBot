package agk.chatbot;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import jcolibri.cbrcore.*;
import jcolibri.exception.InitializingException;
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
    private Path xmlPath;
    
    /**
     *
     */
    public ProcessedXMLConnector(Path xmlPath) {
        this.xmlPath = xmlPath;
    	System.out.println("Using corpus at " + xmlPath.toAbsolutePath().normalize().toString());
        
        xmlFiles = new ArrayList<>();
        
        try {
            Files.walkFileTree(xmlPath, new FileVisitor<Path>() {
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
            NodeList utters = doc.getElementsByTagName("u");
            
            for (int i = 0; i < utters.getLength(); i++) {
                ChatResponse stmt = new ChatResponse();
                ChatResponse resp = new ChatResponse();
                CBRCase c = new CBRCase();
                
                stmt.setId(root.getAttribute("id"));
                resp.setId(root.getAttribute("id"));
                
                NLPText txt;
                String utterance;
                Sentence sent;
                Paragraph para;
                
                List<Token> tokens = new ArrayList<>();
                List<String> words = new ArrayList<>();
                
                Element u = (Element)utters.item(i);
                NodeList toks = u.getElementsByTagName("t");
                // Convert statement
                for (int j = 0; j < toks.getLength(); j++) {
                    Element w = (Element)toks.item(j);
                    String pos = w.getAttribute("pos");
                    String stem = w.getAttribute("stem");
                	Token t = new Token(w.getTextContent());
                	t.setPostag(pos);
                	t.setStem(stem);
                	t.setMainName(!w.getAttribute("ner").equals("O"));
                	tokens.add(t);
                	words.add(w.getTextContent());
                }
                utterance = String.join(" ", words);
                sent = new Sentence(utterance);
                para = new Paragraph(utterance);
                txt = new NLPText(utterance);
                sent.addTokens(tokens);
                para.addSentence(sent);
                txt.addParagraph(para);
                stmt.setText(txt);            
                
                tokens.clear();
                words.clear();
                
                if (i + 1 == utters.getLength() - 1) break;
                u = (Element)utters.item(i + 1);
                toks = u.getElementsByTagName("t");
                // Convert response
                for (int j = 0; j < toks.getLength(); j++) {
                    Element w = (Element)toks.item(j);
                    String pos = w.getAttribute("pos");
                    String stem = w.getAttribute("stem");
                    Token t = new Token(w.getTextContent());
                    t.setPostag(pos);
                    t.setStem(stem);
                    t.setMainName(!w.getAttribute("ner").equals("O"));
                    tokens.add(t);
                    words.add(w.getTextContent());
                }
                utterance = String.join(" ", words);
                sent = new Sentence(utterance);
                para = new Paragraph(utterance);
                txt = new NLPText(utterance);
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
    public void storeCases(Collection<CBRCase> cases) {
        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element conversation = doc.createElement("conversation");
            doc.appendChild(conversation);
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(Date.from(Instant.now()));
            conversation.setAttribute("id", currentDateTime);
            for (CBRCase _case : cases) {
                ChatResponse desc = (ChatResponse)_case.getDescription();
                Element utter = doc.createElement("u");
                conversation.appendChild(utter);
                for (Token _t : desc.getText().getAllTokens()) {
                    NLPToken t = (NLPToken)_t; // TODO: Change
                    Element tok = doc.createElement("tok");
                    tok.setAttribute("pos", t.getPostag());
                    tok.setAttribute("stem", t.getStem());
                    tok.setAttribute("ner", t.getNerTag());
                    tok.setTextContent(t.getRawContent());
                    utter.appendChild(tok);
                }
                
                ChatResponse sol = (ChatResponse)_case.getSolution();
                Element utter2 = doc.createElement("u");
                conversation.appendChild(utter2);
                for (Token _t : sol.getText().getAllTokens()) {
                    NLPToken t = (NLPToken)_t; // TODO: Change
                    Element tok = doc.createElement("tok");
                    tok.setAttribute("pos", t.getPostag());
                    tok.setAttribute("stem", t.getStem());
                    tok.setAttribute("ner", t.getNerTag());
                    tok.setTextContent(t.getRawContent());
                    utter2.appendChild(tok);
                }
            }
            File xmlDir = this.xmlPath.resolve("bot").toFile();
            xmlDir.mkdirs();
            File xmlFile = new File(xmlDir, currentDateTime + ".xml");
            xmlFile.createNewFile();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(xmlFile);
            transformer.transform(source, streamResult);
            System.out.println("Wrote file " + xmlFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
