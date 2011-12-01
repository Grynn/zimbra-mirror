
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAccountSelector;


/**
 * <p>Java class for deleteXMbxSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteXMbxSearchRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchtask" type="{urn:zimbraAdmin}searchID"/>
 *         &lt;element name="account" type="{urn:zimbra}accountSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteXMbxSearchRequest", propOrder = {
    "searchtask",
    "account"
})
public class testDeleteXMbxSearchRequest {

    @XmlElement(required = true)
    protected testSearchID searchtask;
    protected testAccountSelector account;

    /**
     * Gets the value of the searchtask property.
     * 
     * @return
     *     possible object is
     *     {@link testSearchID }
     *     
     */
    public testSearchID getSearchtask() {
        return searchtask;
    }

    /**
     * Sets the value of the searchtask property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSearchID }
     *     
     */
    public void setSearchtask(testSearchID value) {
        this.searchtask = value;
    }

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

}
