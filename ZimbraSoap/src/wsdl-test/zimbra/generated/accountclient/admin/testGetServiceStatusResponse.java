
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getServiceStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getServiceStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timezone" type="{urn:zimbraAdmin}timeZoneInfo"/>
 *         &lt;element name="status" type="{urn:zimbraAdmin}serviceStatus" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getServiceStatusResponse", propOrder = {
    "timezone",
    "status"
})
public class testGetServiceStatusResponse {

    @XmlElement(required = true)
    protected testTimeZoneInfo timezone;
    protected List<testServiceStatus> status;

    /**
     * Gets the value of the timezone property.
     * 
     * @return
     *     possible object is
     *     {@link testTimeZoneInfo }
     *     
     */
    public testTimeZoneInfo getTimezone() {
        return timezone;
    }

    /**
     * Sets the value of the timezone property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTimeZoneInfo }
     *     
     */
    public void setTimezone(testTimeZoneInfo value) {
        this.timezone = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the status property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testServiceStatus }
     * 
     * 
     */
    public List<testServiceStatus> getStatus() {
        if (status == null) {
            status = new ArrayList<testServiceStatus>();
        }
        return this.status;
    }

}
