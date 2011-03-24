
package com.zimbra.soap.account.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifySignatureResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifySignatureResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="signature" type="{urn:zimbraAccount}nameId"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifySignatureResponse", propOrder = {
    "signature"
})
public class ModifySignatureResponse {

    @XmlElement(required = true)
    protected NameId signature;

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link NameId }
     *     
     */
    public NameId getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameId }
     *     
     */
    public void setSignature(NameId value) {
        this.signature = value;
    }

}
