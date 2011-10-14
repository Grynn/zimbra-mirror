
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enableSharedReminderRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enableSharedReminderRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="link" type="{urn:zimbraMail}sharedReminderMount"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enableSharedReminderRequest", propOrder = {
    "link"
})
public class testEnableSharedReminderRequest {

    @XmlElement(required = true)
    protected testSharedReminderMount link;

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link testSharedReminderMount }
     *     
     */
    public testSharedReminderMount getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSharedReminderMount }
     *     
     */
    public void setLink(testSharedReminderMount value) {
        this.link = value;
    }

}
