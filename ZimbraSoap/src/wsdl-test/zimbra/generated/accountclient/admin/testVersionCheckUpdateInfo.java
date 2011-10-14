
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for versionCheckUpdateInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versionCheckUpdateInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="critical" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="updateURL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="shortversion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="release" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="buildtype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="platform" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versionCheckUpdateInfo")
public class testVersionCheckUpdateInfo {

    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "critical")
    protected Boolean critical;
    @XmlAttribute(name = "updateURL")
    protected String updateURL;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "shortversion")
    protected String shortversion;
    @XmlAttribute(name = "release")
    protected String release;
    @XmlAttribute(name = "version")
    protected String version;
    @XmlAttribute(name = "buildtype")
    protected String buildtype;
    @XmlAttribute(name = "platform")
    protected String platform;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the critical property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCritical() {
        return critical;
    }

    /**
     * Sets the value of the critical property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCritical(Boolean value) {
        this.critical = value;
    }

    /**
     * Gets the value of the updateURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateURL() {
        return updateURL;
    }

    /**
     * Sets the value of the updateURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateURL(String value) {
        this.updateURL = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the shortversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortversion() {
        return shortversion;
    }

    /**
     * Sets the value of the shortversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortversion(String value) {
        this.shortversion = value;
    }

    /**
     * Gets the value of the release property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelease() {
        return release;
    }

    /**
     * Sets the value of the release property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelease(String value) {
        this.release = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the buildtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildtype() {
        return buildtype;
    }

    /**
     * Sets the value of the buildtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildtype(String value) {
        this.buildtype = value;
    }

    /**
     * Gets the value of the platform property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the value of the platform property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlatform(String value) {
        this.platform = value;
    }

}
