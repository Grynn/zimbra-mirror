
package zimbra.generated.mailclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTaskSummariesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTaskSummariesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="appt" type="{urn:zimbraMail}legacyAppointmentData"/>
 *           &lt;element name="task" type="{urn:zimbraMail}legacyTaskData"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTaskSummariesResponse", propOrder = {
    "apptOrTask"
})
public class testGetTaskSummariesResponse {

    @XmlElements({
        @XmlElement(name = "appt", type = testLegacyAppointmentData.class),
        @XmlElement(name = "task", type = testLegacyTaskData.class)
    })
    protected List<testLegacyCalendaringData> apptOrTask;

    /**
     * Gets the value of the apptOrTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the apptOrTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApptOrTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testLegacyAppointmentData }
     * {@link testLegacyTaskData }
     * 
     * 
     */
    public List<testLegacyCalendaringData> getApptOrTask() {
        if (apptOrTask == null) {
            apptOrTask = new ArrayList<testLegacyCalendaringData>();
        }
        return this.apptOrTask;
    }

}
