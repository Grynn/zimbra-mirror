
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getShareNotificationsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getShareNotificationsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="share" type="{urn:zimbraMail}shareNotificationInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getShareNotificationsResponse", propOrder = {
    "share"
})
public class testGetShareNotificationsResponse {

    protected List<testShareNotificationInfo> share;

    /**
     * Gets the value of the share property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the share property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShare().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testShareNotificationInfo }
     * 
     * 
     */
    public List<testShareNotificationInfo> getShare() {
        if (share == null) {
            share = new ArrayList<testShareNotificationInfo>();
        }
        return this.share;
    }

}
