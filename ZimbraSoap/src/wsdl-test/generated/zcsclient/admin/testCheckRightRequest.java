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

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkRightRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkRightRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}adminAttrsImpl">
 *       &lt;sequence>
 *         &lt;element name="target" type="{urn:zimbraAdmin}effectiveRightsTargetSelector"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeSelector"/>
 *         &lt;element name="right" type="{urn:zimbraAdmin}checkedRight"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkRightRequest", propOrder = {
    "target",
    "grantee",
    "right"
})
public class testCheckRightRequest
    extends testAdminAttrsImpl
{

    @XmlElement(required = true)
    protected testEffectiveRightsTargetSelector target;
    @XmlElement(required = true)
    protected testGranteeSelector grantee;
    @XmlElement(required = true)
    protected String right;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveRightsTargetSelector }
     *     
     */
    public testEffectiveRightsTargetSelector getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveRightsTargetSelector }
     *     
     */
    public void setTarget(testEffectiveRightsTargetSelector value) {
        this.target = value;
    }

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeSelector }
     *     
     */
    public testGranteeSelector getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeSelector }
     *     
     */
    public void setGrantee(testGranteeSelector value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRight(String value) {
        this.right = value;
    }

}
