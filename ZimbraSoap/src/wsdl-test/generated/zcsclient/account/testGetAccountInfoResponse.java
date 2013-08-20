/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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

package generated.zcsclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testNamedValue;


/**
 * <p>Java class for getAccountInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAccountInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attr" type="{urn:zimbra}namedValue" maxOccurs="unbounded"/>
 *         &lt;element name="soapURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="publicURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="changePasswordURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAccountInfoResponse", propOrder = {
    "name",
    "attr",
    "soapURL",
    "publicURL",
    "changePasswordURL"
})
public class testGetAccountInfoResponse {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected List<testNamedValue> attr;
    protected String soapURL;
    protected String publicURL;
    protected String changePasswordURL;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the attr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testNamedValue }
     * 
     * 
     */
    public List<testNamedValue> getAttr() {
        if (attr == null) {
            attr = new ArrayList<testNamedValue>();
        }
        return this.attr;
    }

    /**
     * Gets the value of the soapURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoapURL() {
        return soapURL;
    }

    /**
     * Sets the value of the soapURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoapURL(String value) {
        this.soapURL = value;
    }

    /**
     * Gets the value of the publicURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicURL() {
        return publicURL;
    }

    /**
     * Sets the value of the publicURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicURL(String value) {
        this.publicURL = value;
    }

    /**
     * Gets the value of the changePasswordURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangePasswordURL() {
        return changePasswordURL;
    }

    /**
     * Sets the value of the changePasswordURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangePasswordURL(String value) {
        this.changePasswordURL = value;
    }

}
