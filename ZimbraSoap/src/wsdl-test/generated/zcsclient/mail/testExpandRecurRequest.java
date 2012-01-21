
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for expandRecurRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="expandRecurRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="cancel" type="{urn:zimbraMail}expandedRecurrenceCancel"/>
 *           &lt;element name="comp" type="{urn:zimbraMail}expandedRecurrenceInvite"/>
 *           &lt;element name="except" type="{urn:zimbraMail}expandedRecurrenceException"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="s" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="e" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "expandRecurRequest", propOrder = {
    "tz",
    "cancelOrCompOrExcept"
})
public class testExpandRecurRequest {

    protected List<testCalTZInfo> tz;
    @XmlElements({
        @XmlElement(name = "cancel", type = testExpandedRecurrenceCancel.class),
        @XmlElement(name = "comp", type = testExpandedRecurrenceInvite.class),
        @XmlElement(name = "except", type = testExpandedRecurrenceException.class)
    })
    protected List<testExpandedRecurrenceComponent> cancelOrCompOrExcept;
    @XmlAttribute(name = "s", required = true)
    protected long s;
    @XmlAttribute(name = "e", required = true)
    protected long e;

    /**
     * Gets the value of the tz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTz().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCalTZInfo }
     * 
     * 
     */
    public List<testCalTZInfo> getTz() {
        if (tz == null) {
            tz = new ArrayList<testCalTZInfo>();
        }
        return this.tz;
    }

    /**
     * Gets the value of the cancelOrCompOrExcept property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cancelOrCompOrExcept property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCancelOrCompOrExcept().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testExpandedRecurrenceCancel }
     * {@link testExpandedRecurrenceInvite }
     * {@link testExpandedRecurrenceException }
     * 
     * 
     */
    public List<testExpandedRecurrenceComponent> getCancelOrCompOrExcept() {
        if (cancelOrCompOrExcept == null) {
            cancelOrCompOrExcept = new ArrayList<testExpandedRecurrenceComponent>();
        }
        return this.cancelOrCompOrExcept;
    }

    /**
     * Gets the value of the s property.
     * 
     */
    public long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     */
    public void setS(long value) {
        this.s = value;
    }

    /**
     * Gets the value of the e property.
     * 
     */
    public long getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     */
    public void setE(long value) {
        this.e = value;
    }

}
