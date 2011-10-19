
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMsgMetadataRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMsgMetadataRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="m" type="{urn:zimbraMail}idsAttr"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMsgMetadataRequest", propOrder = {
    "m"
})
public class testGetMsgMetadataRequest {

    @XmlElement(required = true)
    protected testIdsAttr m;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testIdsAttr }
     *     
     */
    public testIdsAttr getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testIdsAttr }
     *     
     */
    public void setM(testIdsAttr value) {
        this.m = value;
    }

}
