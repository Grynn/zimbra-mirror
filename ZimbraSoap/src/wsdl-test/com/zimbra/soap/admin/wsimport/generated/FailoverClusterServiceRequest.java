
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for failoverClusterServiceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="failoverClusterServiceRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="service" type="{urn:zimbraAdmin}failoverClusterServiceSpec" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "failoverClusterServiceRequest", propOrder = {
    "service"
})
public class FailoverClusterServiceRequest {

    protected FailoverClusterServiceSpec service;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link FailoverClusterServiceSpec }
     *     
     */
    public FailoverClusterServiceSpec getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link FailoverClusterServiceSpec }
     *     
     */
    public void setService(FailoverClusterServiceSpec value) {
        this.service = value;
    }

}
