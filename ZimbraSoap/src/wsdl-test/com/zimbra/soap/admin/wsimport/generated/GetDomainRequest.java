
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDomainRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDomainRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="domain" type="{urn:zimbraAdmin}domainSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applyConfig" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDomainRequest", propOrder = {
    "domain"
})
public class GetDomainRequest
    extends AttributeSelectorImpl
{

    protected DomainSelector domain;
    @XmlAttribute(required = true)
    protected boolean applyConfig;

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link DomainSelector }
     *     
     */
    public DomainSelector getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainSelector }
     *     
     */
    public void setDomain(DomainSelector value) {
        this.domain = value;
    }

    /**
     * Gets the value of the applyConfig property.
     * 
     */
    public boolean isApplyConfig() {
        return applyConfig;
    }

    /**
     * Sets the value of the applyConfig property.
     * 
     */
    public void setApplyConfig(boolean value) {
        this.applyConfig = value;
    }

}
