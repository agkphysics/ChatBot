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

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

import com.hp.hpl.jena.query.function.library.trace;

import agk.chatbot.schema.Conversation;
import jcolibri.cbrcore.*;
import jcolibri.exception.InitializingException;
import jcolibri.extensions.textual.IE.representation.IEText;
import jcolibri.extensions.textual.IE.representation.Paragraph;
import jcolibri.extensions.textual.IE.representation.Sentence;
import jcolibri.extensions.textual.IE.representation.Token;
import talkbank.schema.CHAT;

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
    
    public static Collection<CBRCase> convertConversationToCases(Conversation con) {
        List<CBRCase> cases = new ArrayList<>();
        for (Conversation.ResponsePair pair : con.getResponsePair()) {
            ChatResponse stmt = new ChatResponse();
            ChatResponse resp = new ChatResponse();
            CBRCase c = new CBRCase();
            
            stmt.setId(con.getId());
            resp.setId(con.getId());
            
            IEText txt;
            String utterance;
            Sentence sent;
            Paragraph para;
            
            List<Token> tokens = new ArrayList<>();
            List<String> words = new ArrayList<>();
            
            // Convert statement
            for (Conversation.ResponsePair.Statement.Word w : pair.getStatement().getWord()) {
            	Token t = new Token(w.getValue());
            	t.setPostag(w.getTag());
            	t.setStem(w.getStem());
            	tokens.add(t);
            	words.add(w.getValue());
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
            
            // Convert response
            for (Conversation.ResponsePair.Response.Word w : pair.getResponse().getWord()) {
            	Token t = new Token(w.getValue());
            	t.setPostag(w.getTag());
            	t.setStem(w.getStem());
            	tokens.add(t);
            	words.add(w.getValue());
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
					Conversation con = JAXB.unmarshal(file, Conversation.class);
					coll.addAll(convertConversationToCases(con));
				}
			});
//            Conversation con = JAXB.unmarshal(file, Conversation.class);
//            coll.addAll(convertConversationToCases(con));
        }
        
        executor.shutdown();
        try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
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
