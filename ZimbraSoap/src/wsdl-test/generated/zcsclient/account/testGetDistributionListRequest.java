
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

}
