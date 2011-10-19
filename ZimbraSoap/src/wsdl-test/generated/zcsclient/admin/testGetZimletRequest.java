
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAttributeSelectorImpl;
import generated.zcsclient.zm.testNamedElement;


/**
 * <p>Java class for getZimletRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getZimletRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="zimlet" type="{urn:zimbra}namedElement"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getZimletRequest", propOrder = {
    "zimlet"
})
public class testGetZimletRequest
    extends testAttributeSelectorImpl
{

    @XmlElement(required = true)
    protected testNamedElement zimlet;

    /**
     * Gets the value of the zimlet property.
     * 
     * @return
     *     possible object is
     *     {@link testNamedElement }
     *     
     */
    public testNamedElement getZimlet() {
        return zimlet;
    }

    /**
     * Sets the value of the zimlet property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNamedElement }
     *     
     */
    public void setZimlet(testNamedElement value) {
        this.zimlet = value;
    }

}
