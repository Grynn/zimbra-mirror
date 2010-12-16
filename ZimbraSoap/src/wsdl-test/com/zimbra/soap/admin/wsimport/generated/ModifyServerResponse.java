
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifyServerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifyServerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}server" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifyServerResponse", propOrder = {
    "server"
})
public class ModifyServerResponse {

    protected ServerInfo server;

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link ServerInfo }
     *     
     */
    public ServerInfo getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServerInfo }
     *     
     */
    public void setServer(ServerInfo value) {
        this.server = value;
    }

}
