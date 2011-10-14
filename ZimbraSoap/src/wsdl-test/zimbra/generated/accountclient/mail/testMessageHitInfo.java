
package zimbra.generated.accountclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for messageHitInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageHitInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}messageInfo">
 *       &lt;sequence>
 *         &lt;element name="hp" type="{urn:zimbraMail}part" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sf" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cm" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageHitInfo", propOrder = {
    "hp"
})
@XmlSeeAlso({
    testChatHitInfo.class
})
public class testMessageHitInfo
    extends testMessageInfo
{

    protected List<testPart> hp;
    @XmlAttribute(name = "sf")
    protected String sf;
    @XmlAttribute(name = "cm")
    protected Boolean cm;

    /**
     * Gets the value of the hp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testPart }
     * 
     * 
     */
    public List<testPart> getHp() {
        if (hp == null) {
            hp = new ArrayList<testPart>();
        }
        return this.hp;
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
     * Gets the value of the cm property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCm() {
        return cm;
    }

    /**
     * Sets the value of the cm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCm(Boolean value) {
        this.cm = value;
    }

}
