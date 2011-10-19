
package generated.zcsclient.sync;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cancelPendingRemoteWipeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cancelPendingRemoteWipeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="device" type="{urn:zimbraSync}deviceStatusInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cancelPendingRemoteWipeResponse", propOrder = {
    "device"
})
public class testCancelPendingRemoteWipeResponse {

    protected testDeviceStatusInfo device;

    /**
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link testDeviceStatusInfo }
     *     
     */
    public testDeviceStatusInfo getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDeviceStatusInfo }
     *     
     */
    public void setDevice(testDeviceStatusInfo value) {
        this.device = value;
    }

}
