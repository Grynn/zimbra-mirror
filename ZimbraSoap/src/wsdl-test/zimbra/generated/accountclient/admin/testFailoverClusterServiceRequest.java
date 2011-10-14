
package zimbra.generated.accountclient.admin;

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
public class testFailoverClusterServiceRequest {

    protected testFailoverClusterServiceSpec service;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link testFailoverClusterServiceSpec }
     *     
     */
    public testFailoverClusterServiceSpec getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link testFailoverClusterServiceSpec }
     *     
     */
    public void setService(testFailoverClusterServiceSpec value) {
        this.service = value;
    }

}
