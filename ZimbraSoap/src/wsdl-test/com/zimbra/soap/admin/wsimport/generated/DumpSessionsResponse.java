
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dumpSessionsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dumpSessionsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="soap" type="{urn:zimbraAdmin}infoForSessionType" minOccurs="0"/>
 *         &lt;element name="imap" type="{urn:zimbraAdmin}infoForSessionType" minOccurs="0"/>
 *         &lt;element name="admin" type="{urn:zimbraAdmin}infoForSessionType" minOccurs="0"/>
 *         &lt;element name="wiki" type="{urn:zimbraAdmin}infoForSessionType" minOccurs="0"/>
 *         &lt;element name="synclistener" type="{urn:zimbraAdmin}infoForSessionType" minOccurs="0"/>
 *         &lt;element name="waitset" type="{urn:zimbraAdmin}infoForSessionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="activeSessions" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dumpSessionsResponse", propOrder = {
    "soap",
    "imap",
    "admin",
    "wiki",
    "synclistener",
    "waitset"
})
public class DumpSessionsResponse {

    protected InfoForSessionType soap;
    protected InfoForSessionType imap;
    protected InfoForSessionType admin;
    protected InfoForSessionType wiki;
    protected InfoForSessionType synclistener;
    protected InfoForSessionType waitset;
    @XmlAttribute(required = true)
    protected int activeSessions;

    /**
     * Gets the value of the soap property.
     * 
     * @return
     *     possible object is
     *     {@link InfoForSessionType }
     *     
     */
    public InfoForSessionType getSoap() {
        return soap;
    }

    /**
     * Sets the value of the soap property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoForSessionType }
     *     
     */
    public void setSoap(InfoForSessionType value) {
        this.soap = value;
    }

    /**
     * Gets the value of the imap property.
     * 
     * @return
     *     possible object is
     *     {@link InfoForSessionType }
     *     
     */
    public InfoForSessionType getImap() {
        return imap;
    }

    /**
     * Sets the value of the imap property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoForSessionType }
     *     
     */
    public void setImap(InfoForSessionType value) {
        this.imap = value;
    }

    /**
     * Gets the value of the admin property.
     * 
     * @return
     *     possible object is
     *     {@link InfoForSessionType }
     *     
     */
    public InfoForSessionType getAdmin() {
        return admin;
    }

    /**
     * Sets the value of the admin property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoForSessionType }
     *     
     */
    public void setAdmin(InfoForSessionType value) {
        this.admin = value;
    }

    /**
     * Gets the value of the wiki property.
     * 
     * @return
     *     possible object is
     *     {@link InfoForSessionType }
     *     
     */
    public InfoForSessionType getWiki() {
        return wiki;
    }

    /**
     * Sets the value of the wiki property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoForSessionType }
     *     
     */
    public void setWiki(InfoForSessionType value) {
        this.wiki = value;
    }

    /**
     * Gets the value of the synclistener property.
     * 
     * @return
     *     possible object is
     *     {@link InfoForSessionType }
     *     
     */
    public InfoForSessionType getSynclistener() {
        return synclistener;
    }

    /**
     * Sets the value of the synclistener property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoForSessionType }
     *     
     */
    public void setSynclistener(InfoForSessionType value) {
        this.synclistener = value;
    }

    /**
     * Gets the value of the waitset property.
     * 
     * @return
     *     possible object is
     *     {@link InfoForSessionType }
     *     
     */
    public InfoForSessionType getWaitset() {
        return waitset;
    }

    /**
     * Sets the value of the waitset property.
     * 
     * @param value
     *     allowed object is
     *     {@link InfoForSessionType }
     *     
     */
    public void setWaitset(InfoForSessionType value) {
        this.waitset = value;
    }

    /**
     * Gets the value of the activeSessions property.
     * 
     */
    public int getActiveSessions() {
        return activeSessions;
    }

    /**
     * Sets the value of the activeSessions property.
     * 
     */
    public void setActiveSessions(int value) {
        this.activeSessions = value;
    }

}
