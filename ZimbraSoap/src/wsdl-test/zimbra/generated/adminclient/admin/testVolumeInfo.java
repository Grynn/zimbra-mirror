
package zimbra.generated.adminclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for volumeInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="volumeInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rootpath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="compressBlobs" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="compressionThreshold" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="mgbits" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="mbits" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="fgbits" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="fbits" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="isCurrent" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "volumeInfo")
public class testVolumeInfo {

    @XmlAttribute(name = "id", required = true)
    protected short id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "rootpath")
    protected String rootpath;
    @XmlAttribute(name = "type", required = true)
    protected short type;
    @XmlAttribute(name = "compressBlobs")
    protected Boolean compressBlobs;
    @XmlAttribute(name = "compressionThreshold", required = true)
    protected long compressionThreshold;
    @XmlAttribute(name = "mgbits", required = true)
    protected short mgbits;
    @XmlAttribute(name = "mbits", required = true)
    protected short mbits;
    @XmlAttribute(name = "fgbits", required = true)
    protected short fgbits;
    @XmlAttribute(name = "fbits", required = true)
    protected short fbits;
    @XmlAttribute(name = "isCurrent", required = true)
    protected boolean isCurrent;

    /**
     * Gets the value of the id property.
     * 
     */
    public short getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(short value) {
        this.id = value;
    }

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
     * Gets the value of the rootpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootpath() {
        return rootpath;
    }

    /**
     * Sets the value of the rootpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootpath(String value) {
        this.rootpath = value;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public short getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(short value) {
        this.type = value;
    }

    /**
     * Gets the value of the compressBlobs property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCompressBlobs() {
        return compressBlobs;
    }

    /**
     * Sets the value of the compressBlobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCompressBlobs(Boolean value) {
        this.compressBlobs = value;
    }

    /**
     * Gets the value of the compressionThreshold property.
     * 
     */
    public long getCompressionThreshold() {
        return compressionThreshold;
    }

    /**
     * Sets the value of the compressionThreshold property.
     * 
     */
    public void setCompressionThreshold(long value) {
        this.compressionThreshold = value;
    }

    /**
     * Gets the value of the mgbits property.
     * 
     */
    public short getMgbits() {
        return mgbits;
    }

    /**
     * Sets the value of the mgbits property.
     * 
     */
    public void setMgbits(short value) {
        this.mgbits = value;
    }

    /**
     * Gets the value of the mbits property.
     * 
     */
    public short getMbits() {
        return mbits;
    }

    /**
     * Sets the value of the mbits property.
     * 
     */
    public void setMbits(short value) {
        this.mbits = value;
    }

    /**
     * Gets the value of the fgbits property.
     * 
     */
    public short getFgbits() {
        return fgbits;
    }

    /**
     * Sets the value of the fgbits property.
     * 
     */
    public void setFgbits(short value) {
        this.fgbits = value;
    }

    /**
     * Gets the value of the fbits property.
     * 
     */
    public short getFbits() {
        return fbits;
    }

    /**
     * Sets the value of the fbits property.
     * 
     */
    public void setFbits(short value) {
        this.fbits = value;
    }

    /**
     * Gets the value of the isCurrent property.
     * 
     */
    public boolean isIsCurrent() {
        return isCurrent;
    }

    /**
     * Sets the value of the isCurrent property.
     * 
     */
    public void setIsCurrent(boolean value) {
        this.isCurrent = value;
    }

}
