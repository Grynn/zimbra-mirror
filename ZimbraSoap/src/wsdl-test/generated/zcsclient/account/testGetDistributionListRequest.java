/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testDistributionListSelector;


/**
 * <p>Java class for getDistributionListRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDistributionListRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAccount}attrsImpl">
 *       &lt;sequence>
 *         &lt;element name="dl" type="{urn:zimbra}distributionListSelector"/>
 *       &lt;/sequence>
 *       &lt;attribute name="needOwners" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="needRights" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDistributionListRequest", propOrder = {
    "dl"
})
public class testGetDistributionListRequest
    extends testAttrsImpl
{

    @XmlElement(required = true)
    protected testDistributionListSelector dl;
    @XmlAttribute(name = "needOwners")
    protected Boolean needOwners;
    @XmlAttribute(name = "needRights")
    protected String needRights;

    /**
     * Gets the value of the dl property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListSelector }
     *     
     */
    public testDistributionListSelector getDl() {
        return dl;
    }

    /**
     * Sets the value of the dl property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListSelector }
     *     
     */
    public void setDl(testDistributionListSelector value) {
        this.dl = value;
    }

    /**
     * Gets the value of the needOwners property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeedOwners() {
        return needOwners;
    }

    /**
     * Sets the value of the needOwners property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedOwners(Boolean value) {
        this.needOwners = value;
    }

    /**
     * Gets the value of the needRights property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNeedRights() {
        return needRights;
    }

    /**
     * Sets the value of the needRights property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNeedRights(String value) {
        this.needRights = value;
    }

}
