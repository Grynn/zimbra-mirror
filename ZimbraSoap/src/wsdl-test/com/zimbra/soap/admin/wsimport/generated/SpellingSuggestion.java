
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spellingSuggestion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spellingSuggestion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="dist" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numDocs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spellingSuggestion", namespace = "urn:zimbra")
public class SpellingSuggestion {

    @XmlAttribute(required = true)
    protected int dist;
    @XmlAttribute(required = true)
    protected int numDocs;
    @XmlAttribute(required = true)
    protected String value;

    /**
     * Gets the value of the dist property.
     * 
     */
    public int getDist() {
        return dist;
    }

    /**
     * Sets the value of the dist property.
     * 
     */
    public void setDist(int value) {
        this.dist = value;
    }

    /**
     * Gets the value of the numDocs property.
     * 
     */
    public int getNumDocs() {
        return numDocs;
    }

    /**
     * Sets the value of the numDocs property.
     * 
     */
    public void setNumDocs(int value) {
        this.numDocs = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
