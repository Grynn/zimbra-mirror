
package zimbra.generated.accountclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for accountDataSource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accountDataSource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lastError" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="a" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="importOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="host" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="connectionType" type="{urn:zimbraAccount}adsConnectionType" />
 *       &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pollingInterval" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="useAddressForForwardReply" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="defaultSignature" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="forwardReplySignature" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fromDisplay" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fromAddress" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="replyToAddress" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="replyToDisplay" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="importClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="failingSince" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accountDataSource", propOrder = {
    "lastError",
    "a"
})
@XmlSeeAlso({
    testAccountContactsDataSource.class,
    testAccountPop3DataSource.class,
    testAccountYabDataSource.class,
    testAccountCalDataSource.class,
    testAccountUnknownDataSource.class,
    testAccountCaldavDataSource.class,
    testAccountRssDataSource.class,
    testAccountGalDataSource.class,
    testAccountImapDataSource.class
})
public class testAccountDataSource {

    protected String lastError;
    protected List<String> a;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "l")
    protected String l;
    @XmlAttribute(name = "isEnabled")
    protected Boolean isEnabled;
    @XmlAttribute(name = "importOnly")
    protected Boolean importOnly;
    @XmlAttribute(name = "host")
    protected String host;
    @XmlAttribute(name = "port")
    protected Integer port;
    @XmlAttribute(name = "connectionType")
    protected testAdsConnectionType connectionType;
    @XmlAttribute(name = "username")
    protected String username;
    @XmlAttribute(name = "password")
    protected String password;
    @XmlAttribute(name = "pollingInterval")
    protected String pollingInterval;
    @XmlAttribute(name = "emailAddress")
    protected String emailAddress;
    @XmlAttribute(name = "useAddressForForwardReply")
    protected Boolean useAddressForForwardReply;
    @XmlAttribute(name = "defaultSignature")
    protected String defaultSignature;
    @XmlAttribute(name = "forwardReplySignature")
    protected String forwardReplySignature;
    @XmlAttribute(name = "fromDisplay")
    protected String fromDisplay;
    @XmlAttribute(name = "fromAddress")
    protected String fromAddress;
    @XmlAttribute(name = "replyToAddress")
    protected String replyToAddress;
    @XmlAttribute(name = "replyToDisplay")
    protected String replyToDisplay;
    @XmlAttribute(name = "importClass")
    protected String importClass;
    @XmlAttribute(name = "failingSince")
    protected Long failingSince;

    /**
     * Gets the value of the lastError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * Sets the value of the lastError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastError(String value) {
        this.lastError = value;
    }

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
     * {@link String }
     * 
     * 
     */
    public List<String> getA() {
        if (a == null) {
            a = new ArrayList<String>();
        }
        return this.a;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setL(String value) {
        this.l = value;
    }

    /**
     * Gets the value of the isEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsEnabled() {
        return isEnabled;
    }

    /**
     * Sets the value of the isEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    /**
     * Gets the value of the importOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isImportOnly() {
        return importOnly;
    }

    /**
     * Sets the value of the importOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setImportOnly(Boolean value) {
        this.importOnly = value;
    }

    /**
     * Gets the value of the host property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the value of the host property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPort(Integer value) {
        this.port = value;
    }

    /**
     * Gets the value of the connectionType property.
     * 
     * @return
     *     possible object is
     *     {@link testAdsConnectionType }
     *     
     */
    public testAdsConnectionType getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the value of the connectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAdsConnectionType }
     *     
     */
    public void setConnectionType(testAdsConnectionType value) {
        this.connectionType = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
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
     * Gets the value of the pollingInterval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Sets the value of the pollingInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPollingInterval(String value) {
        this.pollingInterval = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the useAddressForForwardReply property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseAddressForForwardReply() {
        return useAddressForForwardReply;
    }

    /**
     * Sets the value of the useAddressForForwardReply property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseAddressForForwardReply(Boolean value) {
        this.useAddressForForwardReply = value;
    }

    /**
     * Gets the value of the defaultSignature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSignature() {
        return defaultSignature;
    }

    /**
     * Sets the value of the defaultSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSignature(String value) {
        this.defaultSignature = value;
    }

    /**
     * Gets the value of the forwardReplySignature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForwardReplySignature() {
        return forwardReplySignature;
    }

    /**
     * Sets the value of the forwardReplySignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForwardReplySignature(String value) {
        this.forwardReplySignature = value;
    }

    /**
     * Gets the value of the fromDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromDisplay() {
        return fromDisplay;
    }

    /**
     * Sets the value of the fromDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromDisplay(String value) {
        this.fromDisplay = value;
    }

    /**
     * Gets the value of the fromAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Sets the value of the fromAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromAddress(String value) {
        this.fromAddress = value;
    }

    /**
     * Gets the value of the replyToAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplyToAddress() {
        return replyToAddress;
    }

    /**
     * Sets the value of the replyToAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplyToAddress(String value) {
        this.replyToAddress = value;
    }

    /**
     * Gets the value of the replyToDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplyToDisplay() {
        return replyToDisplay;
    }

    /**
     * Sets the value of the replyToDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplyToDisplay(String value) {
        this.replyToDisplay = value;
    }

    /**
     * Gets the value of the importClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportClass() {
        return importClass;
    }

    /**
     * Sets the value of the importClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportClass(String value) {
        this.importClass = value;
    }

    /**
     * Gets the value of the failingSince property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFailingSince() {
        return failingSince;
    }

    /**
     * Sets the value of the failingSince property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFailingSince(Long value) {
        this.failingSince = value;
    }

}
