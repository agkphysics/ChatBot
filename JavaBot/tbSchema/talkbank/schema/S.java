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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#ToneDirection_Marker"&gt;
 *                     CHAT manual
 *                     section on this topic...
 *                 &lt;/a&gt;
 * </pre>
 * 
 *             
 * 
 * <p>Java class for separatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="separatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="semicolon"/>
 *             &lt;enumeration value="colon"/>
 *             &lt;enumeration value="clause delimiter"/>
 *             &lt;enumeration value="rising to high"/>
 *             &lt;enumeration value="rising to mid"/>
 *             &lt;enumeration value="level"/>
 *             &lt;enumeration value="falling to mid"/>
 *             &lt;enumeration value="falling to low"/>
 *             &lt;enumeration value="unmarked ending"/>
 *             &lt;enumeration value="uptake"/>
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
@XmlType(name = "separatorType")
@XmlRootElement(name = "s")
public class S {

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