
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for noteActionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="noteActionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{urn:zimbraMail}noteActionSelector"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "noteActionRequest", propOrder = {
    "action"
})
public class testNoteActionRequest {

    @XmlElement(required = true)
    protected testNoteActionSelector action;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link testNoteActionSelector }
     *     
     */
    public testNoteActionSelector getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNoteActionSelector }
     *     
     */
    public void setAction(testNoteActionSelector value) {
        this.action = value;
    }

}
