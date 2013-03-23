/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
import generated.zcsclient.zm.testAttributeSelectorImpl;


/**
 * <p>Java class for getUCServiceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getUCServiceRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="ucservice" type="{urn:zimbraAdmin}ucServiceSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getUCServiceRequest", propOrder = {
    "ucservice"
})
public class testGetUCServiceRequest
    extends testAttributeSelectorImpl
{

    protected testUcServiceSelector ucservice;

    /**
     * Gets the value of the ucservice property.
     * 
     * @return
     *     possible object is
     *     {@link testUcServiceSelector }
     *     
     */
    public testUcServiceSelector getUcservice() {
        return ucservice;
    }

    /**
     * Sets the value of the ucservice property.
     * 
     * @param value
     *     allowed object is
     *     {@link testUcServiceSelector }
     *     
     */
    public void setUcservice(testUcServiceSelector value) {
        this.ucservice = value;
    }

}
