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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzOnsetInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzOnsetInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="week" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="wkday" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="mon" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="mday" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="hour" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="min" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="sec" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzOnsetInfo")
public class testTzOnsetInfo {

    @XmlAttribute(name = "week")
    protected Integer week;
    @XmlAttribute(name = "wkday")
    protected Integer wkday;
    @XmlAttribute(name = "mon", required = true)
    protected int mon;
    @XmlAttribute(name = "mday")
    protected Integer mday;
    @XmlAttribute(name = "hour", required = true)
    protected int hour;
    @XmlAttribute(name = "min", required = true)
    protected int min;
    @XmlAttribute(name = "sec", required = true)
    protected int sec;

    /**
     * Gets the value of the week property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWeek() {
        return week;
    }

    /**
     * Sets the value of the week property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWeek(Integer value) {
        this.week = value;
    }

    /**
     * Gets the value of the wkday property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWkday() {
        return wkday;
    }

    /**
     * Sets the value of the wkday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWkday(Integer value) {
        this.wkday = value;
    }

    /**
     * Gets the value of the mon property.
     * 
     */
    public int getMon() {
        return mon;
    }

    /**
     * Sets the value of the mon property.
     * 
     */
    public void setMon(int value) {
        this.mon = value;
    }

    /**
     * Gets the value of the mday property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMday() {
        return mday;
    }

    /**
     * Sets the value of the mday property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMday(Integer value) {
        this.mday = value;
    }

    /**
     * Gets the value of the hour property.
     * 
     */
    public int getHour() {
        return hour;
    }

    /**
     * Sets the value of the hour property.
     * 
     */
    public void setHour(int value) {
        this.hour = value;
    }

    /**
     * Gets the value of the min property.
     * 
     */
    public int getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     * 
     */
    public void setMin(int value) {
        this.min = value;
    }

    /**
     * Gets the value of the sec property.
     * 
     */
    public int getSec() {
        return sec;
    }

    /**
     * Sets the value of the sec property.
     * 
     */
    public void setSec(int value) {
        this.sec = value;
    }

}
