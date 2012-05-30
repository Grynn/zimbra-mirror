
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testKeyValuePair;


/**
 * <p>Java class for inviteAsMP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inviteAsMP">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}messageCommon">
 *       &lt;sequence>
 *         &lt;element name="e" type="{urn:zimbraMail}emailInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="su" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inv" type="{urn:zimbraMail}mpInviteInfo" minOccurs="0"/>
 *         &lt;element name="header" type="{urn:zimbra}keyValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="mp" type="{urn:zimbraMail}partInfo"/>
 *           &lt;element name="shr" type="{urn:zimbraMail}shareNotification"/>
 *           &lt;element name="dlSubs" type="{urn:zimbraMail}dlSubscriptionNotification"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="part" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sd" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inviteAsMP", propOrder = {
    "e",
    "su",
    "mid",
    "inv",
    "header",
    "mpOrShrOrDlSubs"
})
public class testInviteAsMP
    extends testMessageCommon
{

    protected List<testEmailInfo> e;
    protected String su;
    protected String mid;
    protected testMpInviteInfo inv;
    protected List<testKeyValuePair> header;
    @XmlElements({
        @XmlElement(name = "mp", type = testPartInfo.class),
        @XmlElement(name = "dlSubs", type = testDlSubscriptionNotification.class),
        @XmlElement(name = "shr", type = testShareNotification.class)
    })
    protected List<Object> mpOrShrOrDlSubs;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "part")
    protected String part;
    @XmlAttribute(name = "sd")
    protected Long sd;

    /**
     * Gets the value of the e property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the e property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testEmailInfo }
     * 
     * 
     */
    public List<testEmailInfo> getE() {
        if (e == null) {
            e = new ArrayList<testEmailInfo>();
        }
        return this.e;
    }

    /**
     * Gets the value of the su property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSu() {
        return su;
    }

    /**
     * Sets the value of the su property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSu(String value) {
        this.su = value;
    }

    /**
     * Gets the value of the mid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMid() {
        return mid;
    }

    /**
     * Sets the value of the mid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMid(String value) {
        this.mid = value;
    }

    /**
     * Gets the value of the inv property.
     * 
     * @return
     *     possible object is
     *     {@link testMpInviteInfo }
     *     
     */
    public testMpInviteInfo getInv() {
        return inv;
    }

    /**
     * Sets the value of the inv property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMpInviteInfo }
     *     
     */
    public void setInv(testMpInviteInfo value) {
        this.inv = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the header property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeader().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testKeyValuePair }
     * 
     * 
     */
    public List<testKeyValuePair> getHeader() {
        if (header == null) {
            header = new ArrayList<testKeyValuePair>();
        }
        return this.header;
    }

    /**
     * Gets the value of the mpOrShrOrDlSubs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mpOrShrOrDlSubs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMpOrShrOrDlSubs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testPartInfo }
     * {@link testDlSubscriptionNotification }
     * {@link testShareNotification }
     * 
     * 
     */
    public List<Object> getMpOrShrOrDlSubs() {
        if (mpOrShrOrDlSubs == null) {
            mpOrShrOrDlSubs = new ArrayList<Object>();
        }
        return this.mpOrShrOrDlSubs;
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
     * Gets the value of the part property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPart() {
        return part;
    }

    /**
     * Sets the value of the part property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPart(String value) {
        this.part = value;
    }

    /**
     * Gets the value of the sd property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSd() {
        return sd;
    }

    /**
     * Sets the value of the sd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSd(Long value) {
        this.sd = value;
    }

}
