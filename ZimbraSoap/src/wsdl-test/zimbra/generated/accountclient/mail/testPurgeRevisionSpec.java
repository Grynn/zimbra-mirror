
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for purgeRevisionSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="purgeRevisionSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ver" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="includeOlderRevisions" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "purgeRevisionSpec")
public class testPurgeRevisionSpec {

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "ver", required = true)
    protected int ver;
    @XmlAttribute(name = "includeOlderRevisions")
    protected Boolean includeOlderRevisions;

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
     * Gets the value of the ver property.
     * 
     */
    public int getVer() {
        return ver;
    }

    /**
     * Sets the value of the ver property.
     * 
     */
    public void setVer(int value) {
        this.ver = value;
    }

    /**
     * Gets the value of the includeOlderRevisions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeOlderRevisions() {
        return includeOlderRevisions;
    }

    /**
     * Sets the value of the includeOlderRevisions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeOlderRevisions(Boolean value) {
        this.includeOlderRevisions = value;
    }

}
