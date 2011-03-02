
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getGrantsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getGrantsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="target" type="{urn:zimbraAdmin}effectiveRightsTargetSelector" minOccurs="0"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getGrantsRequest", propOrder = {
    "target",
    "grantee"
})
public class GetGrantsRequest {

    protected EffectiveRightsTargetSelector target;
    protected GranteeSelector grantee;

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

}
