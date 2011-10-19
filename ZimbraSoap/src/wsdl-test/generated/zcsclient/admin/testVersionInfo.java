
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for versionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="release" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="buildDate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="majorversion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="minorversion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="microversion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="platform" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versionInfo")
public class testVersionInfo {

    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "release", required = true)
    protected String release;
    @XmlAttribute(name = "buildDate", required = true)
    protected String buildDate;
    @XmlAttribute(name = "host", required = true)
    protected String host;
    @XmlAttribute(name = "majorversion", required = true)
    protected String majorversion;
    @XmlAttribute(name = "minorversion", required = true)
    protected String minorversion;
    @XmlAttribute(name = "microversion", required = true)
    protected String microversion;
    @XmlAttribute(name = "platform", required = true)
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
     * Gets the value of the buildDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildDate() {
        return buildDate;
    }

    /**
     * Sets the value of the buildDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildDate(String value) {
        this.buildDate = value;
    }

    /**
     * Gets the value of the host property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the value of the host property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * Gets the value of the majorversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMajorversion() {
        return majorversion;
    }

    /**
     * Sets the value of the majorversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMajorversion(String value) {
        this.majorversion = value;
    }

    /**
     * Gets the value of the minorversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinorversion() {
        return minorversion;
    }

    /**
     * Sets the value of the minorversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinorversion(String value) {
        this.minorversion = value;
    }

    /**
     * Gets the value of the microversion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMicroversion() {
        return microversion;
    }

    /**
     * Sets the value of the microversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMicroversion(String value) {
        this.microversion = value;
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
