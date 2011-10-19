
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tagActionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tagActionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{urn:zimbraMail}tagActionSelector"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tagActionRequest", propOrder = {
    "action"
})
public class testTagActionRequest {

    @XmlElement(required = true)
    protected testTagActionSelector action;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link testTagActionSelector }
     *     
     */
    public testTagActionSelector getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTagActionSelector }
     *     
     */
    public void setAction(testTagActionSelector value) {
        this.action = value;
    }

}
