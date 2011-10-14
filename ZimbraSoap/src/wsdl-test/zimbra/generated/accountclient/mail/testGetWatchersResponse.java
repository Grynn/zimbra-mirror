
package zimbra.generated.accountclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getWatchersResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getWatchersResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="watcher" type="{urn:zimbraMail}watcherInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getWatchersResponse", propOrder = {
    "watcher"
})
public class testGetWatchersResponse {

    protected List<testWatcherInfo> watcher;

    /**
     * Gets the value of the watcher property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the watcher property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWatcher().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testWatcherInfo }
     * 
     * 
     */
    public List<testWatcherInfo> getWatcher() {
        if (watcher == null) {
            watcher = new ArrayList<testWatcherInfo>();
        }
        return this.watcher;
    }

}
