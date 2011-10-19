
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tagActionSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tagActionSelector">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}actionSelector">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraMail}retentionPolicy" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tagActionSelector", propOrder = {
    "retentionPolicy"
})
public class testTagActionSelector
    extends testActionSelector
{

    protected testRetentionPolicy retentionPolicy;

    /**
     * Gets the value of the retentionPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link testRetentionPolicy }
     *     
     */
    public testRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    /**
     * Sets the value of the retentionPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRetentionPolicy }
     *     
     */
    public void setRetentionPolicy(testRetentionPolicy value) {
        this.retentionPolicy = value;
    }

}
