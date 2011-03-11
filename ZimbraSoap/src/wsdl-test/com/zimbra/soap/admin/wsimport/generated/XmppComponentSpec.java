
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmppComponentSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmppComponentSpec">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}adminAttrsImpl">
 *       &lt;sequence>
 *         &lt;element name="domain" type="{urn:zimbraAdmin}domainSelector"/>
 *         &lt;element name="server" type="{urn:zimbraAdmin}serverSelector"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmppComponentSpec", propOrder = {
    "domain",
    "server"
})
public class XmppComponentSpec
    extends AdminAttrsImpl
{

    @XmlElement(required = true)
    protected DomainSelector domain;
    @XmlElement(required = true)
    protected ServerSelector server;
    @XmlAttribute(required = true)
    protected String name;

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
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link ServerSelector }
     *     
     */
    public ServerSelector getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServerSelector }
     *     
     */
    public void setServer(ServerSelector value) {
        this.server = value;
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

}
