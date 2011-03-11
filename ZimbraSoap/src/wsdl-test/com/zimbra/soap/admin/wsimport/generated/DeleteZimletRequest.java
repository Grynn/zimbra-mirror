
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteZimletRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteZimletRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zimlet" type="{urn:zimbraAdmin}namedElement"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteZimletRequest", propOrder = {
    "zimlet"
})
public class DeleteZimletRequest {

    @XmlElement(required = true)
    protected NamedElement zimlet;

    /**
     * Gets the value of the zimlet property.
     * 
     * @return
     *     possible object is
     *     {@link NamedElement }
     *     
     */
    public NamedElement getZimlet() {
        return zimlet;
    }

    /**
     * Sets the value of the zimlet property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedElement }
     *     
     */
    public void setZimlet(NamedElement value) {
        this.zimlet = value;
    }

}
