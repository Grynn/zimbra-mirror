
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for installLicenseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="installLicenseResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="validFrom" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="validUntil" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="serverTime" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "installLicenseResponse")
public class testInstallLicenseResponse {

    @XmlAttribute(name = "validFrom", required = true)
    protected long validFrom;
    @XmlAttribute(name = "validUntil", required = true)
    protected long validUntil;
    @XmlAttribute(name = "serverTime", required = true)
    protected long serverTime;

    /**
     * Gets the value of the validFrom property.
     * 
     */
    public long getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     */
    public void setValidFrom(long value) {
        this.validFrom = value;
    }

    /**
     * Gets the value of the validUntil property.
     * 
     */
    public long getValidUntil() {
        return validUntil;
    }

    /**
     * Sets the value of the validUntil property.
     * 
     */
    public void setValidUntil(long value) {
        this.validUntil = value;
    }

    /**
     * Gets the value of the serverTime property.
     * 
     */
    public long getServerTime() {
        return serverTime;
    }

    /**
     * Sets the value of the serverTime property.
     * 
     */
    public void setServerTime(long value) {
        this.serverTime = value;
    }

}
