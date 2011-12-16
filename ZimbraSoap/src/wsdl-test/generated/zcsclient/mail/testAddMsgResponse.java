
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addMsgResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addMsgResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="chat" type="{urn:zimbraMail}chatSummary"/>
 *           &lt;element name="m" type="{urn:zimbraMail}messageSummary"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addMsgResponse", propOrder = {
    "chat",
    "m"
})
public class testAddMsgResponse {

    protected testChatSummary chat;
    protected testMessageSummary m;

    /**
     * Gets the value of the chat property.
     * 
     * @return
     *     possible object is
     *     {@link testChatSummary }
     *     
     */
    public testChatSummary getChat() {
        return chat;
    }

    /**
     * Sets the value of the chat property.
     * 
     * @param value
     *     allowed object is
     *     {@link testChatSummary }
     *     
     */
    public void setChat(testChatSummary value) {
        this.chat = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testMessageSummary }
     *     
     */
    public testMessageSummary getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMessageSummary }
     *     
     */
    public void setM(testMessageSummary value) {
        this.m = value;
    }

}
