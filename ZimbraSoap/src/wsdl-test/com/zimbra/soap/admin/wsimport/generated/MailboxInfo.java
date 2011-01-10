
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailboxInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="groupId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="accountId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="indexVolumeId" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="itemIdCheckPoint" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="contactCount" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="sizeCheckPoint" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="changeCheckPoint" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="trackingSync" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="trackingImap" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="lastbackupat" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="lastSoapAccess" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="newMessages" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxInfo", propOrder = {

})
public class MailboxInfo {

    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute(required = true)
    protected int groupId;
    @XmlAttribute(required = true)
    protected String accountId;
    @XmlAttribute(required = true)
    protected short indexVolumeId;
    @XmlAttribute(required = true)
    protected int itemIdCheckPoint;
    @XmlAttribute(required = true)
    protected int contactCount;
    @XmlAttribute(required = true)
    protected long sizeCheckPoint;
    @XmlAttribute(required = true)
    protected int changeCheckPoint;
    @XmlAttribute(required = true)
    protected int trackingSync;
    @XmlAttribute(required = true)
    protected boolean trackingImap;
    @XmlAttribute
    protected Integer lastbackupat;
    @XmlAttribute(required = true)
    protected int lastSoapAccess;
    @XmlAttribute(required = true)
    protected int newMessages;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the groupId property.
     * 
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     */
    public void setGroupId(int value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountId(String value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the indexVolumeId property.
     * 
     */
    public short getIndexVolumeId() {
        return indexVolumeId;
    }

    /**
     * Sets the value of the indexVolumeId property.
     * 
     */
    public void setIndexVolumeId(short value) {
        this.indexVolumeId = value;
    }

    /**
     * Gets the value of the itemIdCheckPoint property.
     * 
     */
    public int getItemIdCheckPoint() {
        return itemIdCheckPoint;
    }

    /**
     * Sets the value of the itemIdCheckPoint property.
     * 
     */
    public void setItemIdCheckPoint(int value) {
        this.itemIdCheckPoint = value;
    }

    /**
     * Gets the value of the contactCount property.
     * 
     */
    public int getContactCount() {
        return contactCount;
    }

    /**
     * Sets the value of the contactCount property.
     * 
     */
    public void setContactCount(int value) {
        this.contactCount = value;
    }

    /**
     * Gets the value of the sizeCheckPoint property.
     * 
     */
    public long getSizeCheckPoint() {
        return sizeCheckPoint;
    }

    /**
     * Sets the value of the sizeCheckPoint property.
     * 
     */
    public void setSizeCheckPoint(long value) {
        this.sizeCheckPoint = value;
    }

    /**
     * Gets the value of the changeCheckPoint property.
     * 
     */
    public int getChangeCheckPoint() {
        return changeCheckPoint;
    }

    /**
     * Sets the value of the changeCheckPoint property.
     * 
     */
    public void setChangeCheckPoint(int value) {
        this.changeCheckPoint = value;
    }

    /**
     * Gets the value of the trackingSync property.
     * 
     */
    public int getTrackingSync() {
        return trackingSync;
    }

    /**
     * Sets the value of the trackingSync property.
     * 
     */
    public void setTrackingSync(int value) {
        this.trackingSync = value;
    }

    /**
     * Gets the value of the trackingImap property.
     * 
     */
    public boolean isTrackingImap() {
        return trackingImap;
    }

    /**
     * Sets the value of the trackingImap property.
     * 
     */
    public void setTrackingImap(boolean value) {
        this.trackingImap = value;
    }

    /**
     * Gets the value of the lastbackupat property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLastbackupat() {
        return lastbackupat;
    }

    /**
     * Sets the value of the lastbackupat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLastbackupat(Integer value) {
        this.lastbackupat = value;
    }

    /**
     * Gets the value of the lastSoapAccess property.
     * 
     */
    public int getLastSoapAccess() {
        return lastSoapAccess;
    }

    /**
     * Sets the value of the lastSoapAccess property.
     * 
     */
    public void setLastSoapAccess(int value) {
        this.lastSoapAccess = value;
    }

    /**
     * Gets the value of the newMessages property.
     * 
     */
    public int getNewMessages() {
        return newMessages;
    }

    /**
     * Sets the value of the newMessages property.
     * 
     */
    public void setNewMessages(int value) {
        this.newMessages = value;
    }

}
