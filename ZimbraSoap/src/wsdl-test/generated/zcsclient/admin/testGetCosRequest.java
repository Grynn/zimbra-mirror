
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAttributeSelectorImpl;


/**
 * <p>Java class for getCosRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCosRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="cos" type="{urn:zimbraAdmin}cosSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCosRequest", propOrder = {
    "cos"
})
public class testGetCosRequest
    extends testAttributeSelectorImpl
{

    protected testCosSelector cos;

    /**
     * Gets the value of the cos property.
     * 
     * @return
     *     possible object is
     *     {@link testCosSelector }
     *     
     */
    public testCosSelector getCos() {
        return cos;
    }

    /**
     * Sets the value of the cos property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCosSelector }
     *     
     */
    public void setCos(testCosSelector value) {
        this.cos = value;
    }

}
