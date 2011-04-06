
package com.zimbra.soap.mail.wsimport.generated;

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
 *     &lt;extension base="{urn:zimbraMail}recurIdInfo">
 *       &lt;all>
 *         &lt;element name="add" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *         &lt;element name="exclude" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *       &lt;/all>
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
public class ExceptionRuleInfo
    extends RecurIdInfo
{

    protected RecurrenceInfo add;
    protected RecurrenceInfo exclude;

    /**
     * Gets the value of the add property.
     * 
     * @return
     *     possible object is
     *     {@link RecurrenceInfo }
     *     
     */
    public RecurrenceInfo getAdd() {
        return add;
    }

    /**
     * Sets the value of the add property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecurrenceInfo }
     *     
     */
    public void setAdd(RecurrenceInfo value) {
        this.add = value;
    }

    /**
     * Gets the value of the exclude property.
     * 
     * @return
     *     possible object is
     *     {@link RecurrenceInfo }
     *     
     */
    public RecurrenceInfo getExclude() {
        return exclude;
    }

    /**
     * Sets the value of the exclude property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecurrenceInfo }
     *     
     */
    public void setExclude(RecurrenceInfo value) {
        this.exclude = value;
    }

}
