
package generated.zcsclient.adminext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for generateBulkProvisionFileFromLDAPRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generateBulkProvisionFileFromLDAPRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdminExt}attrsImpl">
 *       &lt;sequence>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="generatePassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="genPasswordLength" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="fileFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mustChangePassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maxResults" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SMTPHost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMTPPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importMails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importContacts" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importCalendar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importTasks" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importJunk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importDeletedItems" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ignorePreviouslyImported" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InvalidSSLOk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MapiProfile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MapiServer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MapiLogonUserDN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ZimbraAdminLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ZimbraAdminPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TargetDomainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="provisionUsers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generateBulkProvisionFileFromLDAPRequest", propOrder = {
    "password",
    "generatePassword",
    "genPasswordLength",
    "fileFormat",
    "mustChangePassword",
    "maxResults",
    "smtpHost",
    "smtpPort",
    "importMails",
    "importContacts",
    "importCalendar",
    "importTasks",
    "importJunk",
    "importDeletedItems",
    "ignorePreviouslyImported",
    "invalidSSLOk",
    "mapiProfile",
    "mapiServer",
    "mapiLogonUserDN",
    "zimbraAdminLogin",
    "zimbraAdminPassword",
    "targetDomainName",
    "provisionUsers"
})
public class testGenerateBulkProvisionFileFromLDAPRequest
    extends testAttrsImpl
{

    protected String password;
    protected String generatePassword;
    protected Integer genPasswordLength;
    protected String fileFormat;
    @XmlElement(required = true)
    protected String mustChangePassword;
    protected Integer maxResults;
    @XmlElement(name = "SMTPHost")
    protected String smtpHost;
    @XmlElement(name = "SMTPPort")
    protected String smtpPort;
    protected String importMails;
    protected String importContacts;
    protected String importCalendar;
    protected String importTasks;
    protected String importJunk;
    protected String importDeletedItems;
    protected String ignorePreviouslyImported;
    @XmlElement(name = "InvalidSSLOk")
    protected String invalidSSLOk;
    @XmlElement(name = "MapiProfile")
    protected String mapiProfile;
    @XmlElement(name = "MapiServer")
    protected String mapiServer;
    @XmlElement(name = "MapiLogonUserDN")
    protected String mapiLogonUserDN;
    @XmlElement(name = "ZimbraAdminLogin")
    protected String zimbraAdminLogin;
    @XmlElement(name = "ZimbraAdminPassword")
    protected String zimbraAdminPassword;
    @XmlElement(name = "TargetDomainName")
    protected String targetDomainName;
    protected String provisionUsers;

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
     * Gets the value of the fileFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the value of the fileFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileFormat(String value) {
        this.fileFormat = value;
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
     * Gets the value of the importMails property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportMails() {
        return importMails;
    }

    /**
     * Sets the value of the importMails property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportMails(String value) {
        this.importMails = value;
    }

    /**
     * Gets the value of the importContacts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportContacts() {
        return importContacts;
    }

    /**
     * Sets the value of the importContacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportContacts(String value) {
        this.importContacts = value;
    }

    /**
     * Gets the value of the importCalendar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportCalendar() {
        return importCalendar;
    }

    /**
     * Sets the value of the importCalendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportCalendar(String value) {
        this.importCalendar = value;
    }

    /**
     * Gets the value of the importTasks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportTasks() {
        return importTasks;
    }

    /**
     * Sets the value of the importTasks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportTasks(String value) {
        this.importTasks = value;
    }

    /**
     * Gets the value of the importJunk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportJunk() {
        return importJunk;
    }

    /**
     * Sets the value of the importJunk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportJunk(String value) {
        this.importJunk = value;
    }

    /**
     * Gets the value of the importDeletedItems property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportDeletedItems() {
        return importDeletedItems;
    }

    /**
     * Sets the value of the importDeletedItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportDeletedItems(String value) {
        this.importDeletedItems = value;
    }

    /**
     * Gets the value of the ignorePreviouslyImported property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIgnorePreviouslyImported() {
        return ignorePreviouslyImported;
    }

    /**
     * Sets the value of the ignorePreviouslyImported property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIgnorePreviouslyImported(String value) {
        this.ignorePreviouslyImported = value;
    }

    /**
     * Gets the value of the invalidSSLOk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvalidSSLOk() {
        return invalidSSLOk;
    }

    /**
     * Sets the value of the invalidSSLOk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvalidSSLOk(String value) {
        this.invalidSSLOk = value;
    }

    /**
     * Gets the value of the mapiProfile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapiProfile() {
        return mapiProfile;
    }

    /**
     * Sets the value of the mapiProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapiProfile(String value) {
        this.mapiProfile = value;
    }

    /**
     * Gets the value of the mapiServer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapiServer() {
        return mapiServer;
    }

    /**
     * Sets the value of the mapiServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapiServer(String value) {
        this.mapiServer = value;
    }

    /**
     * Gets the value of the mapiLogonUserDN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapiLogonUserDN() {
        return mapiLogonUserDN;
    }

    /**
     * Sets the value of the mapiLogonUserDN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapiLogonUserDN(String value) {
        this.mapiLogonUserDN = value;
    }

    /**
     * Gets the value of the zimbraAdminLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZimbraAdminLogin() {
        return zimbraAdminLogin;
    }

    /**
     * Sets the value of the zimbraAdminLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZimbraAdminLogin(String value) {
        this.zimbraAdminLogin = value;
    }

    /**
     * Gets the value of the zimbraAdminPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZimbraAdminPassword() {
        return zimbraAdminPassword;
    }

    /**
     * Sets the value of the zimbraAdminPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZimbraAdminPassword(String value) {
        this.zimbraAdminPassword = value;
    }

    /**
     * Gets the value of the targetDomainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetDomainName() {
        return targetDomainName;
    }

    /**
     * Sets the value of the targetDomainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetDomainName(String value) {
        this.targetDomainName = value;
    }

    /**
     * Gets the value of the provisionUsers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvisionUsers() {
        return provisionUsers;
    }

    /**
     * Sets the value of the provisionUsers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvisionUsers(String value) {
        this.provisionUsers = value;
    }

}
