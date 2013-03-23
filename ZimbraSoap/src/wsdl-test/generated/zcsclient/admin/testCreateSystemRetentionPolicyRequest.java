/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createSystemRetentionPolicyRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createSystemRetentionPolicyRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cos" type="{urn:zimbraAdmin}cosSelector" minOccurs="0"/>
 *         &lt;element name="keep" type="{urn:zimbraAdmin}policyHolder" minOccurs="0"/>
 *         &lt;element name="purge" type="{urn:zimbraAdmin}policyHolder" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createSystemRetentionPolicyRequest", propOrder = {
    "cos",
    "keep",
    "purge"
})
public class testCreateSystemRetentionPolicyRequest {

    protected testCosSelector cos;
    protected testPolicyHolder keep;
    protected testPolicyHolder purge;

    /**
     * Gets the value of the cos property.
     * 
     * @return
     *     possible object is
     *     {@link testCosSelector }
     *     
     */
    public testCosSelector getCos() {
        return cos;
    }

    /**
     * Sets the value of the cos property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCosSelector }
     *     
     */
    public void setCos(testCosSelector value) {
        this.cos = value;
    }

    /**
     * Gets the value of the keep property.
     * 
     * @return
     *     possible object is
     *     {@link testPolicyHolder }
     *     
     */
    public testPolicyHolder getKeep() {
        return keep;
    }

    /**
     * Sets the value of the keep property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPolicyHolder }
     *     
     */
    public void setKeep(testPolicyHolder value) {
        this.keep = value;
    }

    /**
     * Gets the value of the purge property.
     * 
     * @return
     *     possible object is
     *     {@link testPolicyHolder }
     *     
     */
    public testPolicyHolder getPurge() {
        return purge;
    }

    /**
     * Sets the value of the purge property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPolicyHolder }
     *     
     */
    public void setPurge(testPolicyHolder value) {
        this.purge = value;
    }

}
