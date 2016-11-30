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
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 *                 Specifies a syllable constituent. The type is one of constituentTypeType.
 *                 Each constituent can constist of one or more phones identified by zero-based index of the
 *                 parent phonetic rep.
 *             
 * 
 * <p>Java class for constituentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="constituentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="sctype" type="{http://www.talkbank.org/ns/talkbank}constituentTypeType" default="UK" />
 *       &lt;attribute name="id" use="required" type="{http://www.talkbank.org/ns/talkbank}phKeyType" />
 *       &lt;attribute name="hiatus" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "constituentType", propOrder = {
    "content"
})
@XmlRootElement(name = "ph")
public class Ph {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "sctype")
    protected ConstituentTypeType sctype;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "hiatus")
    protected Boolean hiatus;

    /**
     * 
     *                 Specifies a syllable constituent. The type is one of constituentTypeType.
     *                 Each constituent can constist of one or more phones identified by zero-based index of the
     *                 parent phonetic rep.
     *             
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the sctype property.
     * 
     * @return
     *     possible object is
     *     {@link ConstituentTypeType }
     *     
     */
    public ConstituentTypeType getSctype() {
        if (sctype == null) {
            return ConstituentTypeType.UK;
        } else {
            return sctype;
        }
    }

    /**
     * Sets the value of the sctype property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstituentTypeType }
     *     
     */
    public void setSctype(ConstituentTypeType value) {
        this.sctype = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the hiatus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHiatus() {
        return hiatus;
    }

    /**
     * Sets the value of the hiatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHiatus(Boolean value) {
        this.hiatus = value;
    }

}
