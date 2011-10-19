
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteXMPPComponentRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteXMPPComponentRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xmppcomponent" type="{urn:zimbraAdmin}xmppComponentSelector"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteXMPPComponentRequest", propOrder = {
    "xmppcomponent"
})
public class testDeleteXMPPComponentRequest {

    @XmlElement(required = true)
    protected testXmppComponentSelector xmppcomponent;

    /**
     * Gets the value of the xmppcomponent property.
     * 
     * @return
     *     possible object is
     *     {@link testXmppComponentSelector }
     *     
     */
    public testXmppComponentSelector getXmppcomponent() {
        return xmppcomponent;
    }

    /**
     * Sets the value of the xmppcomponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link testXmppComponentSelector }
     *     
     */
    public void setXmppcomponent(testXmppComponentSelector value) {
        this.xmppcomponent = value;
    }

}
