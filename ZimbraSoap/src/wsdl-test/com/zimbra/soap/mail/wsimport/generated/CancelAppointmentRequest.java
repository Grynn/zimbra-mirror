
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cancelAppointmentRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cancelAppointmentRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inst" type="{urn:zimbraMail}instanceRecurIdInfo" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" minOccurs="0"/>
 *         &lt;element name="m" type="{urn:zimbraMail}calendarItemMsg" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="comp" type="{http://www.w3.org/2001/XMLSchema}int" />
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
@XmlType(name = "cancelAppointmentRequest", propOrder = {
    "inst",
    "tz",
    "m"
})
@XmlSeeAlso({
    CancelTaskRequest.class
})
public class CancelAppointmentRequest {

    protected InstanceRecurIdInfo inst;
    protected CalTZInfo tz;
    protected CalendarItemMsg m;
    @XmlAttribute
    protected String id;
    @XmlAttribute
    protected Integer comp;
    @XmlAttribute
    protected Integer ms;
    @XmlAttribute
    protected Integer rev;

    /**
     * Gets the value of the inst property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceRecurIdInfo }
     *     
     */
    public InstanceRecurIdInfo getInst() {
        return inst;
    }

    /**
     * Sets the value of the inst property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceRecurIdInfo }
     *     
     */
    public void setInst(InstanceRecurIdInfo value) {
        this.inst = value;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link CalTZInfo }
     *     
     */
    public CalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalTZInfo }
     *     
     */
    public void setTz(CalTZInfo value) {
        this.tz = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarItemMsg }
     *     
     */
    public CalendarItemMsg getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarItemMsg }
     *     
     */
    public void setM(CalendarItemMsg value) {
        this.m = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the comp property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getComp() {
        return comp;
    }

    /**
     * Sets the value of the comp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setComp(Integer value) {
        this.comp = value;
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
