
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
 *     &lt;extension base="{urn:zimbraAdmin}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="account" type="{urn:zimbraAdmin}accountSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applyCos" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
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
public class GetAccountRequest
    extends AttributeSelectorImpl
{

    protected AccountSelector account;
    @XmlAttribute(required = true)
    protected boolean applyCos;

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link AccountSelector }
     *     
     */
    public AccountSelector getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountSelector }
     *     
     */
    public void setAccount(AccountSelector value) {
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

}
