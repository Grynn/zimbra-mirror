
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;


/**
 * <p>Java class for createCalendarItemResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createCalendarItemResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="m" type="{urn:zimbra}id" minOccurs="0"/>
 *         &lt;element name="echo" type="{urn:zimbraMail}calEcho" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="calItemId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="apptId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="invId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="rev" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createCalendarItemResponse", propOrder = {

})
@XmlSeeAlso({
    testCreateAppointmentExceptionResponse.class,
    testCreateTaskResponse.class,
    testCreateTaskExceptionResponse.class,
    testModifyAppointmentResponse.class,
    testSendInviteReplyResponse.class,
    testCreateAppointmentResponse.class
})
public class testCreateCalendarItemResponse {

    protected testId m;
    protected testCalEcho echo;
    @XmlAttribute(name = "calItemId")
    protected String calItemId;
    @XmlAttribute(name = "apptId")
    protected String apptId;
    @XmlAttribute(name = "invId")
    protected String invId;
    @XmlAttribute(name = "ms")
    protected Integer ms;
    @XmlAttribute(name = "rev")
    protected Integer rev;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testId }
     *     
     */
    public testId getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testId }
     *     
     */
    public void setM(testId value) {
        this.m = value;
    }

    /**
     * Gets the value of the echo property.
     * 
     * @return
     *     possible object is
     *     {@link testCalEcho }
     *     
     */
    public testCalEcho getEcho() {
        return echo;
    }

    /**
     * Sets the value of the echo property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalEcho }
     *     
     */
    public void setEcho(testCalEcho value) {
        this.echo = value;
    }

    /**
     * Gets the value of the calItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalItemId() {
        return calItemId;
    }

    /**
     * Sets the value of the calItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalItemId(String value) {
        this.calItemId = value;
    }

    /**
     * Gets the value of the apptId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApptId() {
        return apptId;
    }

    /**
     * Sets the value of the apptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApptId(String value) {
        this.apptId = value;
    }

    /**
     * Gets the value of the invId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvId() {
        return invId;
    }

    /**
     * Sets the value of the invId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvId(String value) {
        this.invId = value;
    }

    /**
     * Gets the value of the ms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMs() {
        return ms;
    }

    /**
     * Sets the value of the ms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMs(Integer value) {
        this.ms = value;
    }

    /**
     * Gets the value of the rev property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRev() {
        return rev;
    }

    /**
     * Sets the value of the rev property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRev(Integer value) {
        this.rev = value;
    }

}
