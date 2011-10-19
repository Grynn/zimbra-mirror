
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAllCalendarResourcesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAllCalendarResourcesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="calresource" type="{urn:zimbraAdmin}calendarResourceInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllCalendarResourcesResponse", propOrder = {
    "calresource"
})
public class testGetAllCalendarResourcesResponse {

    protected List<testCalendarResourceInfo> calresource;

    /**
     * Gets the value of the calresource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the calresource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCalresource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCalendarResourceInfo }
     * 
     * 
     */
    public List<testCalendarResourceInfo> getCalresource() {
        if (calresource == null) {
            calresource = new ArrayList<testCalendarResourceInfo>();
        }
        return this.calresource;
    }

}
