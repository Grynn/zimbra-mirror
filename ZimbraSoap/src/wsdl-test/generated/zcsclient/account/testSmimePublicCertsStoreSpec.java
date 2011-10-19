
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import generated.zcsclient.zm.testSourceLookupOpt;
import generated.zcsclient.zm.testStoreLookupOpt;


/**
 * <p>Java class for smimePublicCertsStoreSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="smimePublicCertsStoreSpec">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:zimbra>sourceLookupOpt">
 *       &lt;attribute name="storeLookupOpt" type="{urn:zimbra}storeLookupOpt" />
 *       &lt;attribute name="sourceLookupOpt" type="{urn:zimbra}sourceLookupOpt" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "smimePublicCertsStoreSpec", propOrder = {
    "value"
})
public class testSmimePublicCertsStoreSpec {

    @XmlValue
    protected testSourceLookupOpt value;
    @XmlAttribute(name = "storeLookupOpt")
    protected testStoreLookupOpt storeLookupOpt;
    @XmlAttribute(name = "sourceLookupOpt")
    protected testSourceLookupOpt sourceLookupOpt;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link testSourceLookupOpt }
     *     
     */
    public testSourceLookupOpt getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSourceLookupOpt }
     *     
     */
    public void setValue(testSourceLookupOpt value) {
        this.value = value;
    }

    /**
     * Gets the value of the storeLookupOpt property.
     * 
     * @return
     *     possible object is
     *     {@link testStoreLookupOpt }
     *     
     */
    public testStoreLookupOpt getStoreLookupOpt() {
        return storeLookupOpt;
    }

    /**
     * Sets the value of the storeLookupOpt property.
     * 
     * @param value
     *     allowed object is
     *     {@link testStoreLookupOpt }
     *     
     */
    public void setStoreLookupOpt(testStoreLookupOpt value) {
        this.storeLookupOpt = value;
    }

    /**
     * Gets the value of the sourceLookupOpt property.
     * 
     * @return
     *     possible object is
     *     {@link testSourceLookupOpt }
     *     
     */
    public testSourceLookupOpt getSourceLookupOpt() {
        return sourceLookupOpt;
    }

    /**
     * Sets the value of the sourceLookupOpt property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSourceLookupOpt }
     *     
     */
    public void setSourceLookupOpt(testSourceLookupOpt value) {
        this.sourceLookupOpt = value;
    }

}
