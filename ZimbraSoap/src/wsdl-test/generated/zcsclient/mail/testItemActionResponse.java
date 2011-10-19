
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for itemActionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="itemActionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{urn:zimbraMail}idAndOperation"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemActionResponse", propOrder = {
    "action"
})
public class testItemActionResponse {

    @XmlElement(required = true)
    protected testIdAndOperation action;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link testIdAndOperation }
     *     
     */
    public testIdAndOperation getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link testIdAndOperation }
     *     
     */
    public void setAction(testIdAndOperation value) {
        this.action = value;
    }

}
