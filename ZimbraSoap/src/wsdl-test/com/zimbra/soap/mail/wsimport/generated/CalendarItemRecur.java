
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calendarItemRecur complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calendarItemRecur">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="exceptId" type="{urn:zimbraMail}exceptionRecurIdInfo" minOccurs="0"/>
 *         &lt;element name="s" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="e" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="dur" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *         &lt;element name="recur" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calendarItemRecur", propOrder = {

})
@XmlSeeAlso({
    ExceptionItemRecur.class,
    InviteItemRecur.class,
    CancelItemRecur.class
})
public class CalendarItemRecur {

    protected ExceptionRecurIdInfo exceptId;
    protected DtTimeInfo s;
    protected DtTimeInfo e;
    protected DurationInfo dur;
    protected RecurrenceInfo recur;

    /**
     * Gets the value of the exceptId property.
     * 
     * @return
     *     possible object is
     *     {@link ExceptionRecurIdInfo }
     *     
     */
    public ExceptionRecurIdInfo getExceptId() {
        return exceptId;
    }

    /**
     * Sets the value of the exceptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExceptionRecurIdInfo }
     *     
     */
    public void setExceptId(ExceptionRecurIdInfo value) {
        this.exceptId = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link DtTimeInfo }
     *     
     */
    public DtTimeInfo getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtTimeInfo }
     *     
     */
    public void setS(DtTimeInfo value) {
        this.s = value;
    }

    /**
     * Gets the value of the e property.
     * 
     * @return
     *     possible object is
     *     {@link DtTimeInfo }
     *     
     */
    public DtTimeInfo getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtTimeInfo }
     *     
     */
    public void setE(DtTimeInfo value) {
        this.e = value;
    }

    /**
     * Gets the value of the dur property.
     * 
     * @return
     *     possible object is
     *     {@link DurationInfo }
     *     
     */
    public DurationInfo getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationInfo }
     *     
     */
    public void setDur(DurationInfo value) {
        this.dur = value;
    }

    /**
     * Gets the value of the recur property.
     * 
     * @return
     *     possible object is
     *     {@link RecurrenceInfo }
     *     
     */
    public RecurrenceInfo getRecur() {
        return recur;
    }

    /**
     * Sets the value of the recur property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecurrenceInfo }
     *     
     */
    public void setRecur(RecurrenceInfo value) {
        this.recur = value;
    }

}
