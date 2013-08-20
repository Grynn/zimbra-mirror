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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uploadDomCertResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uploadDomCertResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="cert_content" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="key_content" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadDomCertResponse")
public class testUploadDomCertResponse {

    @XmlAttribute(name = "cert_content")
    protected String certContent;
    @XmlAttribute(name = "key_content")
    protected String keyContent;

    /**
     * Gets the value of the cert_Content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCert_Content() {
        return certContent;
    }

    /**
     * Sets the value of the cert_Content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCert_Content(String value) {
        this.certContent = value;
    }

    /**
     * Gets the value of the key_Content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey_Content() {
        return keyContent;
    }

    /**
     * Sets the value of the key_Content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey_Content(String value) {
        this.keyContent = value;
    }

}
