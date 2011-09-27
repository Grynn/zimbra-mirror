
package zimbra.generated.adminclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ruleConditionGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ruleConditionGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="g" type="{urn:zimbraMail}ruleConditionGroup"/>
 *           &lt;element name="c" type="{urn:zimbraMail}ruleCondition"/>
 *           &lt;element name="action" type="{urn:zimbraMail}ruleAction"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="op" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ruleConditionGroup", propOrder = {
    "gOrCOrAction"
})
public class testRuleConditionGroup {

    @XmlElements({
        @XmlElement(name = "g", type = testRuleConditionGroup.class),
        @XmlElement(name = "action", type = testRuleAction.class),
        @XmlElement(name = "c", type = testRuleCondition.class)
    })
    protected List<Object> gOrCOrAction;
    @XmlAttribute(name = "op", required = true)
    protected String op;

    /**
     * Gets the value of the gOrCOrAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gOrCOrAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGOrCOrAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testRuleConditionGroup }
     * {@link testRuleAction }
     * {@link testRuleCondition }
     * 
     * 
     */
    public List<Object> getGOrCOrAction() {
        if (gOrCOrAction == null) {
            gOrCOrAction = new ArrayList<Object>();
        }
        return this.gOrCOrAction;
    }

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOp(String value) {
        this.op = value;
    }

}
