
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for recurrenceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="recurrenceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="add" type="{urn:zimbraMail}addRecurrenceInfo"/>
 *           &lt;element name="exclude" type="{urn:zimbraMail}excludeRecurrenceInfo"/>
 *           &lt;element name="except" type="{urn:zimbraMail}exceptionRuleInfo"/>
 *           &lt;element name="cancel" type="{urn:zimbraMail}cancelRuleInfo"/>
 *           &lt;element name="dates" type="{urn:zimbraMail}singleDates"/>
 *           &lt;element name="rule" type="{urn:zimbraMail}simpleRepeatingRule"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recurrenceInfo", propOrder = {
    "addOrExcludeOrExcept"
})
@XmlSeeAlso({
    AddRecurrenceInfo.class,
    ExcludeRecurrenceInfo.class
})
public class RecurrenceInfo {

    @XmlElements({
        @XmlElement(name = "except", type = ExceptionRuleInfo.class),
        @XmlElement(name = "cancel", type = CancelRuleInfo.class),
        @XmlElement(name = "add", type = AddRecurrenceInfo.class),
        @XmlElement(name = "dates", type = SingleDates.class),
        @XmlElement(name = "rule", type = SimpleRepeatingRule.class),
        @XmlElement(name = "exclude", type = ExcludeRecurrenceInfo.class)
    })
    protected List<Object> addOrExcludeOrExcept;

    /**
     * Gets the value of the addOrExcludeOrExcept property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addOrExcludeOrExcept property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddOrExcludeOrExcept().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExceptionRuleInfo }
     * {@link CancelRuleInfo }
     * {@link AddRecurrenceInfo }
     * {@link SingleDates }
     * {@link SimpleRepeatingRule }
     * {@link ExcludeRecurrenceInfo }
     * 
     * 
     */
    public List<Object> getAddOrExcludeOrExcept() {
        if (addOrExcludeOrExcept == null) {
            addOrExcludeOrExcept = new ArrayList<Object>();
        }
        return this.addOrExcludeOrExcept;
    }

}
