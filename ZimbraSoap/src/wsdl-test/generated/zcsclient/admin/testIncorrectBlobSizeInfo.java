
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for incorrectBlobSizeInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="incorrectBlobSizeInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blob" type="{urn:zimbraAdmin}blobSizeInfo"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="rev" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="s" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="volumeId" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "incorrectBlobSizeInfo", propOrder = {
    "blob"
})
public class testIncorrectBlobSizeInfo {

    @XmlElement(required = true)
    protected testBlobSizeInfo blob;
    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "rev", required = true)
    protected int rev;
    @XmlAttribute(name = "s", required = true)
    protected long s;
    @XmlAttribute(name = "volumeId", required = true)
    protected short volumeId;

    /**
     * Gets the value of the blob property.
     * 
     * @return
     *     possible object is
     *     {@link testBlobSizeInfo }
     *     
     */
    public testBlobSizeInfo getBlob() {
        return blob;
    }

    /**
     * Sets the value of the blob property.
     * 
     * @param value
     *     allowed object is
     *     {@link testBlobSizeInfo }
     *     
     */
    public void setBlob(testBlobSizeInfo value) {
        this.blob = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the rev property.
     * 
     */
    public int getRev() {
        return rev;
    }

    /**
     * Sets the value of the rev property.
     * 
     */
    public void setRev(int value) {
        this.rev = value;
    }

    /**
     * Gets the value of the s property.
     * 
     */
    public long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     */
    public void setS(long value) {
        this.s = value;
    }

    /**
     * Gets the value of the volumeId property.
     * 
     */
    public short getVolumeId() {
        return volumeId;
    }

    /**
     * Sets the value of the volumeId property.
     * 
     */
    public void setVolumeId(short value) {
        this.volumeId = value;
    }

}
