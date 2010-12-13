
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAccountRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAccountRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbraAdmin}account" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applyCos" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="attrs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAccountRequest", propOrder = {
    "account"
})
public class GetAccountRequest {

    protected Account account;
    @XmlAttribute(required = true)
    protected boolean applyCos;
    @XmlAttribute
    protected String attrs;

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link Account }
     *     
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link Account }
     *     
     */
    public void setAccount(Account value) {
        this.account = value;
    }

    /**
     * Gets the value of the applyCos property.
     * 
     */
    public boolean isApplyCos() {
        return applyCos;
    }

    /**
     * Sets the value of the applyCos property.
     * 
     */
    public void setApplyCos(boolean value) {
        this.applyCos = value;
    }

    /**
     * Gets the value of the attrs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttrs() {
        return attrs;
    }

    /**
     * Sets the value of the attrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttrs(String value) {
        this.attrs = value;
    }

}
