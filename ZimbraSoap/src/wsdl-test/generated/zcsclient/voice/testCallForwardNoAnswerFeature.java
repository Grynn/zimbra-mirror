
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for callForwardNoAnswerFeature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="callForwardNoAnswerFeature">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}callFeatureInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ft" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nr" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "callForwardNoAnswerFeature")
public class testCallForwardNoAnswerFeature
    extends testCallFeatureInfo
{

    @XmlAttribute(name = "ft")
    protected String ft;
    @XmlAttribute(name = "nr")
    protected String nr;

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

    /**
     * Gets the value of the nr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNr() {
        return nr;
    }

    /**
     * Sets the value of the nr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNr(String value) {
        this.nr = value;
    }

}
