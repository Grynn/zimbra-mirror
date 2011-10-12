
package zimbra.generated.accountclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.accountclient.zm.testLicenseStatus;


/**
 * <p>Java class for licenseInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="licenseInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attr" type="{urn:zimbraAccount}licenseAttr"/>
 *       &lt;/sequence>
 *       &lt;attribute name="status" use="required" type="{urn:zimbra}licenseStatus" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "licenseInfo", propOrder = {
    "attr"
})
public class testLicenseInfo {

    @XmlElement(required = true)
    protected testLicenseAttr attr;
    @XmlAttribute(name = "status", required = true)
    protected testLicenseStatus status;

    /**
     * Gets the value of the attr property.
     * 
     * @return
     *     possible object is
     *     {@link testLicenseAttr }
     *     
     */
    public testLicenseAttr getAttr() {
        return attr;
    }

    /**
     * Sets the value of the attr property.
     * 
     * @param value
     *     allowed object is
     *     {@link testLicenseAttr }
     *     
     */
    public void setAttr(testLicenseAttr value) {
        this.attr = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link testLicenseStatus }
     *     
     */
    public testLicenseStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link testLicenseStatus }
     *     
     */
    public void setStatus(testLicenseStatus value) {
        this.status = value;
    }

}
