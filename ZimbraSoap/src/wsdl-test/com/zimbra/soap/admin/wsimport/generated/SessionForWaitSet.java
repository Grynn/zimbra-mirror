
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sessionForWaitSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sessionForWaitSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WaitSetSession" type="{urn:zimbraAdmin}waitSetSessionInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="account" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="types" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="token" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mboxSyncToken" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="mboxSyncTokenDiff" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sessionForWaitSet", propOrder = {
    "waitSetSession"
})
public class SessionForWaitSet {

    @XmlElement(name = "WaitSetSession")
    protected WaitSetSessionInfo waitSetSession;
    @XmlAttribute(required = true)
    protected String account;
    @XmlAttribute(required = true)
    protected String types;
    @XmlAttribute
    protected String token;
    @XmlAttribute
    protected Integer mboxSyncToken;
    @XmlAttribute
    protected Integer mboxSyncTokenDiff;

    /**
     * Gets the value of the waitSetSession property.
     * 
     * @return
     *     possible object is
     *     {@link WaitSetSessionInfo }
     *     
     */
    public WaitSetSessionInfo getWaitSetSession() {
        return waitSetSession;
    }

    /**
     * Sets the value of the waitSetSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link WaitSetSessionInfo }
     *     
     */
    public void setWaitSetSession(WaitSetSessionInfo value) {
        this.waitSetSession = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccount(String value) {
        this.account = value;
    }

    /**
     * Gets the value of the types property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypes() {
        return types;
    }

    /**
     * Sets the value of the types property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypes(String value) {
        this.types = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Gets the value of the mboxSyncToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMboxSyncToken() {
        return mboxSyncToken;
    }

    /**
     * Sets the value of the mboxSyncToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMboxSyncToken(Integer value) {
        this.mboxSyncToken = value;
    }

    /**
     * Gets the value of the mboxSyncTokenDiff property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMboxSyncTokenDiff() {
        return mboxSyncTokenDiff;
    }

    /**
     * Sets the value of the mboxSyncTokenDiff property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMboxSyncTokenDiff(Integer value) {
        this.mboxSyncTokenDiff = value;
    }

}
