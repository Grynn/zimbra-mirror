
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkExchangeAuthRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkExchangeAuthRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="auth" type="{urn:zimbraAdmin}exchangeAuthSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkExchangeAuthRequest", propOrder = {
    "auth"
})
public class testCheckExchangeAuthRequest {

    @XmlElement(required = true)
    protected testExchangeAuthSpec auth;

    /**
     * Gets the value of the auth property.
     * 
     * @return
     *     possible object is
     *     {@link testExchangeAuthSpec }
     *     
     */
    public testExchangeAuthSpec getAuth() {
        return auth;
    }

    /**
     * Sets the value of the auth property.
     * 
     * @param value
     *     allowed object is
     *     {@link testExchangeAuthSpec }
     *     
     */
    public void setAuth(testExchangeAuthSpec value) {
        this.auth = value;
    }

}
