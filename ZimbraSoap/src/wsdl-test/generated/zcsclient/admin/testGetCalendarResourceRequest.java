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
import generated.zcsclient.zm.testAttributeSelectorImpl;


/**
 * <p>Java class for getCalendarResourceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCalendarResourceRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="calresource" type="{urn:zimbraAdmin}calendarResourceSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applyCos" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCalendarResourceRequest", propOrder = {
    "calresource"
})
public class testGetCalendarResourceRequest
    extends testAttributeSelectorImpl
{

    protected testCalendarResourceSelector calresource;
    @XmlAttribute(name = "applyCos")
    protected Boolean applyCos;

    /**
     * Gets the value of the calresource property.
     * 
     * @return
     *     possible object is
     *     {@link testCalendarResourceSelector }
     *     
     */
    public testCalendarResourceSelector getCalresource() {
        return calresource;
    }

    /**
     * Sets the value of the calresource property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalendarResourceSelector }
     *     
     */
    public void setCalresource(testCalendarResourceSelector value) {
        this.calresource = value;
    }

    /**
     * Gets the value of the applyCos property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isApplyCos() {
        return applyCos;
    }

    /**
     * Sets the value of the applyCos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApplyCos(Boolean value) {
        this.applyCos = value;
    }

}
