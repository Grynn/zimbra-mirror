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
 * <p>Java class for getMemcachedClientConfigResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMemcachedClientConfigResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="serverList" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="hashAlgorithm" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="binaryProtocol" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="defaultExpirySeconds" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="defaultTimeoutMillis" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMemcachedClientConfigResponse")
public class testGetMemcachedClientConfigResponse {

    @XmlAttribute(name = "serverList")
    protected String serverList;
    @XmlAttribute(name = "hashAlgorithm")
    protected String hashAlgorithm;
    @XmlAttribute(name = "binaryProtocol")
    protected Boolean binaryProtocol;
    @XmlAttribute(name = "defaultExpirySeconds")
    protected Integer defaultExpirySeconds;
    @XmlAttribute(name = "defaultTimeoutMillis")
    protected Long defaultTimeoutMillis;

    /**
     * Gets the value of the serverList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerList() {
        return serverList;
    }

    /**
     * Sets the value of the serverList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerList(String value) {
        this.serverList = value;
    }

    /**
     * Gets the value of the hashAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * Sets the value of the hashAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHashAlgorithm(String value) {
        this.hashAlgorithm = value;
    }

    /**
     * Gets the value of the binaryProtocol property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBinaryProtocol() {
        return binaryProtocol;
    }

    /**
     * Sets the value of the binaryProtocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBinaryProtocol(Boolean value) {
        this.binaryProtocol = value;
    }

    /**
     * Gets the value of the defaultExpirySeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDefaultExpirySeconds() {
        return defaultExpirySeconds;
    }

    /**
     * Sets the value of the defaultExpirySeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDefaultExpirySeconds(Integer value) {
        this.defaultExpirySeconds = value;
    }

    /**
     * Gets the value of the defaultTimeoutMillis property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDefaultTimeoutMillis() {
        return defaultTimeoutMillis;
    }

    /**
     * Sets the value of the defaultTimeoutMillis property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDefaultTimeoutMillis(Long value) {
        this.defaultTimeoutMillis = value;
    }

}
