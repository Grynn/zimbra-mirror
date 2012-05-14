
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createMountpointRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createMountpointRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="link" type="{urn:zimbraMail}newMountpointSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createMountpointRequest", propOrder = {
    "link"
})
public class testCreateMountpointRequest {

    @XmlElement(required = true)
    protected testNewMountpointSpec link;

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link testNewMountpointSpec }
     *     
     */
    public testNewMountpointSpec getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNewMountpointSpec }
     *     
     */
    public void setLink(testNewMountpointSpec value) {
        this.link = value;
    }

}
