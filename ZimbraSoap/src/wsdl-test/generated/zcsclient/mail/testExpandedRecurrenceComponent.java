
package generated.zcsclient.mail;

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
    testExpandedRecurrenceInvite.class,
    testExpandedRecurrenceCancel.class,
    testExpandedRecurrenceException.class
})
public class testExpandedRecurrenceComponent {

    protected testInstanceRecurIdInfo exceptId;
    protected testDurationInfo dur;
    protected testRecurrenceInfo recur;
    @XmlAttribute(name = "s")
    protected Long s;
    @XmlAttribute(name = "e")
    protected Long e;

    /**
     * Gets the value of the exceptId property.
     * 
     * @return
     *     possible object is
     *     {@link testInstanceRecurIdInfo }
     *     
     */
    public testInstanceRecurIdInfo getExceptId() {
        return exceptId;
    }

    /**
     * Sets the value of the exceptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link testInstanceRecurIdInfo }
     *     
     */
    public void setExceptId(testInstanceRecurIdInfo value) {
        this.exceptId = value;
    }

    /**
     * Gets the value of the dur property.
     * 
     * @return
     *     possible object is
     *     {@link testDurationInfo }
     *     
     */
    public testDurationInfo getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDurationInfo }
     *     
     */
    public void setDur(testDurationInfo value) {
        this.dur = value;
    }

    /**
     * Gets the value of the recur property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getRecur() {
        return recur;
    }

    /**
     * Sets the value of the recur property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setRecur(testRecurrenceInfo value) {
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
