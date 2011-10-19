
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testSectionAttr;


/**
 * <p>Java class for getMailboxMetadataRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMailboxMetadataRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="meta" type="{urn:zimbra}sectionAttr" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMailboxMetadataRequest", propOrder = {
    "meta"
})
public class testGetMailboxMetadataRequest {

    protected testSectionAttr meta;

    /**
     * Gets the value of the meta property.
     * 
     * @return
     *     possible object is
     *     {@link testSectionAttr }
     *     
     */
    public testSectionAttr getMeta() {
        return meta;
    }

    /**
     * Sets the value of the meta property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSectionAttr }
     *     
     */
    public void setMeta(testSectionAttr value) {
        this.meta = value;
    }

}
