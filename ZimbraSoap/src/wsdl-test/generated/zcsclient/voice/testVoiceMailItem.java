
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceMailItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceMailItem">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}voiceCallItem">
 *       &lt;sequence>
 *         &lt;element name="cp" type="{urn:zimbraVoice}voiceMailCallParty" minOccurs="0"/>
 *         &lt;element name="content" type="{urn:zimbraVoice}voiceMailContent" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="f" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceMailItem", propOrder = {
    "cp",
    "content"
})
public class testVoiceMailItem
    extends testVoiceCallItem
{

    protected testVoiceMailCallParty cp;
    protected testVoiceMailContent content;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "f")
    protected String f;

    /**
     * Gets the value of the cp property.
     * 
     * @return
     *     possible object is
     *     {@link testVoiceMailCallParty }
     *     
     */
    public testVoiceMailCallParty getCp() {
        return cp;
    }

    /**
     * Sets the value of the cp property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVoiceMailCallParty }
     *     
     */
    public void setCp(testVoiceMailCallParty value) {
        this.cp = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link testVoiceMailContent }
     *     
     */
    public testVoiceMailContent getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVoiceMailContent }
     *     
     */
    public void setContent(testVoiceMailContent value) {
        this.content = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the f property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF() {
        return f;
    }

    /**
     * Sets the value of the f property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF(String value) {
        this.f = value;
    }

}
