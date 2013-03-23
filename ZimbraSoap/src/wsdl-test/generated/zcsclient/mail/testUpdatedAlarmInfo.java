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

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updatedAlarmInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updatedAlarmInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alarmData" type="{urn:zimbraMail}alarmDataInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="calItemId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updatedAlarmInfo", propOrder = {
    "alarmData"
})
@XmlSeeAlso({
    testUpdatedAppointmentAlarmInfo.class,
    testUpdatedTaskAlarmInfo.class
})
public class testUpdatedAlarmInfo {

    protected testAlarmDataInfo alarmData;
    @XmlAttribute(name = "calItemId", required = true)
    protected String calItemId;

    /**
     * Gets the value of the alarmData property.
     * 
     * @return
     *     possible object is
     *     {@link testAlarmDataInfo }
     *     
     */
    public testAlarmDataInfo getAlarmData() {
        return alarmData;
    }

    /**
     * Sets the value of the alarmData property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAlarmDataInfo }
     *     
     */
    public void setAlarmData(testAlarmDataInfo value) {
        this.alarmData = value;
    }

    /**
     * Gets the value of the calItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalItemId() {
        return calItemId;
    }

    /**
     * Sets the value of the calItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalItemId(String value) {
        this.calItemId = value;
    }

}
