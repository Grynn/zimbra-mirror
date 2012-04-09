
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for acl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="acl">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="grant" type="{urn:zimbraMail}grant" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="internalGrantExpiry" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guestGrantExpiry" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "acl", propOrder = {
    "grant"
})
public class testAcl {

    protected List<testGrant> grant;
    @XmlAttribute(name = "internalGrantExpiry")
    protected Long internalGrantExpiry;
    @XmlAttribute(name = "guestGrantExpiry")
    protected Long guestGrantExpiry;

    /**
     * Gets the value of the grant property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the grant property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGrant().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testGrant }
     * 
     * 
     */
    public List<testGrant> getGrant() {
        if (grant == null) {
            grant = new ArrayList<testGrant>();
        }
        return this.grant;
    }

    /**
     * Gets the value of the internalGrantExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getInternalGrantExpiry() {
        return internalGrantExpiry;
    }

    /**
     * Sets the value of the internalGrantExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setInternalGrantExpiry(Long value) {
        this.internalGrantExpiry = value;
    }

    /**
     * Gets the value of the guestGrantExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getGuestGrantExpiry() {
        return guestGrantExpiry;
    }

    /**
     * Sets the value of the guestGrantExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setGuestGrantExpiry(Long value) {
        this.guestGrantExpiry = value;
    }

}
