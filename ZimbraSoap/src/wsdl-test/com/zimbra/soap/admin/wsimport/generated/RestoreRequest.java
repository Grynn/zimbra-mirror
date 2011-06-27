
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for restoreRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="restoreRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="restore" type="{urn:zimbraAdmin}restoreSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "restoreRequest", propOrder = {
    "restore"
})
public class RestoreRequest {

    @XmlElement(required = true)
    protected RestoreSpec restore;

    /**
     * Gets the value of the restore property.
     * 
     * @return
     *     possible object is
     *     {@link RestoreSpec }
     *     
     */
    public RestoreSpec getRestore() {
        return restore;
    }

    /**
     * Sets the value of the restore property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestoreSpec }
     *     
     */
    public void setRestore(RestoreSpec value) {
        this.restore = value;
    }

}
