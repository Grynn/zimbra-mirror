
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.accountclient.zm.testNamedElement;


/**
 * <p>Java class for fixCalendarTZRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fixCalendarTZRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbra}namedElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tzfixup" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fixupRule" type="{urn:zimbraAdmin}tzFixupRule" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="sync" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="after" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fixCalendarTZRequest", propOrder = {
    "account",
    "tzfixup"
})
public class testFixCalendarTZRequest {

    protected List<testNamedElement> account;
    protected testFixCalendarTZRequest.Tzfixup tzfixup;
    @XmlAttribute(name = "sync")
    protected Boolean sync;
    @XmlAttribute(name = "after")
    protected Long after;

    /**
     * Gets the value of the account property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the account property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testNamedElement }
     * 
     * 
     */
    public List<testNamedElement> getAccount() {
        if (account == null) {
            account = new ArrayList<testNamedElement>();
        }
        return this.account;
    }

    /**
     * Gets the value of the tzfixup property.
     * 
     * @return
     *     possible object is
     *     {@link testFixCalendarTZRequest.Tzfixup }
     *     
     */
    public testFixCalendarTZRequest.Tzfixup getTzfixup() {
        return tzfixup;
    }

    /**
     * Sets the value of the tzfixup property.
     * 
     * @param value
     *     allowed object is
     *     {@link testFixCalendarTZRequest.Tzfixup }
     *     
     */
    public void setTzfixup(testFixCalendarTZRequest.Tzfixup value) {
        this.tzfixup = value;
    }

    /**
     * Gets the value of the sync property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSync() {
        return sync;
    }

    /**
     * Sets the value of the sync property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSync(Boolean value) {
        this.sync = value;
    }

    /**
     * Gets the value of the after property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAfter() {
        return after;
    }

    /**
     * Sets the value of the after property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAfter(Long value) {
        this.after = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="fixupRule" type="{urn:zimbraAdmin}tzFixupRule" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "fixupRule"
    })
    public static class Tzfixup {

        protected List<testTzFixupRule> fixupRule;

        /**
         * Gets the value of the fixupRule property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fixupRule property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFixupRule().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testTzFixupRule }
         * 
         * 
         */
        public List<testTzFixupRule> getFixupRule() {
            if (fixupRule == null) {
                fixupRule = new ArrayList<testTzFixupRule>();
            }
            return this.fixupRule;
        }

    }

}
