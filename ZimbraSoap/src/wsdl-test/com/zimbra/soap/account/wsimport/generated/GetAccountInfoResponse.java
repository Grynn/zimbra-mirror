
package com.zimbra.soap.account.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAccountInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAccountInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attr" type="{urn:zimbra}namedValue" maxOccurs="unbounded"/>
 *         &lt;element name="soapURL" type="{urn:zimbra}namedValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="publicURL" type="{urn:zimbra}namedValue" minOccurs="0"/>
 *         &lt;element name="changePasswordURL" type="{urn:zimbra}namedValue" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAccountInfoResponse", propOrder = {
    "name",
    "attr",
    "soapURL",
    "publicURL",
    "changePasswordURL"
})
public class GetAccountInfoResponse {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected List<NamedValue> attr;
    protected List<NamedValue> soapURL;
    protected NamedValue publicURL;
    protected NamedValue changePasswordURL;

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
     * Gets the value of the attr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamedValue }
     * 
     * 
     */
    public List<NamedValue> getAttr() {
        if (attr == null) {
            attr = new ArrayList<NamedValue>();
        }
        return this.attr;
    }

    /**
     * Gets the value of the soapURL property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the soapURL property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoapURL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamedValue }
     * 
     * 
     */
    public List<NamedValue> getSoapURL() {
        if (soapURL == null) {
            soapURL = new ArrayList<NamedValue>();
        }
        return this.soapURL;
    }

    /**
     * Gets the value of the publicURL property.
     * 
     * @return
     *     possible object is
     *     {@link NamedValue }
     *     
     */
    public NamedValue getPublicURL() {
        return publicURL;
    }

    /**
     * Sets the value of the publicURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedValue }
     *     
     */
    public void setPublicURL(NamedValue value) {
        this.publicURL = value;
    }

    /**
     * Gets the value of the changePasswordURL property.
     * 
     * @return
     *     possible object is
     *     {@link NamedValue }
     *     
     */
    public NamedValue getChangePasswordURL() {
        return changePasswordURL;
    }

    /**
     * Sets the value of the changePasswordURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedValue }
     *     
     */
    public void setChangePasswordURL(NamedValue value) {
        this.changePasswordURL = value;
    }

}
