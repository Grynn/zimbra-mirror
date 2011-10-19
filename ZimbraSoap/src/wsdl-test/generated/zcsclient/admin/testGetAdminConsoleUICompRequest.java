
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAccountSelector;


/**
 * <p>Java class for getAdminConsoleUICompRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAdminConsoleUICompRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbra}accountSelector" minOccurs="0"/>
 *         &lt;element name="dl" type="{urn:zimbraAdmin}distributionListSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAdminConsoleUICompRequest", propOrder = {
    "account",
    "dl"
})
public class testGetAdminConsoleUICompRequest {

    protected testAccountSelector account;
    protected testDistributionListSelector dl;

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link testAccountSelector }
     *     
     */
    public testAccountSelector getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAccountSelector }
     *     
     */
    public void setAccount(testAccountSelector value) {
        this.account = value;
    }

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

}
