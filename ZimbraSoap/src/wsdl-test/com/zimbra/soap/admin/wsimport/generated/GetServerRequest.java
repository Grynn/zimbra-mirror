
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getServerRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getServerRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="server" type="{urn:zimbraAdmin}serverSelector" minOccurs="0"/>
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
@XmlType(name = "getServerRequest", propOrder = {
    "server"
})
public class GetServerRequest
    extends AttributeSelectorImpl
{

    protected ServerSelector server;
    @XmlAttribute(required = true)
    protected boolean applyConfig;

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
