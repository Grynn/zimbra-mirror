
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceFolderSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceFolderSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="u" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="n" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceFolderSummary")
public class testVoiceFolderSummary {

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "u", required = true)
    protected long u;
    @XmlAttribute(name = "n", required = true)
    protected long n;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the u property.
     * 
     */
    public long getU() {
        return u;
    }

    /**
     * Sets the value of the u property.
     * 
     */
    public void setU(long value) {
        this.u = value;
    }

    /**
     * Gets the value of the n property.
     * 
     */
    public long getN() {
        return n;
    }

    /**
     * Sets the value of the n property.
     * 
     */
    public void setN(long value) {
        this.n = value;
    }

}
