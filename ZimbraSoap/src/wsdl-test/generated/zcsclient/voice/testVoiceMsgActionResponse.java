
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceMsgActionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceMsgActionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{urn:zimbraVoice}voiceMsgActionInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceMsgActionResponse", propOrder = {
    "action"
})
public class testVoiceMsgActionResponse {

    @XmlElement(required = true)
    protected testVoiceMsgActionInfo action;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link testVoiceMsgActionInfo }
     *     
     */
    public testVoiceMsgActionInfo getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVoiceMsgActionInfo }
     *     
     */
    public void setAction(testVoiceMsgActionInfo value) {
        this.action = value;
    }

}
