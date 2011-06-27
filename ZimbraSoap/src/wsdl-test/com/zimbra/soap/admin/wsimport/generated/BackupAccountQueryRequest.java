
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupAccountQueryRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupAccountQueryRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="query" type="{urn:zimbraAdmin}backupAccountQuerySpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupAccountQueryRequest", propOrder = {
    "query"
})
public class BackupAccountQueryRequest {

    @XmlElement(required = true)
    protected BackupAccountQuerySpec query;

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link BackupAccountQuerySpec }
     *     
     */
    public BackupAccountQuerySpec getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link BackupAccountQuerySpec }
     *     
     */
    public void setQuery(BackupAccountQuerySpec value) {
        this.query = value;
    }

}
