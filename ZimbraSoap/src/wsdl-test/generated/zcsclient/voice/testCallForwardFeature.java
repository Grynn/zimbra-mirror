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

package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for callForwardFeature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="callForwardFeature">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}callFeatureInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ft" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "callForwardFeature")
public class testCallForwardFeature
    extends testCallFeatureInfo
{

    @XmlAttribute(name = "ft")
    protected String ft;

    /**
     * Gets the value of the ft property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFt() {
        return ft;
    }

    /**
     * Sets the value of the ft property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFt(String value) {
        this.ft = value;
    }

}
