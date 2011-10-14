
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getICalResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getICalResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ical" type="{urn:zimbraMail}iCalContent"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getICalResponse", propOrder = {
    "ical"
})
public class testGetICalResponse {

    @XmlElement(required = true)
    protected testICalContent ical;

    /**
     * Gets the value of the ical property.
     * 
     * @return
     *     possible object is
     *     {@link testICalContent }
     *     
     */
    public testICalContent getIcal() {
        return ical;
    }

    /**
     * Sets the value of the ical property.
     * 
     * @param value
     *     allowed object is
     *     {@link testICalContent }
     *     
     */
    public void setIcal(testICalContent value) {
        this.ical = value;
    }

}
