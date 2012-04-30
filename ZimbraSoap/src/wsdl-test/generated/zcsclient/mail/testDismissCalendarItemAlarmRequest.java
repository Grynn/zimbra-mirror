
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dismissCalendarItemAlarmRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dismissCalendarItemAlarmRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="appt" type="{urn:zimbraMail}dismissAppointmentAlarm"/>
 *           &lt;element name="task" type="{urn:zimbraMail}dismissTaskAlarm"/>
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
@XmlType(name = "dismissCalendarItemAlarmRequest", propOrder = {
    "apptOrTask"
})
public class testDismissCalendarItemAlarmRequest {

    @XmlElements({
        @XmlElement(name = "task", type = testDismissTaskAlarm.class),
        @XmlElement(name = "appt", type = testDismissAppointmentAlarm.class)
    })
    protected List<testDismissAlarm> apptOrTask;

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
     * {@link testDismissTaskAlarm }
     * {@link testDismissAppointmentAlarm }
     * 
     * 
     */
    public List<testDismissAlarm> getApptOrTask() {
        if (apptOrTask == null) {
            apptOrTask = new ArrayList<testDismissAlarm>();
        }
        return this.apptOrTask;
    }

}
