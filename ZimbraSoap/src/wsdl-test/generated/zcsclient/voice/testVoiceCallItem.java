
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceCallItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceCallItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="phone" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sf" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="du" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="d" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceCallItem")
@XmlSeeAlso({
    testCallLogItem.class,
    testVoiceMailItem.class
})
public class testVoiceCallItem {

    @XmlAttribute(name = "phone", required = true)
    protected String phone;
    @XmlAttribute(name = "l", required = true)
    protected String l;
    @XmlAttribute(name = "sf", required = true)
    protected String sf;
    @XmlAttribute(name = "du", required = true)
    protected int du;
    @XmlAttribute(name = "d", required = true)
    protected long d;

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setL(String value) {
        this.l = value;
    }

    /**
     * Gets the value of the sf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSf() {
        return sf;
    }

    /**
     * Sets the value of the sf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSf(String value) {
        this.sf = value;
    }

    /**
     * Gets the value of the du property.
     * 
     */
    public int getDu() {
        return du;
    }

    /**
     * Sets the value of the du property.
     * 
     */
    public void setDu(int value) {
        this.du = value;
    }

    /**
     * Gets the value of the d property.
     * 
     */
    public long getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     */
    public void setD(long value) {
        this.d = value;
    }

}
