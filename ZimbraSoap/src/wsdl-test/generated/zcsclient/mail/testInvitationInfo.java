
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for invitationInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invitationInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}inviteComponent">
 *       &lt;sequence>
 *         &lt;element name="content" type="{urn:zimbraMail}rawInvite" minOccurs="0"/>
 *         &lt;element name="comp" type="{urn:zimbraMail}inviteComponent" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mp" type="{urn:zimbraMail}mimePartInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attach" type="{urn:zimbraMail}attachmentsInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ct" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ci" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invitationInfo", propOrder = {
    "content",
    "comp",
    "tz",
    "mp",
    "attach"
})
public class testInvitationInfo
    extends testInviteComponent
{

    protected testRawInvite content;
    protected testInviteComponent comp;
    protected List<testCalTZInfo> tz;
    protected List<testMimePartInfo> mp;
    protected testAttachmentsInfo attach;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "ct")
    protected String ct;
    @XmlAttribute(name = "ci")
    protected String ci;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link testRawInvite }
     *     
     */
    public testRawInvite getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRawInvite }
     *     
     */
    public void setContent(testRawInvite value) {
        this.content = value;
    }

    /**
     * Gets the value of the comp property.
     * 
     * @return
     *     possible object is
     *     {@link testInviteComponent }
     *     
     */
    public testInviteComponent getComp() {
        return comp;
    }

    /**
     * Sets the value of the comp property.
     * 
     * @param value
     *     allowed object is
     *     {@link testInviteComponent }
     *     
     */
    public void setComp(testInviteComponent value) {
        this.comp = value;
    }

    /**
     * Gets the value of the tz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTz().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCalTZInfo }
     * 
     * 
     */
    public List<testCalTZInfo> getTz() {
        if (tz == null) {
            tz = new ArrayList<testCalTZInfo>();
        }
        return this.tz;
    }

    /**
     * Gets the value of the mp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testMimePartInfo }
     * 
     * 
     */
    public List<testMimePartInfo> getMp() {
        if (mp == null) {
            mp = new ArrayList<testMimePartInfo>();
        }
        return this.mp;
    }

    /**
     * Gets the value of the attach property.
     * 
     * @return
     *     possible object is
     *     {@link testAttachmentsInfo }
     *     
     */
    public testAttachmentsInfo getAttach() {
        return attach;
    }

    /**
     * Sets the value of the attach property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAttachmentsInfo }
     *     
     */
    public void setAttach(testAttachmentsInfo value) {
        this.attach = value;
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
     * Gets the value of the ct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCt() {
        return ct;
    }

    /**
     * Sets the value of the ct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCt(String value) {
        this.ct = value;
    }

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

}
