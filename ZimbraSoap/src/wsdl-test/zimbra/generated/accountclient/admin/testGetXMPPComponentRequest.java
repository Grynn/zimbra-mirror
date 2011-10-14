
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.accountclient.zm.testAttributeSelectorImpl;


/**
 * <p>Java class for getXMPPComponentRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getXMPPComponentRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="xmppcomponent" type="{urn:zimbraAdmin}xmppComponentSelector"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getXMPPComponentRequest", propOrder = {
    "xmppcomponent"
})
public class testGetXMPPComponentRequest
    extends testAttributeSelectorImpl
{

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
