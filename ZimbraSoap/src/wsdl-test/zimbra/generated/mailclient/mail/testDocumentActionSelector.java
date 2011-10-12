
package zimbra.generated.mailclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentActionSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentActionSelector">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}actionSelector">
 *       &lt;sequence>
 *         &lt;element name="grant" type="{urn:zimbraMail}documentActionGrant" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="zid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentActionSelector", propOrder = {
    "grant"
})
public class testDocumentActionSelector
    extends testActionSelector
{

    protected testDocumentActionGrant grant;
    @XmlAttribute(name = "zid")
    protected String zid;

    /**
     * Gets the value of the grant property.
     * 
     * @return
     *     possible object is
     *     {@link testDocumentActionGrant }
     *     
     */
    public testDocumentActionGrant getGrant() {
        return grant;
    }

    /**
     * Sets the value of the grant property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDocumentActionGrant }
     *     
     */
    public void setGrant(testDocumentActionGrant value) {
        this.grant = value;
    }

    /**
     * Gets the value of the zid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZid() {
        return zid;
    }

    /**
     * Sets the value of the zid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZid(String value) {
        this.zid = value;
    }

}
