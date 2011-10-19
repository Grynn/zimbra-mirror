
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getCreateObjectAttrsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCreateObjectAttrsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setAttrs" type="{urn:zimbraAdmin}effectiveAttrsInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCreateObjectAttrsResponse", propOrder = {
    "setAttrs"
})
public class testGetCreateObjectAttrsResponse {

    @XmlElement(required = true)
    protected testEffectiveAttrsInfo setAttrs;

    /**
     * Gets the value of the setAttrs property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveAttrsInfo }
     *     
     */
    public testEffectiveAttrsInfo getSetAttrs() {
        return setAttrs;
    }

    /**
     * Sets the value of the setAttrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveAttrsInfo }
     *     
     */
    public void setSetAttrs(testEffectiveAttrsInfo value) {
        this.setAttrs = value;
    }

}
