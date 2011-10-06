
package zimbra.generated.mailclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.mailclient.zm.testId;


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
 *         &lt;element name="item" type="{urn:zimbra}id" minOccurs="0"/>
 *         &lt;element name="e" type="{urn:zimbraMail}emailAddrInfo" maxOccurs="unbounded" minOccurs="0"/>
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
    "item",
    "e",
    "notes"
})
public class testSendShareNotificationRequest {

    protected testId item;
    protected List<testEmailAddrInfo> e;
    protected String notes;

    /**
     * Gets the value of the item property.
     * 
     * @return
     *     possible object is
     *     {@link testId }
     *     
     */
    public testId getItem() {
        return item;
    }

    /**
     * Sets the value of the item property.
     * 
     * @param value
     *     allowed object is
     *     {@link testId }
     *     
     */
    public void setItem(testId value) {
        this.item = value;
    }

    /**
     * Gets the value of the e property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the e property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testEmailAddrInfo }
     * 
     * 
     */
    public List<testEmailAddrInfo> getE() {
        if (e == null) {
            e = new ArrayList<testEmailAddrInfo>();
        }
        return this.e;
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
