
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkRightResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkRightResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="via" type="{urn:zimbraAdmin}rightViaInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="allow" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkRightResponse", propOrder = {
    "via"
})
public class testCheckRightResponse {

    protected testRightViaInfo via;
    @XmlAttribute(name = "allow", required = true)
    protected boolean allow;

    /**
     * Gets the value of the via property.
     * 
     * @return
     *     possible object is
     *     {@link testRightViaInfo }
     *     
     */
    public testRightViaInfo getVia() {
        return via;
    }

    /**
     * Sets the value of the via property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRightViaInfo }
     *     
     */
    public void setVia(testRightViaInfo value) {
        this.via = value;
    }

    /**
     * Gets the value of the allow property.
     * 
     */
    public boolean isAllow() {
        return allow;
    }

    /**
     * Sets the value of the allow property.
     * 
     */
    public void setAllow(boolean value) {
        this.allow = value;
    }

}
