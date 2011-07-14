
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for abortXMbxSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="abortXMbxSearchRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchtask" type="{urn:zimbraAdmin}searchID"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abortXMbxSearchRequest", propOrder = {
    "searchtask"
})
public class AbortXMbxSearchRequest {

    @XmlElement(required = true)
    protected SearchID searchtask;

    /**
     * Gets the value of the searchtask property.
     * 
     * @return
     *     possible object is
     *     {@link SearchID }
     *     
     */
    public SearchID getSearchtask() {
        return searchtask;
    }

    /**
     * Sets the value of the searchtask property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchID }
     *     
     */
    public void setSearchtask(SearchID value) {
        this.searchtask = value;
    }

}
