
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getEffectiveRightsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getEffectiveRightsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeInfo"/>
 *         &lt;element name="target" type="{urn:zimbraAdmin}effectiveRightsTargetInfo"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEffectiveRightsResponse", propOrder = {

})
public class GetEffectiveRightsResponse {

    @XmlElement(required = true)
    protected GranteeInfo grantee;
    @XmlElement(required = true)
    protected EffectiveRightsTargetInfo target;

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link GranteeInfo }
     *     
     */
    public GranteeInfo getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link GranteeInfo }
     *     
     */
    public void setGrantee(GranteeInfo value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link EffectiveRightsTargetInfo }
     *     
     */
    public EffectiveRightsTargetInfo getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link EffectiveRightsTargetInfo }
     *     
     */
    public void setTarget(EffectiveRightsTargetInfo value) {
        this.target = value;
    }

}
