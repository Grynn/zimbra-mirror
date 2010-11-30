
package com.zimbra.soap.account.wsimport.generated;

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
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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
 *       &lt;attribute name="fromDisplay" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fromAddress" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="replyToAddress" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="replyToDisplay" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "lastError"
})
@XmlSeeAlso({
    AccountContactsDataSource.class,
    AccountPop3DataSource.class,
    AccountCalDataSource.class,
    AccountImapDataSource.class,
    AccountRssDataSource.class
})
public class AccountDataSource {

    protected String lastError;
    @XmlAttribute
    protected String id;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String l;
    @XmlAttribute
    protected Boolean enabled;
    @XmlAttribute
    protected Boolean importOnly;
    @XmlAttribute
    protected String host;
    @XmlAttribute
    protected Integer port;
    @XmlAttribute
    protected AdsConnectionType connectionType;
    @XmlAttribute
    protected String username;
    @XmlAttribute
    protected String password;
    @XmlAttribute
    protected String pollingInterval;
    @XmlAttribute
    protected String emailAddress;
    @XmlAttribute
    protected Boolean useAddressForForwardReply;
    @XmlAttribute
    protected String defaultSignature;
    @XmlAttribute
    protected String fromDisplay;
    @XmlAttribute
    protected String fromAddress;
    @XmlAttribute
    protected String replyToAddress;
    @XmlAttribute
    protected String replyToDisplay;
    @XmlAttribute
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
     * Gets the value of the enabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnabled(Boolean value) {
        this.enabled = value;
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
     *     {@link AdsConnectionType }
     *     
     */
    public AdsConnectionType getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the value of the connectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdsConnectionType }
     *     
     */
    public void setConnectionType(AdsConnectionType value) {
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
