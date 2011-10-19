
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;
import generated.zcsclient.zm.testIdAndType;


/**
 * <p>Java class for waitSetResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waitSetResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="a" type="{urn:zimbra}id" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="error" type="{urn:zimbra}idAndType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="waitSet" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="canceled" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waitSetResponse", propOrder = {
    "a",
    "error"
})
public class testWaitSetResponse {

    protected List<testId> a;
    protected List<testIdAndType> error;
    @XmlAttribute(name = "waitSet", required = true)
    protected String waitSet;
    @XmlAttribute(name = "canceled")
    protected Boolean canceled;
    @XmlAttribute(name = "seq")
    protected String seq;

    /**
     * Gets the value of the a property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the a property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testId }
     * 
     * 
     */
    public List<testId> getA() {
        if (a == null) {
            a = new ArrayList<testId>();
        }
        return this.a;
    }

    /**
     * Gets the value of the error property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testIdAndType }
     * 
     * 
     */
    public List<testIdAndType> getError() {
        if (error == null) {
            error = new ArrayList<testIdAndType>();
        }
        return this.error;
    }

    /**
     * Gets the value of the waitSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaitSet() {
        return waitSet;
    }

    /**
     * Sets the value of the waitSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitSet(String value) {
        this.waitSet = value;
    }

    /**
     * Gets the value of the canceled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanceled() {
        return canceled;
    }

    /**
     * Sets the value of the canceled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanceled(Boolean value) {
        this.canceled = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeq(String value) {
        this.seq = value;
    }

}
