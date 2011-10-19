
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for exceptionRuleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exceptionRuleInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}recurIdInfo">
 *       &lt;sequence>
 *         &lt;element name="add" type="{urn:zimbraAdmin}recurrenceInfo" minOccurs="0"/>
 *         &lt;element name="exclude" type="{urn:zimbraAdmin}recurrenceInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exceptionRuleInfo", propOrder = {
    "add",
    "exclude"
})
public class testExceptionRuleInfo
    extends testRecurIdInfo
{

    protected testRecurrenceInfo add;
    protected testRecurrenceInfo exclude;

    /**
     * Gets the value of the add property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getAdd() {
        return add;
    }

    /**
     * Sets the value of the add property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setAdd(testRecurrenceInfo value) {
        this.add = value;
    }

    /**
     * Gets the value of the exclude property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getExclude() {
        return exclude;
    }

    /**
     * Sets the value of the exclude property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setExclude(testRecurrenceInfo value) {
        this.exclude = value;
    }

}
