
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getFreeBusyQueueInfoRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getFreeBusyQueueInfoRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="provider" type="{urn:zimbraAdmin}namedElement" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFreeBusyQueueInfoRequest", propOrder = {
    "provider"
})
public class GetFreeBusyQueueInfoRequest {

    protected NamedElement provider;

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link NamedElement }
     *     
     */
    public NamedElement getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedElement }
     *     
     */
    public void setProvider(NamedElement value) {
        this.provider = value;
    }

}
