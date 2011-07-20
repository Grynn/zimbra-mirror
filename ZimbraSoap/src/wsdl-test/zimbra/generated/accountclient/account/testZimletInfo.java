
package zimbra.generated.accountclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for zimletInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="zimletInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zimletContext" type="{urn:zimbraAccount}zimletContext"/>
 *         &lt;element name="zimlet" type="{urn:zimbraAccount}zimletDesc" minOccurs="0"/>
 *         &lt;element name="zimletConfig" type="{urn:zimbraAccount}zimletConfigInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "zimletInfo", propOrder = {
    "zimletContext",
    "zimlet",
    "zimletConfig"
})
public class testZimletInfo {

    @XmlElement(required = true)
    protected testZimletContext zimletContext;
    protected testZimletDesc zimlet;
    protected testZimletConfigInfo zimletConfig;

    /**
     * Gets the value of the zimletContext property.
     * 
     * @return
     *     possible object is
     *     {@link testZimletContext }
     *     
     */
    public testZimletContext getZimletContext() {
        return zimletContext;
    }

    /**
     * Sets the value of the zimletContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link testZimletContext }
     *     
     */
    public void setZimletContext(testZimletContext value) {
        this.zimletContext = value;
    }

    /**
     * Gets the value of the zimlet property.
     * 
     * @return
     *     possible object is
     *     {@link testZimletDesc }
     *     
     */
    public testZimletDesc getZimlet() {
        return zimlet;
    }

    /**
     * Sets the value of the zimlet property.
     * 
     * @param value
     *     allowed object is
     *     {@link testZimletDesc }
     *     
     */
    public void setZimlet(testZimletDesc value) {
        this.zimlet = value;
    }

    /**
     * Gets the value of the zimletConfig property.
     * 
     * @return
     *     possible object is
     *     {@link testZimletConfigInfo }
     *     
     */
    public testZimletConfigInfo getZimletConfig() {
        return zimletConfig;
    }

    /**
     * Sets the value of the zimletConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link testZimletConfigInfo }
     *     
     */
    public void setZimletConfig(testZimletConfigInfo value) {
        this.zimletConfig = value;
    }

}
