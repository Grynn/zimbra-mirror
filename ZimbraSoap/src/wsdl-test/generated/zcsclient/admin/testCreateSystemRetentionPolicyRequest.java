
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createSystemRetentionPolicyRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createSystemRetentionPolicyRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keep" type="{urn:zimbraAdmin}policyHolder" minOccurs="0"/>
 *         &lt;element name="purge" type="{urn:zimbraAdmin}policyHolder" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createSystemRetentionPolicyRequest", propOrder = {
    "keep",
    "purge"
})
public class testCreateSystemRetentionPolicyRequest {

    protected testPolicyHolder keep;
    protected testPolicyHolder purge;

    /**
     * Gets the value of the keep property.
     * 
     * @return
     *     possible object is
     *     {@link testPolicyHolder }
     *     
     */
    public testPolicyHolder getKeep() {
        return keep;
    }

    /**
     * Sets the value of the keep property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPolicyHolder }
     *     
     */
    public void setKeep(testPolicyHolder value) {
        this.keep = value;
    }

    /**
     * Gets the value of the purge property.
     * 
     * @return
     *     possible object is
     *     {@link testPolicyHolder }
     *     
     */
    public testPolicyHolder getPurge() {
        return purge;
    }

    /**
     * Sets the value of the purge property.
     * 
     * @param value
     *     allowed object is
     *     {@link testPolicyHolder }
     *     
     */
    public void setPurge(testPolicyHolder value) {
        this.purge = value;
    }

}
