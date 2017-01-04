package agk.chatbot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import javax.xml.bind.JAXB;

import jcolibri.cbrcore.*;
import jcolibri.exception.InitializingException;
import talkbank.schema.*;

/**
 * This class implements the connector between the TalkBank corpus of XML files
 * and the chat bot.
 * 
 * @author Aaron
 */
public class TalkBankXMLConnector implements Connector {
    
    private List<File> xmlFiles;

    /**
     *
     */
    public TalkBankXMLConnector(Path pathToXmls) {
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
                    if (attrs.isRegularFile() && file.getFileName().toString().endsWith(".xml")) xmlFiles.add(file.toFile());
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
        
        System.out.println("Found " + xmlFiles.size() + " xml chat files.");
    }
    
    /**
     * Converts a CHAT object to a list of cases, which are response pairs.
     * 
     * @param ch the CHAT object to convert
     * @return the list of CBRCase objects composed of utterances as list of strings.
     */
    public static Collection<CBRCase> convertChatToCases(CHAT ch) {
        List<CBRCase> cases = new ArrayList<>();
        for (int i = 0; i < ch.getCommentsAndTcusAndBeginGems().size(); i++) {
            Object o = ch.getCommentsAndTcusAndBeginGems().get(i);
            if (o instanceof U) { // Element is an utterance
                U u = (U)o;
                CBRCase cbrcase = new CBRCase();
                
                ChatResponse resp = new ChatResponse();
                resp.setId(ch.getId() + u.getUID());
                List<String> words = new ArrayList<>();
                for (Object o1 : u.getWSAndBlobsAndGS()) {
                    if (o1 instanceof W) {
                        W w = (W)o1;
                        for (Object o2 : w.getContent()) {
                            if (o2 instanceof String) words.add((String)o2);
                        }
                    }
                }
                resp.setText(String.join(" ", words));
                cbrcase.setDescription(resp);
                
                // Find next utterance from a different user, as a reply
                for (int j = i; j < ch.getCommentsAndTcusAndBeginGems().size(); j++) {
                    Object o1 = ch.getCommentsAndTcusAndBeginGems().get(j);
                    if (o1 instanceof U && !((U)o1).getWho().equals(u.getWho())) {
                        U u2 = (U)o1;
                        ChatResponse resp2 = new ChatResponse();
                        resp2.setId(ch.getId() + u2.getUID());
                        List<String> words2 = new ArrayList<>();
                        for (Object o2 : u2.getWSAndBlobsAndGS()) {
                            if (o2 instanceof W) {
                                W w = (W)o2;
                                for (Object o3 : w.getContent()) {
                                    if (o3 instanceof String) words2.add((String)o3);
                                }
                            }
                        }
                        resp2.setText(String.join(" ", words2));
                        cbrcase.setSolution(resp2);
                        break;
                    }
                }
                
                cases.add(cbrcase);
            }
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
    public void deleteCases(Collection<CBRCase> arg0) {}

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#initFromXMLfile(java.net.URL)
     */
    @Override
    public void initFromXMLfile(URL arg0) throws InitializingException {}

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#retrieveAllCases()
     */
    @Override
    public Collection<CBRCase> retrieveAllCases() {
        Collection<CBRCase> coll = new ArrayList<>();
        
        for (File file : xmlFiles) {
            CHAT ch = JAXB.unmarshal(file, CHAT.class);
            if (ch.getLangs().size() == 1
                    && ch.getLangs().get(0).equals("eng")
                    && ch.getParticipants().getParticipants().size() > 1) {
                // Only use chats with solely English language utterances and more than one speaker
                Collection<CBRCase> cases = convertChatToCases(ch);
                coll.addAll(cases);
            }
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
    public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter arg0) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.cbrcore.Connector#storeCases(java.util.Collection)
     */
    @Override
    public void storeCases(Collection<CBRCase> arg0) {}

}
