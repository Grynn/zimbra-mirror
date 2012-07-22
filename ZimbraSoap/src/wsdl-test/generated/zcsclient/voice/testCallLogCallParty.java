
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for callLogCallParty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="callLogCallParty">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}voiceMailCallParty">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ci" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="st" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="co" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "callLogCallParty")
public class testCallLogCallParty
    extends testVoiceMailCallParty
{

    @XmlAttribute(name = "ci", required = true)
    protected String ci;
    @XmlAttribute(name = "st", required = true)
    protected String st;
    @XmlAttribute(name = "co", required = true)
    protected String co;

    /**
     * Gets the value of the ci property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCi() {
        return ci;
    }

    /**
     * Sets the value of the ci property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCi(String value) {
        this.ci = value;
    }

    /**
     * Gets the value of the st property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSt() {
        return st;
    }

    /**
     * Sets the value of the st property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSt(String value) {
        this.st = value;
    }

    /**
     * Gets the value of the co property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCo() {
        return co;
    }

    /**
     * Sets the value of the co property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCo(String value) {
        this.co = value;
    }

}
