
package zimbra.generated.adminclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendShareNotificationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendShareNotificationRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="share" type="{urn:zimbraMail}sendShareNotificationSpec"/>
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendShareNotificationRequest", propOrder = {
    "share",
    "notes"
})
public class testSendShareNotificationRequest {

    @XmlElement(required = true)
    protected testSendShareNotificationSpec share;
    protected String notes;

    /**
     * Gets the value of the share property.
     * 
     * @return
     *     possible object is
     *     {@link testSendShareNotificationSpec }
     *     
     */
    public testSendShareNotificationSpec getShare() {
        return share;
    }

    /**
     * Sets the value of the share property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSendShareNotificationSpec }
     *     
     */
    public void setShare(testSendShareNotificationSpec value) {
        this.share = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

}
