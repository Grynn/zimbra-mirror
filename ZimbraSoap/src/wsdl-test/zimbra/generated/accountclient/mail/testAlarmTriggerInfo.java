
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alarmTriggerInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alarmTriggerInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="abs" type="{urn:zimbraMail}dateAttr" minOccurs="0"/>
 *         &lt;element name="rel" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alarmTriggerInfo", propOrder = {

})
public class testAlarmTriggerInfo {

    protected testDateAttr abs;
    protected testDurationInfo rel;

    /**
     * Gets the value of the abs property.
     * 
     * @return
     *     possible object is
     *     {@link testDateAttr }
     *     
     */
    public testDateAttr getAbs() {
        return abs;
    }

    /**
     * Sets the value of the abs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDateAttr }
     *     
     */
    public void setAbs(testDateAttr value) {
        this.abs = value;
    }

    /**
     * Gets the value of the rel property.
     * 
     * @return
     *     possible object is
     *     {@link testDurationInfo }
     *     
     */
    public testDurationInfo getRel() {
        return rel;
    }

    /**
     * Sets the value of the rel property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDurationInfo }
     *     
     */
    public void setRel(testDurationInfo value) {
        this.rel = value;
    }

}
