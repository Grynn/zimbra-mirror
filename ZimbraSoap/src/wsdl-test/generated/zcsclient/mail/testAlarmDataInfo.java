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

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alarmDataInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alarmDataInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alarm" type="{urn:zimbraMail}alarmInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nextAlarm" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="alarmInstStart" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="invId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="compNum" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="loc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alarmDataInfo", propOrder = {
    "alarm"
})
public class testAlarmDataInfo {

    protected testAlarmInfo alarm;
    @XmlAttribute(name = "nextAlarm")
    protected Long nextAlarm;
    @XmlAttribute(name = "alarmInstStart")
    protected Long alarmInstStart;
    @XmlAttribute(name = "invId")
    protected Integer invId;
    @XmlAttribute(name = "compNum")
    protected Integer compNum;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "loc")
    protected String loc;

    /**
     * Gets the value of the alarm property.
     * 
     * @return
     *     possible object is
     *     {@link testAlarmInfo }
     *     
     */
    public testAlarmInfo getAlarm() {
        return alarm;
    }

    /**
     * Sets the value of the alarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAlarmInfo }
     *     
     */
    public void setAlarm(testAlarmInfo value) {
        this.alarm = value;
    }

    /**
     * Gets the value of the nextAlarm property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNextAlarm() {
        return nextAlarm;
    }

    /**
     * Sets the value of the nextAlarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNextAlarm(Long value) {
        this.nextAlarm = value;
    }

    /**
     * Gets the value of the alarmInstStart property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAlarmInstStart() {
        return alarmInstStart;
    }

    /**
     * Sets the value of the alarmInstStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAlarmInstStart(Long value) {
        this.alarmInstStart = value;
    }

    /**
     * Gets the value of the invId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInvId() {
        return invId;
    }

    /**
     * Sets the value of the invId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInvId(Integer value) {
        this.invId = value;
    }

    /**
     * Gets the value of the compNum property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCompNum() {
        return compNum;
    }

    /**
     * Sets the value of the compNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCompNum(Integer value) {
        this.compNum = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the loc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the value of the loc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoc(String value) {
        this.loc = value;
    }

}
