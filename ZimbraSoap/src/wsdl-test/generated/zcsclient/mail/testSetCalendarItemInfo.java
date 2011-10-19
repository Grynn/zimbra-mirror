
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setCalendarItemInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setCalendarItemInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="m" type="{urn:zimbraMail}calendarItemMsg" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ptst" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setCalendarItemInfo", propOrder = {
    "m"
})
@XmlSeeAlso({
    testAddAppointmentInviteRequest.class,
    testAddTaskInviteRequest.class
})
public class testSetCalendarItemInfo {

    protected testCalendarItemMsg m;
    @XmlAttribute(name = "ptst")
    protected String ptst;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testCalendarItemMsg }
     *     
     */
    public testCalendarItemMsg getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalendarItemMsg }
     *     
     */
    public void setM(testCalendarItemMsg value) {
        this.m = value;
    }

    /**
     * Gets the value of the ptst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPtst() {
        return ptst;
    }

    /**
     * Sets the value of the ptst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPtst(String value) {
        this.ptst = value;
    }

}
