//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.30 at 05:39:42 PM NZDT 
//


package talkbank.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for annotationTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="annotationTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="addressee"/>
 *     &lt;enumeration value="actions"/>
 *     &lt;enumeration value="alternative"/>
 *     &lt;enumeration value="coding"/>
 *     &lt;enumeration value="cohesion"/>
 *     &lt;enumeration value="comments"/>
 *     &lt;enumeration value="english translation"/>
 *     &lt;enumeration value="errcoding"/>
 *     &lt;enumeration value="explanation"/>
 *     &lt;enumeration value="flow"/>
 *     &lt;enumeration value="facial"/>
 *     &lt;enumeration value="target gloss"/>
 *     &lt;enumeration value="gesture"/>
 *     &lt;enumeration value="intonation"/>
 *     &lt;enumeration value="language"/>
 *     &lt;enumeration value="orthography"/>
 *     &lt;enumeration value="paralinguistics"/>
 *     &lt;enumeration value="SALT"/>
 *     &lt;enumeration value="situation"/>
 *     &lt;enumeration value="speech act"/>
 *     &lt;enumeration value="time stamp"/>
 *     &lt;enumeration value="extension"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "annotationTypeType")
@XmlEnum
public enum AnnotationTypeType {


    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Addressee_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("addressee")
    ADDRESSEE("addressee"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Action_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("actions")
    ACTIONS("actions"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Alternate_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("alternative")
    ALTERNATIVE("alternative"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Coding_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("coding")
    CODING("coding"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Cohesion_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("cohesion")
    COHESION("cohesion"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Comment_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("comments")
    COMMENTS("comments"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#English_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("english translation")
    ENGLISH_TRANSLATION("english translation"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Error_Tier"&gt;
     *                             CHAT manual section on
     *                             this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("errcoding")
    ERRCODING("errcoding"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Explanation_Scope"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("explanation")
    EXPLANATION("explanation"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Flow_Tier"&gt;
     *                             CHAT manual section on
     *                             this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("flow")
    FLOW("flow"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#FacialGesture_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("facial")
    FACIAL("facial"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Gloss_Tier"&gt;
     *                             CHAT manual section on
     *                             this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("target gloss")
    TARGET_GLOSS("target gloss"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Gestural_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("gesture")
    GESTURE("gesture"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Intonational_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("intonation")
    INTONATION("intonation"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Language_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("language")
    LANGUAGE("language"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Orthography_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("orthography")
    ORTHOGRAPHY("orthography"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Paralinguistics_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("paralinguistics")
    PARALINGUISTICS("paralinguistics"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Definitions_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    SALT("SALT"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Situation_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("situation")
    SITUATION("situation"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#SpeechAct_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("speech act")
    SPEECH_ACT("speech act"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Timing_Tier"&gt;
     *                             CHAT manual section
     *                             on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("time stamp")
    TIME_STAMP("time stamp"),

    /**
     * 
     *                         
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Extension_Tier"&gt;
     *                             CHAT manual
     *                             section on this topic...
     *                         &lt;/a&gt;
     * </pre>
     * 
     *                     
     * 
     */
    @XmlEnumValue("extension")
    EXTENSION("extension");
    private final String value;

    AnnotationTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AnnotationTypeType fromValue(String v) {
        for (AnnotationTypeType c: AnnotationTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}