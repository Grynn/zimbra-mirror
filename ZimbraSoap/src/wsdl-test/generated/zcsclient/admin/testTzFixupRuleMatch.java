
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;


/**
 * <p>Java class for tzFixupRuleMatch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzFixupRuleMatch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="any" type="{urn:zimbraAdmin}simpleElement"/>
 *           &lt;element name="tzid" type="{urn:zimbra}id"/>
 *           &lt;element name="nonDst" type="{urn:zimbraAdmin}offset"/>
 *           &lt;element name="rules" type="{urn:zimbraAdmin}tzFixupRuleMatchRules"/>
 *           &lt;element name="dates" type="{urn:zimbraAdmin}tzFixupRuleMatchDates"/>
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
@XmlType(name = "tzFixupRuleMatch", propOrder = {
    "anyOrTzidOrNonDst"
})
public class testTzFixupRuleMatch {

    @XmlElements({
        @XmlElement(name = "rules", type = testTzFixupRuleMatchRules.class),
        @XmlElement(name = "nonDst", type = testOffset.class),
        @XmlElement(name = "tzid", type = testId.class),
        @XmlElement(name = "dates", type = testTzFixupRuleMatchDates.class),
        @XmlElement(name = "any", type = testSimpleElement.class)
    })
    protected List<Object> anyOrTzidOrNonDst;

    /**
     * Gets the value of the anyOrTzidOrNonDst property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the anyOrTzidOrNonDst property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnyOrTzidOrNonDst().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testTzFixupRuleMatchRules }
     * {@link testOffset }
     * {@link testId }
     * {@link testTzFixupRuleMatchDates }
     * {@link testSimpleElement }
     * 
     * 
     */
    public List<Object> getAnyOrTzidOrNonDst() {
        if (anyOrTzidOrNonDst == null) {
            anyOrTzidOrNonDst = new ArrayList<Object>();
        }
        return this.anyOrTzidOrNonDst;
    }

}
