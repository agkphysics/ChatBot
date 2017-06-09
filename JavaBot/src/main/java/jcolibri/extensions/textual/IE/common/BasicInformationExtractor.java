/**
 * BasicInformationExtractor.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 21/06/2007
 */
package jcolibri.extensions.textual.IE.common;

import java.util.*;

import jcolibri.cbrcore.*;
import jcolibri.exception.AttributeAccessException;
import jcolibri.extensions.textual.IE.representation.IEText;
import jcolibri.extensions.textual.IE.representation.info.FeatureInfo;
import jcolibri.extensions.textual.IE.representation.info.PhraseInfo;
import jcolibri.util.ProgressController;

/**
 * This class implements a basic information extractor.<br>
 * For each Case Component of a case or query, this method obtains the features
 * or phrases extracted in its textual attributes and copies the values into the
 * other attributes of the component.<br>
 * To copy the features, this method looks for attributes with the same name
 * that the featues and typed as Strings. Then it copies the value of the
 * feature. If there are many features it concatenates their values separated by
 * a white space. <br>
 * With the phrases it does something similar: finds attributes with the same
 * name but typed as booleans. If so, it changes the boolean to true.
 * <p>
 * First version was developed at: Robert Gordon University - Aberdeen &
 * Facultad Inform�tica, Universidad Complutense de Madrid (GAIA)
 *
 * @author Juan A. Recio-Garcia
 * @version 2.0
 * @see jcolibri.cbrcore.CaseComponent
 */
public class BasicInformationExtractor {
    /**
     * Performs the algorithm in a query.
     */
    public static void extractInformation(CBRQuery query) {
        org.apache.commons.logging.LogFactory.getLog(BasicInformationExtractor.class)
                .info("Extracting query information.");
        extractInformation(query.getDescription());
    }

    /**
     * Performs the algorithm in a collection of cases.
     */
    public static void extractInformation(Collection<CBRCase> cases) {
        org.apache.commons.logging.LogFactory.getLog(BasicInformationExtractor.class)
                .info("Extracting cases information.");
        ProgressController.init(BasicInformationExtractor.class, "Extracting cases information ...", cases.size());
        for (CBRCase c : cases) {
            extractInformation(c.getDescription());
            extractInformation(c.getSolution());
            extractInformation(c.getDescription());
            extractInformation(c.getDescription());
            ProgressController.step(BasicInformationExtractor.class);
        }
        ProgressController.finish(BasicInformationExtractor.class);
    }

    /**
     * Extracts the information of a given CaseComponent
     *
     * @param cc
     */
    private static void extractInformation(CaseComponent cc) {
        if (cc == null) return;
        try {
            Attribute[] attrs = jcolibri.util.AttributeUtils.getAttributes(cc.getClass());

            // Find the texts and other attributes
            ArrayList<IEText> texts = new ArrayList<>();
            ArrayList<Attribute> other = new ArrayList<>();
            for (int i = 0; i < attrs.length; i++) {
                Object o = attrs[i].getValue(cc);
                if (o instanceof CaseComponent) extractInformation((CaseComponent)o);
                else if (o instanceof IEText) texts.add((IEText)o);
                else other.add(attrs[i]);

            }

            // Obtain all features and phrases
            ArrayList<PhraseInfo> phrases = new ArrayList<>();
            ArrayList<FeatureInfo> features = new ArrayList<>();
            for (IEText text : texts) {
                phrases.addAll(text.getPhrases());
                features.addAll(text.getFeatures());
            }

            // find a proper value for each attribute. If its type is:
            // String: find a feature
            // Phrase: find a phrase
            for (Attribute at : other) {
                String name = at.getName();
                if (at.getType().equals(String.class)) {
                    String value = "";
                    for (FeatureInfo feature : features)
                        if (feature.getFeature().equalsIgnoreCase(name)) value += feature.getValue() + " ";
                    if (value.length() > 0) {
                        at.setValue(cc, value);
                        org.apache.commons.logging.LogFactory.getLog(BasicInformationExtractor.class)
                                .debug("Adding features to attribute: " + at.getName() + " <- " + value);
                    }

                } else if (at.getType().equals(Boolean.class)) {
                    Boolean phrase = new Boolean(false);
                    for (Iterator<PhraseInfo> iter = phrases.iterator(); iter.hasNext() && !phrase.booleanValue();) {
                        PhraseInfo p = iter.next();
                        if (p.getPhrase().equalsIgnoreCase(name)) {
                            phrase = Boolean.TRUE;
                            at.setValue(cc, phrase);
                            org.apache.commons.logging.LogFactory.getLog(BasicInformationExtractor.class)
                                    .debug("Enabling attribute: " + at.getName() + ". Source: " + p.getPhrase());

                        }
                    }
                }
            }
        } catch (AttributeAccessException e) {
            org.apache.commons.logging.LogFactory.getLog(BasicInformationExtractor.class).error(e);

        }

    }
}
