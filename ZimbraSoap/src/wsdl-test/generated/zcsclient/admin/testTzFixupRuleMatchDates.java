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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzFixupRuleMatchDates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzFixupRuleMatchDates">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="standard" type="{urn:zimbraAdmin}tzFixupRuleMatchDate"/>
 *         &lt;element name="daylight" type="{urn:zimbraAdmin}tzFixupRuleMatchDate"/>
 *       &lt;/all>
 *       &lt;attribute name="stdoff" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="dayoff" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzFixupRuleMatchDates", propOrder = {

})
public class testTzFixupRuleMatchDates {

    @XmlElement(required = true)
    protected testTzFixupRuleMatchDate standard;
    @XmlElement(required = true)
    protected testTzFixupRuleMatchDate daylight;
    @XmlAttribute(name = "stdoff", required = true)
    protected long stdoff;
    @XmlAttribute(name = "dayoff", required = true)
    protected long dayoff;

    /**
     * Gets the value of the standard property.
     * 
     * @return
     *     possible object is
     *     {@link testTzFixupRuleMatchDate }
     *     
     */
    public testTzFixupRuleMatchDate getStandard() {
        return standard;
    }

    /**
     * Sets the value of the standard property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTzFixupRuleMatchDate }
     *     
     */
    public void setStandard(testTzFixupRuleMatchDate value) {
        this.standard = value;
    }

    /**
     * Gets the value of the daylight property.
     * 
     * @return
     *     possible object is
     *     {@link testTzFixupRuleMatchDate }
     *     
     */
    public testTzFixupRuleMatchDate getDaylight() {
        return daylight;
    }

    /**
     * Sets the value of the daylight property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTzFixupRuleMatchDate }
     *     
     */
    public void setDaylight(testTzFixupRuleMatchDate value) {
        this.daylight = value;
    }

    /**
     * Gets the value of the stdoff property.
     * 
     */
    public long getStdoff() {
        return stdoff;
    }

    /**
     * Sets the value of the stdoff property.
     * 
     */
    public void setStdoff(long value) {
        this.stdoff = value;
    }

    /**
     * Gets the value of the dayoff property.
     * 
     */
    public long getDayoff() {
        return dayoff;
    }

    /**
     * Sets the value of the dayoff property.
     * 
     */
    public void setDayoff(long value) {
        this.dayoff = value;
    }

}
