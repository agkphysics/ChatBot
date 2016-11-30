//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.30 at 05:39:42 PM NZDT 
//


package talkbank.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#SimpleEvents"&gt;
 *                     CHAT manual section on
 *                     this topic...
 *                 &lt;/a&gt;
 * </pre>
 * 
 *             
 * 
 * <p>Java class for eventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}action"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}happening"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}otherSpokenEvent"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}k"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}error"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}r"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}overlap"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}ga"/>
 *           &lt;element ref="{http://www.talkbank.org/ns/talkbank}duration"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventType", propOrder = {
    "otherSpokenEvent",
    "happening",
    "action",
    "ksAndErrorsAndRS"
})
@XmlRootElement(name = "e")
public class E {

    protected OtherSpokenEvent otherSpokenEvent;
    protected String happening;
    protected Object action;
    @XmlElements({
        @XmlElement(name = "k", type = K.class),
        @XmlElement(name = "error", type = String.class),
        @XmlElement(name = "r", type = R.class),
        @XmlElement(name = "overlap", type = Overlap.class),
        @XmlElement(name = "ga", type = Ga.class),
        @XmlElement(name = "duration", type = BigDecimal.class)
    })
    protected List<Object> ksAndErrorsAndRS;

    /**
     * Gets the value of the otherSpokenEvent property.
     * 
     * @return
     *     possible object is
     *     {@link OtherSpokenEvent }
     *     
     */
    public OtherSpokenEvent getOtherSpokenEvent() {
        return otherSpokenEvent;
    }

    /**
     * Sets the value of the otherSpokenEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherSpokenEvent }
     *     
     */
    public void setOtherSpokenEvent(OtherSpokenEvent value) {
        this.otherSpokenEvent = value;
    }

    /**
     * Gets the value of the happening property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHappening() {
        return happening;
    }

    /**
     * Sets the value of the happening property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHappening(String value) {
        this.happening = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAction(Object value) {
        this.action = value;
    }

    /**
     * Gets the value of the ksAndErrorsAndRS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ksAndErrorsAndRS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKSAndErrorsAndRS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link K }
     * {@link String }
     * {@link R }
     * {@link Overlap }
     * {@link Ga }
     * {@link BigDecimal }
     * 
     * 
     */
    public List<Object> getKSAndErrorsAndRS() {
        if (ksAndErrorsAndRS == null) {
            ksAndErrorsAndRS = new ArrayList<Object>();
        }
        return this.ksAndErrorsAndRS;
    }

}
