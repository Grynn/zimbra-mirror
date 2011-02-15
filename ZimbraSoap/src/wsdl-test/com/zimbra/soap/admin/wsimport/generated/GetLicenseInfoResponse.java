
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getLicenseInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getLicenseInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="expiration" type="{urn:zimbraAdmin}licenseExpirationInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getLicenseInfoResponse", propOrder = {
    "expiration"
})
public class GetLicenseInfoResponse {

    @XmlElement(required = true)
    protected LicenseExpirationInfo expiration;

    /**
     * Gets the value of the expiration property.
     * 
     * @return
     *     possible object is
     *     {@link LicenseExpirationInfo }
     *     
     */
    public LicenseExpirationInfo getExpiration() {
        return expiration;
    }

    /**
     * Sets the value of the expiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicenseExpirationInfo }
     *     
     */
    public void setExpiration(LicenseExpirationInfo value) {
        this.expiration = value;
    }

}
