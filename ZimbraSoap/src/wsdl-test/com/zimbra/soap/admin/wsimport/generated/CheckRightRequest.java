
package com.zimbra.soap.admin.wsimport.generated;

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
public class CheckRightRequest
    extends AdminAttrsImpl
{

    @XmlElement(required = true)
    protected EffectiveRightsTargetSelector target;
    @XmlElement(required = true)
    protected GranteeSelector grantee;
    @XmlElement(required = true)
    protected CheckedRight right;

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
     *     {@link CheckedRight }
     *     
     */
    public CheckedRight getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckedRight }
     *     
     */
    public void setRight(CheckedRight value) {
        this.right = value;
    }

}
