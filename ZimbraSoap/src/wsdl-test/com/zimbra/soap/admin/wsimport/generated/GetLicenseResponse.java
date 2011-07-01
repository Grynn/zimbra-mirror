
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getLicenseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getLicenseResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="license" type="{urn:zimbraAdmin}adminAttrsImpl"/>
 *         &lt;element name="activation" type="{urn:zimbraAdmin}adminAttrsImpl" minOccurs="0"/>
 *         &lt;element name="info" type="{urn:zimbraAdmin}adminAttrsImpl"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getLicenseResponse", propOrder = {

})
public class GetLicenseResponse {

    @XmlElement(required = true)
    protected AdminAttrsImpl license;
    protected AdminAttrsImpl activation;
    @XmlElement(required = true)
    protected AdminAttrsImpl info;

    /**
     * Gets the value of the license property.
     * 
     * @return
     *     possible object is
     *     {@link AdminAttrsImpl }
     *     
     */
    public AdminAttrsImpl getLicense() {
        return license;
    }

    /**
     * Sets the value of the license property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdminAttrsImpl }
     *     
     */
    public void setLicense(AdminAttrsImpl value) {
        this.license = value;
    }

    /**
     * Gets the value of the activation property.
     * 
     * @return
     *     possible object is
     *     {@link AdminAttrsImpl }
     *     
     */
    public AdminAttrsImpl getActivation() {
        return activation;
    }

    /**
     * Sets the value of the activation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdminAttrsImpl }
     *     
     */
    public void setActivation(AdminAttrsImpl value) {
        this.activation = value;
    }

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link AdminAttrsImpl }
     *     
     */
    public AdminAttrsImpl getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdminAttrsImpl }
     *     
     */
    public void setInfo(AdminAttrsImpl value) {
        this.info = value;
    }

}
