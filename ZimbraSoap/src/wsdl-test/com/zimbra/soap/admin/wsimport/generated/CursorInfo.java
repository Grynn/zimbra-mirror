
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cursorInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cursorInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortVal" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="endSortVal" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cursorInfo")
public class CursorInfo {

    @XmlAttribute
    protected String id;
    @XmlAttribute
    protected String sortVal;
    @XmlAttribute
    protected String endSortVal;

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
     * Gets the value of the sortVal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortVal() {
        return sortVal;
    }

    /**
     * Sets the value of the sortVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortVal(String value) {
        this.sortVal = value;
    }

    /**
     * Gets the value of the endSortVal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndSortVal() {
        return endSortVal;
    }

    /**
     * Sets the value of the endSortVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndSortVal(String value) {
        this.endSortVal = value;
    }

}
