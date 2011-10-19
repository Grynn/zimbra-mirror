
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rightViaInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rightViaInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="target" type="{urn:zimbraAdmin}targetWithType"/>
 *         &lt;element name="grantee" type="{urn:zimbraAdmin}granteeWithType"/>
 *         &lt;element name="right" type="{urn:zimbraAdmin}checkedRight"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rightViaInfo", propOrder = {

})
public class testRightViaInfo {

    @XmlElement(required = true)
    protected testTargetWithType target;
    @XmlElement(required = true)
    protected testGranteeWithType grantee;
    @XmlElement(required = true)
    protected testCheckedRight right;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link testTargetWithType }
     *     
     */
    public testTargetWithType getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTargetWithType }
     *     
     */
    public void setTarget(testTargetWithType value) {
        this.target = value;
    }

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeWithType }
     *     
     */
    public testGranteeWithType getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeWithType }
     *     
     */
    public void setGrantee(testGranteeWithType value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link testCheckedRight }
     *     
     */
    public testCheckedRight getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCheckedRight }
     *     
     */
    public void setRight(testCheckedRight value) {
        this.right = value;
    }

}
