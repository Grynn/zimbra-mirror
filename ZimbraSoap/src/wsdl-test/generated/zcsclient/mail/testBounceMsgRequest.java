
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bounceMsgRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bounceMsgRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="m" type="{urn:zimbraMail}bounceMsgSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bounceMsgRequest", propOrder = {
    "m"
})
public class testBounceMsgRequest {

    @XmlElement(required = true)
    protected testBounceMsgSpec m;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testBounceMsgSpec }
     *     
     */
    public testBounceMsgSpec getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testBounceMsgSpec }
     *     
     */
    public void setM(testBounceMsgSpec value) {
        this.m = value;
    }

}
