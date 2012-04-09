
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentActionGrant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentActionGrant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="perm" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="gt" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expiry" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentActionGrant")
public class testDocumentActionGrant {

    @XmlAttribute(name = "perm", required = true)
    protected String perm;
    @XmlAttribute(name = "gt", required = true)
    protected String gt;
    @XmlAttribute(name = "expiry")
    protected Long expiry;

    /**
     * Gets the value of the perm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerm() {
        return perm;
    }

    /**
     * Sets the value of the perm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerm(String value) {
        this.perm = value;
    }

    /**
     * Gets the value of the gt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGt() {
        return gt;
    }

    /**
     * Sets the value of the gt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGt(String value) {
        this.gt = value;
    }

    /**
     * Gets the value of the expiry property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getExpiry() {
        return expiry;
    }

    /**
     * Sets the value of the expiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setExpiry(Long value) {
        this.expiry = value;
    }

}
