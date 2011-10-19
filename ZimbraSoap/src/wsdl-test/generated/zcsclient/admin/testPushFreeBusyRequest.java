
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;


/**
 * <p>Java class for pushFreeBusyRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pushFreeBusyRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="domain" type="{urn:zimbraAdmin}names" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:zimbra}id" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pushFreeBusyRequest", propOrder = {
    "domain",
    "account"
})
public class testPushFreeBusyRequest {

    protected testNames domain;
    protected List<testId> account;

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link testNames }
     *     
     */
    public testNames getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNames }
     *     
     */
    public void setDomain(testNames value) {
        this.domain = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the account property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testId }
     * 
     * 
     */
    public List<testId> getAccount() {
        if (account == null) {
            account = new ArrayList<testId>();
        }
        return this.account;
    }

}
