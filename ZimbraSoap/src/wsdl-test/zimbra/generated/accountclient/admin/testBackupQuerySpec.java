
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupQuerySpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupQuerySpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="from" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="to" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="stats" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="backupListOffset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="backupListCount" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="accountListStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accountListOffset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="accountListCount" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupQuerySpec")
public class testBackupQuerySpec {

    @XmlAttribute(name = "target")
    protected String target;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "from")
    protected Long from;
    @XmlAttribute(name = "to")
    protected Long to;
    @XmlAttribute(name = "stats")
    protected Boolean stats;
    @XmlAttribute(name = "backupListOffset")
    protected Integer backupListOffset;
    @XmlAttribute(name = "backupListCount")
    protected Integer backupListCount;
    @XmlAttribute(name = "accountListStatus")
    protected String accountListStatus;
    @XmlAttribute(name = "accountListOffset")
    protected Integer accountListOffset;
    @XmlAttribute(name = "accountListCount")
    protected Integer accountListCount;

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
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
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
     * Gets the value of the stats property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStats() {
        return stats;
    }

    /**
     * Sets the value of the stats property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStats(Boolean value) {
        this.stats = value;
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

    /**
     * Gets the value of the accountListStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountListStatus() {
        return accountListStatus;
    }

    /**
     * Sets the value of the accountListStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountListStatus(String value) {
        this.accountListStatus = value;
    }

    /**
     * Gets the value of the accountListOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountListOffset() {
        return accountListOffset;
    }

    /**
     * Sets the value of the accountListOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountListOffset(Integer value) {
        this.accountListOffset = value;
    }

    /**
     * Gets the value of the accountListCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountListCount() {
        return accountListCount;
    }

    /**
     * Sets the value of the accountListCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountListCount(Integer value) {
        this.accountListCount = value;
    }

}
