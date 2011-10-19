
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupAccountQuerySpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupAccountQuerySpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbraAdmin}name" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="from" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="to" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="backupListOffset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="backupListCount" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupAccountQuerySpec", propOrder = {
    "account"
})
public class testBackupAccountQuerySpec {

    protected List<testName> account;
    @XmlAttribute(name = "target")
    protected String target;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "from")
    protected Long from;
    @XmlAttribute(name = "to")
    protected Long to;
    @XmlAttribute(name = "backupListOffset")
    protected Integer backupListOffset;
    @XmlAttribute(name = "backupListCount")
    protected Integer backupListCount;

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
     * {@link testName }
     * 
     * 
     */
    public List<testName> getAccount() {
        if (account == null) {
            account = new ArrayList<testName>();
        }
        return this.account;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFrom(Long value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTo(Long value) {
        this.to = value;
    }

    /**
     * Gets the value of the backupListOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBackupListOffset() {
        return backupListOffset;
    }

    /**
     * Sets the value of the backupListOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBackupListOffset(Integer value) {
        this.backupListOffset = value;
    }

    /**
     * Gets the value of the backupListCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBackupListCount() {
        return backupListCount;
    }

    /**
     * Sets the value of the backupListCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBackupListCount(Integer value) {
        this.backupListCount = value;
    }

}
