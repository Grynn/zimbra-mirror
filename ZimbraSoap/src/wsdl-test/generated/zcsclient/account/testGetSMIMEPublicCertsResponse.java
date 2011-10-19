
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getSMIMEPublicCertsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSMIMEPublicCertsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="certs" type="{urn:zimbraAccount}smimePublicCertsInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSMIMEPublicCertsResponse", propOrder = {
    "certs"
})
public class testGetSMIMEPublicCertsResponse {

    protected testSmimePublicCertsInfo certs;

    /**
     * Gets the value of the certs property.
     * 
     * @return
     *     possible object is
     *     {@link testSmimePublicCertsInfo }
     *     
     */
    public testSmimePublicCertsInfo getCerts() {
        return certs;
    }

    /**
     * Sets the value of the certs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSmimePublicCertsInfo }
     *     
     */
    public void setCerts(testSmimePublicCertsInfo value) {
        this.certs = value;
    }

}
