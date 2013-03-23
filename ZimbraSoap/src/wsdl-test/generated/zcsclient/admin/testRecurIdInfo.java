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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for recurIdInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="recurIdInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="rangeType" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="recurId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tz" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ridZ" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recurIdInfo")
@XmlSeeAlso({
    testExceptionRuleInfo.class,
    testCalendarReply.class,
    testCancelRuleInfo.class
})
public class testRecurIdInfo {

    @XmlAttribute(name = "rangeType", required = true)
    protected int rangeType;
    @XmlAttribute(name = "recurId", required = true)
    protected String recurId;
    @XmlAttribute(name = "tz")
    protected String tz;
    @XmlAttribute(name = "ridZ")
    protected String ridZ;

    /**
     * Gets the value of the rangeType property.
     * 
     */
    public int getRangeType() {
        return rangeType;
    }

    /**
     * Sets the value of the rangeType property.
     * 
     */
    public void setRangeType(int value) {
        this.rangeType = value;
    }

    /**
     * Gets the value of the recurId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecurId() {
        return recurId;
    }

    /**
     * Sets the value of the recurId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecurId(String value) {
        this.recurId = value;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTz(String value) {
        this.tz = value;
    }

    /**
     * Gets the value of the ridZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRidZ() {
        return ridZ;
    }

    /**
     * Sets the value of the ridZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRidZ(String value) {
        this.ridZ = value;
    }

}
