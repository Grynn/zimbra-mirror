
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkDeviceStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkDeviceStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="device" type="{urn:zimbraMail}idStatus"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkDeviceStatusResponse", propOrder = {
    "device"
})
public class testCheckDeviceStatusResponse {

    @XmlElement(required = true)
    protected testIdStatus device;

    /**
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link testIdStatus }
     *     
     */
    public testIdStatus getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link testIdStatus }
     *     
     */
    public void setDevice(testIdStatus value) {
        this.device = value;
    }

}
