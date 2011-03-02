
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for grantRightRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="grantRightRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="target" type="{urn:zimbraAdmin}effectiveRightsTargetSelector"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeSelector"/>
 *         &lt;element name="right" type="{urn:zimbraAdmin}rightModifierInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "grantRightRequest", propOrder = {
    "target",
    "grantee",
    "right"
})
public class GrantRightRequest {

    @XmlElement(required = true)
    protected EffectiveRightsTargetSelector target;
    @XmlElement(required = true)
    protected GranteeSelector grantee;
    @XmlElement(required = true)
    protected RightModifierInfo right;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link EffectiveRightsTargetSelector }
     *     
     */
    public EffectiveRightsTargetSelector getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link EffectiveRightsTargetSelector }
     *     
     */
    public void setTarget(EffectiveRightsTargetSelector value) {
        this.target = value;
    }

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link GranteeSelector }
     *     
     */
    public GranteeSelector getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link GranteeSelector }
     *     
     */
    public void setGrantee(GranteeSelector value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link RightModifierInfo }
     *     
     */
    public RightModifierInfo getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link RightModifierInfo }
     *     
     */
    public void setRight(RightModifierInfo value) {
        this.right = value;
    }

}
