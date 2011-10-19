
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for grantInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="grantInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="target" type="{urn:zimbraAdmin}typeIdName"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeInfo"/>
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
@XmlType(name = "grantInfo", propOrder = {
    "target",
    "grantee",
    "right"
})
public class testGrantInfo {

    @XmlElement(required = true)
    protected testTypeIdName target;
    @XmlElement(required = true)
    protected testGranteeInfo grantee;
    @XmlElement(required = true)
    protected testRightModifierInfo right;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link testTypeIdName }
     *     
     */
    public testTypeIdName getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTypeIdName }
     *     
     */
    public void setTarget(testTypeIdName value) {
        this.target = value;
    }

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeInfo }
     *     
     */
    public testGranteeInfo getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeInfo }
     *     
     */
    public void setGrantee(testGranteeInfo value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link testRightModifierInfo }
     *     
     */
    public testRightModifierInfo getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRightModifierInfo }
     *     
     */
    public void setRight(testRightModifierInfo value) {
        this.right = value;
    }

}
