
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for versionCheckResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versionCheckResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versionCheck" type="{urn:zimbraAdmin}versionCheckInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versionCheckResponse", propOrder = {
    "versionCheck"
})
public class testVersionCheckResponse {

    protected testVersionCheckInfo versionCheck;

    /**
     * Gets the value of the versionCheck property.
     * 
     * @return
     *     possible object is
     *     {@link testVersionCheckInfo }
     *     
     */
    public testVersionCheckInfo getVersionCheck() {
        return versionCheck;
    }

    /**
     * Sets the value of the versionCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVersionCheckInfo }
     *     
     */
    public void setVersionCheck(testVersionCheckInfo value) {
        this.versionCheck = value;
    }

}
