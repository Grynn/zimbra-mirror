/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.admin;

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
public class testGetLicenseResponse {

    @XmlElement(required = true)
    protected testAdminAttrsImpl license;
    protected testAdminAttrsImpl activation;
    @XmlElement(required = true)
    protected testAdminAttrsImpl info;

    /**
     * Gets the value of the license property.
     * 
     * @return
     *     possible object is
     *     {@link testAdminAttrsImpl }
     *     
     */
    public testAdminAttrsImpl getLicense() {
        return license;
    }

    /**
     * Sets the value of the license property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAdminAttrsImpl }
     *     
     */
    public void setLicense(testAdminAttrsImpl value) {
        this.license = value;
    }

    /**
     * Gets the value of the activation property.
     * 
     * @return
     *     possible object is
     *     {@link testAdminAttrsImpl }
     *     
     */
    public testAdminAttrsImpl getActivation() {
        return activation;
    }

    /**
     * Sets the value of the activation property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAdminAttrsImpl }
     *     
     */
    public void setActivation(testAdminAttrsImpl value) {
        this.activation = value;
    }

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link testAdminAttrsImpl }
     *     
     */
    public testAdminAttrsImpl getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAdminAttrsImpl }
     *     
     */
    public void setInfo(testAdminAttrsImpl value) {
        this.info = value;
    }

}
