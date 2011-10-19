
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkRecurConflictsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkRecurConflictsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inst" type="{urn:zimbraMail}conflictRecurrenceInstance" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkRecurConflictsResponse", propOrder = {
    "inst"
})
public class testCheckRecurConflictsResponse {

    protected List<testConflictRecurrenceInstance> inst;

    /**
     * Gets the value of the inst property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inst property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInst().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testConflictRecurrenceInstance }
     * 
     * 
     */
    public List<testConflictRecurrenceInstance> getInst() {
        if (inst == null) {
            inst = new ArrayList<testConflictRecurrenceInstance>();
        }
        return this.inst;
    }

}
