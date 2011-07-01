
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzReplaceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzReplaceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="wellKnownTz" type="{urn:zimbra}id" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraAdmin}calTZInfo" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzReplaceInfo", propOrder = {

})
public class TzReplaceInfo {

    protected Id wellKnownTz;
    protected CalTZInfo tz;

    /**
     * Gets the value of the wellKnownTz property.
     * 
     * @return
     *     possible object is
     *     {@link Id }
     *     
     */
    public Id getWellKnownTz() {
        return wellKnownTz;
    }

    /**
     * Sets the value of the wellKnownTz property.
     * 
     * @param value
     *     allowed object is
     *     {@link Id }
     *     
     */
    public void setWellKnownTz(Id value) {
        this.wellKnownTz = value;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link CalTZInfo }
     *     
     */
    public CalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalTZInfo }
     *     
     */
    public void setTz(CalTZInfo value) {
        this.tz = value;
    }

}
