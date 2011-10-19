
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for unregisterMailboxMoveOutRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="unregisterMailboxMoveOutRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbraAdmin}mailboxMoveSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unregisterMailboxMoveOutRequest", propOrder = {
    "account"
})
public class testUnregisterMailboxMoveOutRequest {

    @XmlElement(required = true)
    protected testMailboxMoveSpec account;

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link testMailboxMoveSpec }
     *     
     */
    public testMailboxMoveSpec getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMailboxMoveSpec }
     *     
     */
    public void setAccount(testMailboxMoveSpec value) {
        this.account = value;
    }

}
