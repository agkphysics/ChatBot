//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.30 at 05:39:42 PM NZDT 
//


package talkbank.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Terminators"&gt;
 *                     CHAT manual section on
 *                     this topic...
 *                 &lt;/a&gt;
 * </pre>
 * 
 *             
 * 
 * <p>Java class for baseTerminatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="baseTerminatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="p"/>
 *             &lt;enumeration value="q"/>
 *             &lt;enumeration value="e"/>
 *             &lt;enumeration value="broken for coding"/>
 *             &lt;enumeration value="trail off"/>
 *             &lt;enumeration value="trail off question"/>
 *             &lt;enumeration value="question exclamation"/>
 *             &lt;enumeration value="interruption"/>
 *             &lt;enumeration value="interruption question"/>
 *             &lt;enumeration value="self interruption"/>
 *             &lt;enumeration value="self interruption question"/>
 *             &lt;enumeration value="quotation next line"/>
 *             &lt;enumeration value="quotation precedes"/>
 *             &lt;enumeration value="missing CA terminator"/>
 *             &lt;enumeration value="technical break TCU continuation"/>
 *             &lt;enumeration value="no break TCU continuation"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseTerminatorType")
@XmlSeeAlso({
    T.class
})
public class BaseTerminatorType {

    @XmlAttribute(name = "type", required = true)
    protected String type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}