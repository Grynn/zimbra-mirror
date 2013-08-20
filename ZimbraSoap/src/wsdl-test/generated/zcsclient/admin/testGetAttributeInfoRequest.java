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
 * <p>Java class for getAttributeInfoRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAttributeInfoRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="attrs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="entryTypes" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAttributeInfoRequest")
public class testGetAttributeInfoRequest {

    @XmlAttribute(name = "attrs")
    protected String attrs;
    @XmlAttribute(name = "entryTypes")
    protected String entryTypes;

    /**
     * Gets the value of the attrs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttrs(String value) {
        this.attrs = value;
    }

    /**
     * Gets the value of the entryTypes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntryTypes() {
        return entryTypes;
    }

    /**
     * Sets the value of the entryTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntryTypes(String value) {
        this.entryTypes = value;
    }

}
