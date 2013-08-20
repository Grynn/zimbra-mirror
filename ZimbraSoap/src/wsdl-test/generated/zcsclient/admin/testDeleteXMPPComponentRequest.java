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
 * <p>Java class for deleteXMPPComponentRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteXMPPComponentRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xmppcomponent" type="{urn:zimbraAdmin}xmppComponentSelector"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteXMPPComponentRequest", propOrder = {
    "xmppcomponent"
})
public class testDeleteXMPPComponentRequest {

    @XmlElement(required = true)
    protected testXmppComponentSelector xmppcomponent;

    /**
     * Gets the value of the xmppcomponent property.
     * 
     * @return
     *     possible object is
     *     {@link testXmppComponentSelector }
     *     
     */
    public testXmppComponentSelector getXmppcomponent() {
        return xmppcomponent;
    }

    /**
     * Sets the value of the xmppcomponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link testXmppComponentSelector }
     *     
     */
    public void setXmppcomponent(testXmppComponentSelector value) {
        this.xmppcomponent = value;
    }

}
