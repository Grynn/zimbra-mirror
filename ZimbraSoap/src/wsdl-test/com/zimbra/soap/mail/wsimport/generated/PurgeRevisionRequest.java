
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for purgeRevisionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="purgeRevisionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="revision" type="{urn:zimbraMail}purgeRevisionSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "purgeRevisionRequest", propOrder = {
    "revision"
})
public class PurgeRevisionRequest {

    @XmlElement(required = true)
    protected PurgeRevisionSpec revision;

    /**
     * Gets the value of the revision property.
     * 
     * @return
     *     possible object is
     *     {@link PurgeRevisionSpec }
     *     
     */
    public PurgeRevisionSpec getRevision() {
        return revision;
    }

    /**
     * Sets the value of the revision property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurgeRevisionSpec }
     *     
     */
    public void setRevision(PurgeRevisionSpec value) {
        this.revision = value;
    }

}
