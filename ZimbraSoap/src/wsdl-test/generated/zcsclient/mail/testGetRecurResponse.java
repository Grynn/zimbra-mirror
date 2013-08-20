/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getRecurResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRecurResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="cancel" type="{urn:zimbraMail}cancelItemRecur"/>
 *           &lt;element name="except" type="{urn:zimbraMail}exceptionItemRecur"/>
 *           &lt;element name="comp" type="{urn:zimbraMail}inviteItemRecur"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRecurResponse", propOrder = {
    "tz",
    "cancelOrExceptOrComp"
})
public class testGetRecurResponse {

    protected testCalTZInfo tz;
    @XmlElements({
        @XmlElement(name = "except", type = testExceptionItemRecur.class),
        @XmlElement(name = "comp", type = testInviteItemRecur.class),
        @XmlElement(name = "cancel", type = testCancelItemRecur.class)
    })
    protected List<testCalendarItemRecur> cancelOrExceptOrComp;

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link testCalTZInfo }
     *     
     */
    public testCalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalTZInfo }
     *     
     */
    public void setTz(testCalTZInfo value) {
        this.tz = value;
    }

    /**
     * Gets the value of the cancelOrExceptOrComp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cancelOrExceptOrComp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCancelOrExceptOrComp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testExceptionItemRecur }
     * {@link testInviteItemRecur }
     * {@link testCancelItemRecur }
     * 
     * 
     */
    public List<testCalendarItemRecur> getCancelOrExceptOrComp() {
        if (cancelOrExceptOrComp == null) {
            cancelOrExceptOrComp = new ArrayList<testCalendarItemRecur>();
        }
        return this.cancelOrExceptOrComp;
    }

}
