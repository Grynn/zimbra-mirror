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

package generated.zcsclient.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationMasterStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationMasterStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="catchupStatus" type="{urn:zimbraRepl}replicationMasterCatchupStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="masterOperatingMode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationMasterStatus", propOrder = {
    "catchupStatus"
})
public class testReplicationMasterStatus {

    protected testReplicationMasterCatchupStatus catchupStatus;
    @XmlAttribute(name = "masterOperatingMode", required = true)
    protected String masterOperatingMode;

    /**
     * Gets the value of the catchupStatus property.
     * 
     * @return
     *     possible object is
     *     {@link testReplicationMasterCatchupStatus }
     *     
     */
    public testReplicationMasterCatchupStatus getCatchupStatus() {
        return catchupStatus;
    }

    /**
     * Sets the value of the catchupStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link testReplicationMasterCatchupStatus }
     *     
     */
    public void setCatchupStatus(testReplicationMasterCatchupStatus value) {
        this.catchupStatus = value;
    }

    /**
     * Gets the value of the masterOperatingMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMasterOperatingMode() {
        return masterOperatingMode;
    }

    /**
     * Sets the value of the masterOperatingMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMasterOperatingMode(String value) {
        this.masterOperatingMode = value;
    }

}
