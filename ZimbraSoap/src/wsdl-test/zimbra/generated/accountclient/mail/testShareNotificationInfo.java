
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shareNotificationInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shareNotificationInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="grantor" type="{urn:zimbraMail}grantor"/>
 *         &lt;element name="link" type="{urn:zimbraMail}linkInfo"/>
 *       &lt;/all>
 *       &lt;attribute name="status" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="d" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shareNotificationInfo", propOrder = {

})
public class testShareNotificationInfo {

    @XmlElement(required = true)
    protected testGrantor grantor;
    @XmlElement(required = true)
    protected testLinkInfo link;
    @XmlAttribute(name = "status", required = true)
    protected String status;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "d", required = true)
    protected long d;

    /**
     * Gets the value of the grantor property.
     * 
     * @return
     *     possible object is
     *     {@link testGrantor }
     *     
     */
    public testGrantor getGrantor() {
        return grantor;
    }

    /**
     * Sets the value of the grantor property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGrantor }
     *     
     */
    public void setGrantor(testGrantor value) {
        this.grantor = value;
    }

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link testLinkInfo }
     *     
     */
    public testLinkInfo getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link testLinkInfo }
     *     
     */
    public void setLink(testLinkInfo value) {
        this.link = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

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
     * Gets the value of the d property.
     * 
     */
    public long getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     */
    public void setD(long value) {
        this.d = value;
    }

}
