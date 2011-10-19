
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for removeAttachmentsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeAttachmentsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="m" type="{urn:zimbraMail}msgPartIds"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeAttachmentsRequest", propOrder = {
    "m"
})
public class testRemoveAttachmentsRequest {

    @XmlElement(required = true)
    protected testMsgPartIds m;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testMsgPartIds }
     *     
     */
    public testMsgPartIds getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMsgPartIds }
     *     
     */
    public void setM(testMsgPartIds value) {
        this.m = value;
    }

}
