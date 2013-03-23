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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for installLicenseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="installLicenseResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="validFrom" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="validUntil" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="serverTime" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "installLicenseResponse")
public class testInstallLicenseResponse {

    @XmlAttribute(name = "validFrom", required = true)
    protected long validFrom;
    @XmlAttribute(name = "validUntil", required = true)
    protected long validUntil;
    @XmlAttribute(name = "serverTime", required = true)
    protected long serverTime;

    /**
     * Gets the value of the validFrom property.
     * 
     */
    public long getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     */
    public void setValidFrom(long value) {
        this.validFrom = value;
    }

    /**
     * Gets the value of the validUntil property.
     * 
     */
    public long getValidUntil() {
        return validUntil;
    }

    /**
     * Sets the value of the validUntil property.
     * 
     */
    public void setValidUntil(long value) {
        this.validUntil = value;
    }

    /**
     * Gets the value of the serverTime property.
     * 
     */
    public long getServerTime() {
        return serverTime;
    }

    /**
     * Sets the value of the serverTime property.
     * 
     */
    public void setServerTime(long value) {
        this.serverTime = value;
    }

}
