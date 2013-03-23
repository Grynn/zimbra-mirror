/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
 * <p>Java class for versionCheckResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versionCheckResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versionCheck" type="{urn:zimbraAdmin}versionCheckInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versionCheckResponse", propOrder = {
    "versionCheck"
})
public class testVersionCheckResponse {

    protected testVersionCheckInfo versionCheck;

    /**
     * Gets the value of the versionCheck property.
     * 
     * @return
     *     possible object is
     *     {@link testVersionCheckInfo }
     *     
     */
    public testVersionCheckInfo getVersionCheck() {
        return versionCheck;
    }

    /**
     * Sets the value of the versionCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVersionCheckInfo }
     *     
     */
    public void setVersionCheck(testVersionCheckInfo value) {
        this.versionCheck = value;
    }

}
