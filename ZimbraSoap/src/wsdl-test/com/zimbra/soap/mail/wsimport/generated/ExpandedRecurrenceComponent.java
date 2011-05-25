
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for expandedRecurrenceComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="expandedRecurrenceComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="exceptId" type="{urn:zimbraMail}instanceRecurIdInfo" minOccurs="0"/>
 *         &lt;element name="dur" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *         &lt;element name="recur" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="e" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "expandedRecurrenceComponent", propOrder = {

})
@XmlSeeAlso({
    ExpandedRecurrenceInvite.class,
    ExpandedRecurrenceCancel.class,
    ExpandedRecurrenceException.class
})
public class ExpandedRecurrenceComponent {

    protected InstanceRecurIdInfo exceptId;
    protected DurationInfo dur;
    protected RecurrenceInfo recur;
    @XmlAttribute
    protected Long s;
    @XmlAttribute
    protected Long e;

    /**
     * Gets the value of the exceptId property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceRecurIdInfo }
     *     
     */
    public InstanceRecurIdInfo getExceptId() {
        return exceptId;
    }

    /**
     * Sets the value of the exceptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceRecurIdInfo }
     *     
     */
    public void setExceptId(InstanceRecurIdInfo value) {
        this.exceptId = value;
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

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setS(Long value) {
        this.s = value;
    }

    /**
     * Gets the value of the e property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setE(Long value) {
        this.e = value;
    }

}
