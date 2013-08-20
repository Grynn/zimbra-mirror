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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for accountZimletInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accountZimletInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zimletContext" type="{urn:zimbraAccount}accountZimletContext" minOccurs="0"/>
 *         &lt;element name="zimlet" type="{urn:zimbraAccount}accountZimletDesc" minOccurs="0"/>
 *         &lt;element name="zimletConfig" type="{urn:zimbraAccount}accountZimletConfigInfo" minOccurs="0"/>
 *         &lt;any processContents='skip' namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accountZimletInfo", propOrder = {
    "zimletContext",
    "zimlet",
    "zimletConfig",
    "any"
})
public class testAccountZimletInfo {

    protected testAccountZimletContext zimletContext;
    protected testAccountZimletDesc zimlet;
    protected testAccountZimletConfigInfo zimletConfig;
    @XmlAnyElement
    protected Element any;

    /**
     * Gets the value of the zimletContext property.
     * 
     * @return
     *     possible object is
     *     {@link testAccountZimletContext }
     *     
     */
    public testAccountZimletContext getZimletContext() {
        return zimletContext;
    }

    /**
     * Sets the value of the zimletContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAccountZimletContext }
     *     
     */
    public void setZimletContext(testAccountZimletContext value) {
        this.zimletContext = value;
    }

    /**
     * Gets the value of the zimlet property.
     * 
     * @return
     *     possible object is
     *     {@link testAccountZimletDesc }
     *     
     */
    public testAccountZimletDesc getZimlet() {
        return zimlet;
    }

    /**
     * Sets the value of the zimlet property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAccountZimletDesc }
     *     
     */
    public void setZimlet(testAccountZimletDesc value) {
        this.zimlet = value;
    }

    /**
     * Gets the value of the zimletConfig property.
     * 
     * @return
     *     possible object is
     *     {@link testAccountZimletConfigInfo }
     *     
     */
    public testAccountZimletConfigInfo getZimletConfig() {
        return zimletConfig;
    }

    /**
     * Sets the value of the zimletConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAccountZimletConfigInfo }
     *     
     */
    public void setZimletConfig(testAccountZimletConfigInfo value) {
        this.zimletConfig = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Element }
     *     
     */
    public Element getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Element }
     *     
     */
    public void setAny(Element value) {
        this.any = value;
    }

}
