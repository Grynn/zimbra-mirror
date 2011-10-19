
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for calendarItemMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calendarItemMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="header" type="{urn:zimbraMail}header" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mp" type="{urn:zimbraMail}mimePartInfo" minOccurs="0"/>
 *         &lt;element name="attach" type="{urn:zimbraMail}attachmentsInfo" minOccurs="0"/>
 *         &lt;element name="inv" type="{urn:zimbraMail}invitationInfo" minOccurs="0"/>
 *         &lt;element name="e" type="{urn:zimbraMail}emailAddrInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="fr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;any processContents='skip' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="aid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="origid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="idnt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="su" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="irt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="f" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calendarItemMsg", propOrder = {
    "content",
    "header",
    "mp",
    "attach",
    "inv",
    "e",
    "tz",
    "fr",
    "any"
})
@XmlSeeAlso({
    testMsgToSend.class,
    testSaveDraftMsg.class
})
public class testCalendarItemMsg {

    protected String content;
    protected List<testHeader> header;
    protected testMimePartInfo mp;
    protected testAttachmentsInfo attach;
    protected testInvitationInfo inv;
    protected List<testEmailAddrInfo> e;
    protected List<testCalTZInfo> tz;
    protected String fr;
    @XmlAnyElement
    protected List<Element> any;
    @XmlAttribute(name = "aid")
    protected String aid;
    @XmlAttribute(name = "origid")
    protected String origid;
    @XmlAttribute(name = "rt")
    protected String rt;
    @XmlAttribute(name = "idnt")
    protected String idnt;
    @XmlAttribute(name = "su")
    protected String su;
    @XmlAttribute(name = "irt")
    protected String irt;
    @XmlAttribute(name = "l")
    protected String l;
    @XmlAttribute(name = "f")
    protected String f;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
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
     * {@link testHeader }
     * 
     * 
     */
    public List<testHeader> getHeader() {
        if (header == null) {
            header = new ArrayList<testHeader>();
        }
        return this.header;
    }

    /**
     * Gets the value of the mp property.
     * 
     * @return
     *     possible object is
     *     {@link testMimePartInfo }
     *     
     */
    public testMimePartInfo getMp() {
        return mp;
    }

    /**
     * Sets the value of the mp property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMimePartInfo }
     *     
     */
    public void setMp(testMimePartInfo value) {
        this.mp = value;
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
     * Gets the value of the inv property.
     * 
     * @return
     *     possible object is
     *     {@link testInvitationInfo }
     *     
     */
    public testInvitationInfo getInv() {
        return inv;
    }

    /**
     * Sets the value of the inv property.
     * 
     * @param value
     *     allowed object is
     *     {@link testInvitationInfo }
     *     
     */
    public void setInv(testInvitationInfo value) {
        this.inv = value;
    }

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
     * {@link testEmailAddrInfo }
     * 
     * 
     */
    public List<testEmailAddrInfo> getE() {
        if (e == null) {
            e = new ArrayList<testEmailAddrInfo>();
        }
        return this.e;
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
     * Gets the value of the fr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFr() {
        return fr;
    }

    /**
     * Sets the value of the fr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFr(String value) {
        this.fr = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * 
     * 
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
        }
        return this.any;
    }

    /**
     * Gets the value of the aid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAid() {
        return aid;
    }

    /**
     * Sets the value of the aid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAid(String value) {
        this.aid = value;
    }

    /**
     * Gets the value of the origid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigid() {
        return origid;
    }

    /**
     * Sets the value of the origid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigid(String value) {
        this.origid = value;
    }

    /**
     * Gets the value of the rt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRt() {
        return rt;
    }

    /**
     * Sets the value of the rt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRt(String value) {
        this.rt = value;
    }

    /**
     * Gets the value of the idnt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdnt() {
        return idnt;
    }

    /**
     * Sets the value of the idnt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdnt(String value) {
        this.idnt = value;
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
     * Gets the value of the irt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIrt() {
        return irt;
    }

    /**
     * Sets the value of the irt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIrt(String value) {
        this.irt = value;
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
