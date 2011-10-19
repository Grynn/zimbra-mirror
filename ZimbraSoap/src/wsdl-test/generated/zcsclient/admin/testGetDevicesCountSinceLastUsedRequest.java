
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDevicesCountSinceLastUsedRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDevicesCountSinceLastUsedRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lastUsedDate" type="{urn:zimbraAdmin}dateString"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDevicesCountSinceLastUsedRequest", propOrder = {
    "lastUsedDate"
})
public class testGetDevicesCountSinceLastUsedRequest {

    @XmlElement(required = true)
    protected testDateString lastUsedDate;

    /**
     * Gets the value of the lastUsedDate property.
     * 
     * @return
     *     possible object is
     *     {@link testDateString }
     *     
     */
    public testDateString getLastUsedDate() {
        return lastUsedDate;
    }

    /**
     * Sets the value of the lastUsedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDateString }
     *     
     */
    public void setLastUsedDate(testDateString value) {
        this.lastUsedDate = value;
    }

}
