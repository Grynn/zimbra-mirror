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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAccountDistributionListsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAccountDistributionListsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ownerOf" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="memberOf" type="{urn:zimbraAccount}memberOfSelector" />
 *       &lt;attribute name="attrs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAccountDistributionListsRequest")
public class testGetAccountDistributionListsRequest {

    @XmlAttribute(name = "ownerOf")
    protected Boolean ownerOf;
    @XmlAttribute(name = "memberOf")
    protected testMemberOfSelector memberOf;
    @XmlAttribute(name = "attrs")
    protected String attrs;

    /**
     * Gets the value of the ownerOf property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOwnerOf() {
        return ownerOf;
    }

    /**
     * Sets the value of the ownerOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOwnerOf(Boolean value) {
        this.ownerOf = value;
    }

    /**
     * Gets the value of the memberOf property.
     * 
     * @return
     *     possible object is
     *     {@link testMemberOfSelector }
     *     
     */
    public testMemberOfSelector getMemberOf() {
        return memberOf;
    }

    /**
     * Sets the value of the memberOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMemberOfSelector }
     *     
     */
    public void setMemberOf(testMemberOfSelector value) {
        this.memberOf = value;
    }

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

}
