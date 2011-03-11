
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMailQueueRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMailQueueRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="server" type="{urn:zimbraAdmin}serverMailQueueQuery"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMailQueueRequest", propOrder = {
    "server"
})
public class GetMailQueueRequest {

    @XmlElement(required = true)
    protected ServerMailQueueQuery server;

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link ServerMailQueueQuery }
     *     
     */
    public ServerMailQueueQuery getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServerMailQueueQuery }
     *     
     */
    public void setServer(ServerMailQueueQuery value) {
        this.server = value;
    }

}
