
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testNamedElement;


/**
 * <p>Java class for mailQueueFlushRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailQueueFlushRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="server" type="{urn:zimbra}namedElement"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailQueueFlushRequest", propOrder = {
    "server"
})
public class testMailQueueFlushRequest {

    @XmlElement(required = true)
    protected testNamedElement server;

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link testNamedElement }
     *     
     */
    public testNamedElement getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNamedElement }
     *     
     */
    public void setServer(testNamedElement value) {
        this.server = value;
    }

}
