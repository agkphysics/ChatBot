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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;a xmlns="http://www.w3.org/1999/xhtml" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:tb="http://www.talkbank.org/ns/talkbank" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" xmlns:xs="http://www.w3.org/2001/XMLSchema" href="http://childes.psy.cmu.edu/manuals/chat.html#MorphologicalCompound"&gt;
 *                     CHAT manual
 *                     section on this topic...
 *                 &lt;/a&gt;
 * </pre>
 * 
 *             
 * 
 * <p>Java class for morphemicCompoundWordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="morphemicCompoundWordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.talkbank.org/ns/talkbank}mpfx" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.talkbank.org/ns/talkbank}pos"/>
 *         &lt;element ref="{http://www.talkbank.org/ns/talkbank}mw" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "morphemicCompoundWordType", propOrder = {
    "mpfxes",
    "pos",
    "mws"
})
@XmlRootElement(name = "mwc")
public class Mwc {

    @XmlElement(name = "mpfx")
    protected List<Mpfx> mpfxes;
    @XmlElement(required = true)
    protected Pos pos;
    @XmlElement(name = "mw", required = true)
    protected List<Mw> mws;

    /**
     * Gets the value of the mpfxes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mpfxes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMpfxes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mpfx }
     * 
     * 
     */
    public List<Mpfx> getMpfxes() {
        if (mpfxes == null) {
            mpfxes = new ArrayList<Mpfx>();
        }
        return this.mpfxes;
    }

    /**
     * Gets the value of the pos property.
     * 
     * @return
     *     possible object is
     *     {@link Pos }
     *     
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * Sets the value of the pos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pos }
     *     
     */
    public void setPos(Pos value) {
        this.pos = value;
    }

    /**
     * Gets the value of the mws property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mws property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMws().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mw }
     * 
     * 
     */
    public List<Mw> getMws() {
        if (mws == null) {
            mws = new ArrayList<Mw>();
        }
        return this.mws;
    }

}
