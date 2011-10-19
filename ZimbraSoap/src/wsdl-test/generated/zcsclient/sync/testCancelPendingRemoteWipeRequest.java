
package generated.zcsclient.sync;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cancelPendingRemoteWipeRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cancelPendingRemoteWipeRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="device" type="{urn:zimbraSync}deviceId"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cancelPendingRemoteWipeRequest", propOrder = {
    "device"
})
public class testCancelPendingRemoteWipeRequest {

    @XmlElement(required = true)
    protected testDeviceId device;

    /**
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link testDeviceId }
     *     
     */
    public testDeviceId getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDeviceId }
     *     
     */
    public void setDevice(testDeviceId value) {
        this.device = value;
    }

}
