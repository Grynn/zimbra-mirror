
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testNamedElement;


/**
 * <p>Java class for purgeFreeBusyQueueRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="purgeFreeBusyQueueRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="provider" type="{urn:zimbra}namedElement" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "purgeFreeBusyQueueRequest", propOrder = {
    "provider"
})
public class testPurgeFreeBusyQueueRequest {

    protected testNamedElement provider;

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link testNamedElement }
     *     
     */
    public testNamedElement getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNamedElement }
     *     
     */
    public void setProvider(testNamedElement value) {
        this.provider = value;
    }

}
