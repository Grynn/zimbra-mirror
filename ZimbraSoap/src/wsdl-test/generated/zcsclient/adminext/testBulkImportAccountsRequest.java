
package generated.zcsclient.adminext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bulkImportAccountsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bulkImportAccountsRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdminExt}attrsImpl">
 *       &lt;sequence>
 *         &lt;element name="createDomains" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SMTPHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="aid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="genPasswordLength" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="generatePassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maxResults" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mustChangePassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="op" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bulkImportAccountsRequest", propOrder = {
    "createDomains",
    "smtpHost",
    "smtpPort",
    "sourceType",
    "aid",
    "password",
    "genPasswordLength",
    "generatePassword",
    "maxResults",
    "mustChangePassword"
})
public class testBulkImportAccountsRequest
    extends testAttrsImpl
{

    @XmlElement(required = true)
    protected String createDomains;
    @XmlElement(name = "SMTPHost")
    protected String smtpHost;
    @XmlElement(name = "SMTPPort")
    protected String smtpPort;
    protected String sourceType;
    protected String aid;
    protected String password;
    protected Integer genPasswordLength;
    protected String generatePassword;
    protected Integer maxResults;
    @XmlElement(required = true)
    protected String mustChangePassword;
    @XmlAttribute(name = "op")
    protected String op;

    /**
     * Gets the value of the createDomains property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateDomains() {
        return createDomains;
    }

    /**
     * Sets the value of the createDomains property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateDomains(String value) {
        this.createDomains = value;
    }

    /**
     * Gets the value of the smtpHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMTPHost() {
        return smtpHost;
    }

    /**
     * Sets the value of the smtpHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMTPHost(String value) {
        this.smtpHost = value;
    }

    /**
     * Gets the value of the smtpPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSMTPPort() {
        return smtpPort;
    }

    /**
     * Sets the value of the smtpPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSMTPPort(String value) {
        this.smtpPort = value;
    }

    /**
     * Gets the value of the sourceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * Sets the value of the sourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceType(String value) {
        this.sourceType = value;
    }

    /**
     * Gets the value of the aid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAid() {
        return aid;
    }

    /**
     * Sets the value of the aid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAid(String value) {
        this.aid = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the genPasswordLength property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGenPasswordLength() {
        return genPasswordLength;
    }

    /**
     * Sets the value of the genPasswordLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGenPasswordLength(Integer value) {
        this.genPasswordLength = value;
    }

    /**
     * Gets the value of the generatePassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneratePassword() {
        return generatePassword;
    }

    /**
     * Sets the value of the generatePassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneratePassword(String value) {
        this.generatePassword = value;
    }

    /**
     * Gets the value of the maxResults property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the value of the maxResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxResults(Integer value) {
        this.maxResults = value;
    }

    /**
     * Gets the value of the mustChangePassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMustChangePassword() {
        return mustChangePassword;
    }

    /**
     * Sets the value of the mustChangePassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMustChangePassword(String value) {
        this.mustChangePassword = value;
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
