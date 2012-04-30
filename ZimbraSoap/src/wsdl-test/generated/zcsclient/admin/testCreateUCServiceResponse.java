
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createUCServiceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createUCServiceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}ucservice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createUCServiceResponse", propOrder = {
    "ucservice"
})
public class testCreateUCServiceResponse {

    protected testUcServiceInfo ucservice;

    /**
     * Gets the value of the ucservice property.
     * 
     * @return
     *     possible object is
     *     {@link testUcServiceInfo }
     *     
     */
    public testUcServiceInfo getUcservice() {
        return ucservice;
    }

    /**
     * Sets the value of the ucservice property.
     * 
     * @param value
     *     allowed object is
     *     {@link testUcServiceInfo }
     *     
     */
    public void setUcservice(testUcServiceInfo value) {
        this.ucservice = value;
    }

}
