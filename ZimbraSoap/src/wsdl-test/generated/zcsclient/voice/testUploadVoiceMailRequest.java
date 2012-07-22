
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uploadVoiceMailRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uploadVoiceMailRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="storeprincipal" type="{urn:zimbraVoice}storePrincipalSpec" minOccurs="0"/>
 *         &lt;element name="vm" type="{urn:zimbraVoice}voiceMsgUploadSpec" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadVoiceMailRequest", propOrder = {
    "storeprincipal",
    "vm"
})
public class testUploadVoiceMailRequest {

    protected testStorePrincipalSpec storeprincipal;
    protected testVoiceMsgUploadSpec vm;

    /**
     * Gets the value of the storeprincipal property.
     * 
     * @return
     *     possible object is
     *     {@link testStorePrincipalSpec }
     *     
     */
    public testStorePrincipalSpec getStoreprincipal() {
        return storeprincipal;
    }

    /**
     * Sets the value of the storeprincipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testStorePrincipalSpec }
     *     
     */
    public void setStoreprincipal(testStorePrincipalSpec value) {
        this.storeprincipal = value;
    }

    /**
     * Gets the value of the vm property.
     * 
     * @return
     *     possible object is
     *     {@link testVoiceMsgUploadSpec }
     *     
     */
    public testVoiceMsgUploadSpec getVm() {
        return vm;
    }

    /**
     * Sets the value of the vm property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVoiceMsgUploadSpec }
     *     
     */
    public void setVm(testVoiceMsgUploadSpec value) {
        this.vm = value;
    }

}
