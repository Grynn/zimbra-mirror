
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createXMPPComponentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createXMPPComponentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xmppcomponent" type="{urn:zimbraAdmin}xmppComponentInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createXMPPComponentResponse", propOrder = {
    "xmppcomponent"
})
public class CreateXMPPComponentResponse {

    @XmlElement(required = true)
    protected XmppComponentInfo xmppcomponent;

    /**
     * Gets the value of the xmppcomponent property.
     * 
     * @return
     *     possible object is
     *     {@link XmppComponentInfo }
     *     
     */
    public XmppComponentInfo getXmppcomponent() {
        return xmppcomponent;
    }

    /**
     * Sets the value of the xmppcomponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmppComponentInfo }
     *     
     */
    public void setXmppcomponent(XmppComponentInfo value) {
        this.xmppcomponent = value;
    }

}
