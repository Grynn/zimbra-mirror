
package com.zimbra.soap.account.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteIdentityRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteIdentityRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identity" type="{urn:zimbraAccount}nameId"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteIdentityRequest", propOrder = {
    "identity"
})
public class DeleteIdentityRequest {

    @XmlElement(required = true)
    protected NameId identity;

    /**
     * Gets the value of the identity property.
     * 
     * @return
     *     possible object is
     *     {@link NameId }
     *     
     */
    public NameId getIdentity() {
        return identity;
    }

    /**
     * Sets the value of the identity property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameId }
     *     
     */
    public void setIdentity(NameId value) {
        this.identity = value;
    }

}
