
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for exportMailboxSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exportMailboxSelector">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dest" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="destPort" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tempDir" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="overwrite" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exportMailboxSelector")
public class testExportMailboxSelector {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "dest", required = true)
    protected String dest;
    @XmlAttribute(name = "destPort")
    protected Integer destPort;
    @XmlAttribute(name = "tempDir")
    protected String tempDir;
    @XmlAttribute(name = "overwrite")
    protected Boolean overwrite;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the dest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDest() {
        return dest;
    }

    /**
     * Sets the value of the dest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDest(String value) {
        this.dest = value;
    }

    /**
     * Gets the value of the destPort property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDestPort() {
        return destPort;
    }

    /**
     * Sets the value of the destPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDestPort(Integer value) {
        this.destPort = value;
    }

    /**
     * Gets the value of the tempDir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTempDir() {
        return tempDir;
    }

    /**
     * Sets the value of the tempDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTempDir(String value) {
        this.tempDir = value;
    }

    /**
     * Gets the value of the overwrite property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Sets the value of the overwrite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOverwrite(Boolean value) {
        this.overwrite = value;
    }

}
