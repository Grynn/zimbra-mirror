
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for selectiveCallForwardFeature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="selectiveCallForwardFeature">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}featureWithCallerList">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ft" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "selectiveCallForwardFeature")
public class testSelectiveCallForwardFeature
    extends testFeatureWithCallerList
{

    @XmlAttribute(name = "ft")
    protected String ft;

    /**
     * Gets the value of the ft property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFt() {
        return ft;
    }

    /**
     * Sets the value of the ft property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFt(String value) {
        this.ft = value;
    }

}
