
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deviceStatusInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deviceStatusInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="provisionable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="firstReqReceived" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lastPolicyUpdate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="remoteWipeReqTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="remoteWipeAckTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="recoveryPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUsedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ua" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="protocol" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="model" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="imei" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="friendly_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="os" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="os_language" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="phone_number" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deviceStatusInfo", propOrder = {
    "provisionable",
    "status",
    "firstReqReceived",
    "lastPolicyUpdate",
    "remoteWipeReqTime",
    "remoteWipeAckTime",
    "recoveryPassword",
    "lastUsedDate"
})
public class testDeviceStatusInfo {

    protected boolean provisionable;
    protected byte status;
    protected int firstReqReceived;
    protected Integer lastPolicyUpdate;
    protected Integer remoteWipeReqTime;
    protected Integer remoteWipeAckTime;
    protected String recoveryPassword;
    protected String lastUsedDate;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "ua")
    protected String ua;
    @XmlAttribute(name = "protocol")
    protected String protocol;
    @XmlAttribute(name = "model")
    protected String model;
    @XmlAttribute(name = "imei")
    protected String imei;
    @XmlAttribute(name = "friendly_name")
    protected String friendlyName;
    @XmlAttribute(name = "os")
    protected String os;
    @XmlAttribute(name = "os_language")
    protected String osLanguage;
    @XmlAttribute(name = "phone_number")
    protected String phoneNumber;

    /**
     * Gets the value of the provisionable property.
     * 
     */
    public boolean isProvisionable() {
        return provisionable;
    }

    /**
     * Sets the value of the provisionable property.
     * 
     */
    public void setProvisionable(boolean value) {
        this.provisionable = value;
    }

    /**
     * Gets the value of the status property.
     * 
     */
    public byte getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     */
    public void setStatus(byte value) {
        this.status = value;
    }

    /**
     * Gets the value of the firstReqReceived property.
     * 
     */
    public int getFirstReqReceived() {
        return firstReqReceived;
    }

    /**
     * Sets the value of the firstReqReceived property.
     * 
     */
    public void setFirstReqReceived(int value) {
        this.firstReqReceived = value;
    }

    /**
     * Gets the value of the lastPolicyUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLastPolicyUpdate() {
        return lastPolicyUpdate;
    }

    /**
     * Sets the value of the lastPolicyUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLastPolicyUpdate(Integer value) {
        this.lastPolicyUpdate = value;
    }

    /**
     * Gets the value of the remoteWipeReqTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRemoteWipeReqTime() {
        return remoteWipeReqTime;
    }

    /**
     * Sets the value of the remoteWipeReqTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRemoteWipeReqTime(Integer value) {
        this.remoteWipeReqTime = value;
    }

    /**
     * Gets the value of the remoteWipeAckTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRemoteWipeAckTime() {
        return remoteWipeAckTime;
    }

    /**
     * Sets the value of the remoteWipeAckTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRemoteWipeAckTime(Integer value) {
        this.remoteWipeAckTime = value;
    }

    /**
     * Gets the value of the recoveryPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecoveryPassword() {
        return recoveryPassword;
    }

    /**
     * Sets the value of the recoveryPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecoveryPassword(String value) {
        this.recoveryPassword = value;
    }

    /**
     * Gets the value of the lastUsedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastUsedDate() {
        return lastUsedDate;
    }

    /**
     * Sets the value of the lastUsedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUsedDate(String value) {
        this.lastUsedDate = value;
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
     * Gets the value of the ua property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUa() {
        return ua;
    }

    /**
     * Sets the value of the ua property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUa(String value) {
        this.ua = value;
    }

    /**
     * Gets the value of the protocol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the value of the protocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocol(String value) {
        this.protocol = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the imei property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the value of the imei property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImei(String value) {
        this.imei = value;
    }

    /**
     * Gets the value of the friendly_Name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFriendly_Name() {
        return friendlyName;
    }

    /**
     * Sets the value of the friendly_Name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFriendly_Name(String value) {
        this.friendlyName = value;
    }

    /**
     * Gets the value of the os property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOs() {
        return os;
    }

    /**
     * Sets the value of the os property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOs(String value) {
        this.os = value;
    }

    /**
     * Gets the value of the os_Language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOs_Language() {
        return osLanguage;
    }

    /**
     * Sets the value of the os_Language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOs_Language(String value) {
        this.osLanguage = value;
    }

    /**
     * Gets the value of the phone_Number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone_Number() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phone_Number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone_Number(String value) {
        this.phoneNumber = value;
    }

}
