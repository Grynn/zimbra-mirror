
package zimbra.generated.adminclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import zimbra.generated.adminclient.zm.testDistributionListOwnerBy;
import zimbra.generated.adminclient.zm.testDistributionListOwnerType;


/**
 * <p>Java class for distributionListOwnerSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionListOwnerSelector">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="type" use="required" type="{urn:zimbra}distributionListOwnerType" />
 *       &lt;attribute name="by" use="required" type="{urn:zimbra}distributionListOwnerBy" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionListOwnerSelector", propOrder = {
    "value"
})
public class testDistributionListOwnerSelector {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "type", required = true)
    protected testDistributionListOwnerType type;
    @XmlAttribute(name = "by", required = true)
    protected testDistributionListOwnerBy by;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListOwnerType }
     *     
     */
    public testDistributionListOwnerType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListOwnerType }
     *     
     */
    public void setType(testDistributionListOwnerType value) {
        this.type = value;
    }

    /**
     * Gets the value of the by property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListOwnerBy }
     *     
     */
    public testDistributionListOwnerBy getBy() {
        return by;
    }

    /**
     * Sets the value of the by property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListOwnerBy }
     *     
     */
    public void setBy(testDistributionListOwnerBy value) {
        this.by = value;
    }

}
