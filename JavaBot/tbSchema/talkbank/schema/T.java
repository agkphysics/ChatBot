//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.30 at 05:39:42 PM NZDT 
//


package talkbank.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#Terminator_Alignment"&gt;
 *                     CHAT manual
 *                     section on this topic...
 *                 &lt;/a&gt;
 * </pre>
 * 
 *             
 * 
 * <p>Java class for terminatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="terminatorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.talkbank.org/ns/talkbank}baseTerminatorType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.talkbank.org/ns/talkbank}mor" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "terminatorType", propOrder = {
    "mors"
})
@XmlRootElement(name = "t")
public class T
    extends BaseTerminatorType
{

    @XmlElement(name = "mor")
    protected List<Mor> mors;

    /**
     * Gets the value of the mors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mor }
     * 
     * 
     */
    public List<Mor> getMors() {
        if (mors == null) {
            mors = new ArrayList<Mor>();
        }
        return this.mors;
    }

}