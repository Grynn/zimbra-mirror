
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getVoiceFeaturesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getVoiceFeaturesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="phone" type="{urn:zimbraVoice}phoneVoiceFeaturesInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getVoiceFeaturesResponse", propOrder = {
    "phone"
})
public class testGetVoiceFeaturesResponse {

    protected testPhoneVoiceFeaturesInfo phone;

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link testPhoneVoiceFeaturesInfo }
     *     
     */
    public testPhoneVoiceFeaturesInfo getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPhoneVoiceFeaturesInfo }
     *     
     */
    public void setPhone(testPhoneVoiceFeaturesInfo value) {
        this.phone = value;
    }

}
