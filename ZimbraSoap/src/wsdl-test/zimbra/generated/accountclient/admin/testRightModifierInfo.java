
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for rightModifierInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rightModifierInfo">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="deny" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="canDelegate" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="subDomain" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rightModifierInfo", propOrder = {
    "value"
})
public class testRightModifierInfo {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "deny")
    protected Boolean deny;
    @XmlAttribute(name = "canDelegate")
    protected Boolean canDelegate;
    @XmlAttribute(name = "subDomain")
    protected Boolean subDomain;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the deny property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeny() {
        return deny;
    }

    /**
     * Sets the value of the deny property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeny(Boolean value) {
        this.deny = value;
    }

    /**
     * Gets the value of the canDelegate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanDelegate() {
        return canDelegate;
    }

    /**
     * Sets the value of the canDelegate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanDelegate(Boolean value) {
        this.canDelegate = value;
    }

    /**
     * Gets the value of the subDomain property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubDomain() {
        return subDomain;
    }

    /**
     * Sets the value of the subDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubDomain(Boolean value) {
        this.subDomain = value;
    }

}
