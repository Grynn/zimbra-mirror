
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getZimletRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getZimletRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="zimlet" type="{urn:zimbraAdmin}namedElement"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getZimletRequest", propOrder = {
    "zimlet"
})
public class GetZimletRequest
    extends AttributeSelectorImpl
{

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
