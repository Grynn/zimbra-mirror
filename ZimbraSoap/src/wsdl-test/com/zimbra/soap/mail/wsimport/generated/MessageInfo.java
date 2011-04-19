
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for messageInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="meta" type="{urn:zimbra}customMetadata" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="fr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="e" type="{urn:zimbraMail}emailInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="su" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="irt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inv" type="{urn:zimbraMail}inviteInfo" minOccurs="0"/>
 *         &lt;element name="header" type="{urn:zimbra}keyValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="mp" type="{urn:zimbraMail}partInfo"/>
 *           &lt;element name="shr" type="{urn:zimbraMail}shareNotification"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="f" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="t" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rev" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="md" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="cif" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="origid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="idnt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="forAcct" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="autoSendTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="sd" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="rd" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="part" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageInfo", propOrder = {
    "meta",
    "fr",
    "e",
    "su",
    "mid",
    "irt",
    "inv",
    "header",
    "mpOrShr"
})
@XmlSeeAlso({
    ChatMessageInfo.class
})
public class MessageInfo {

    protected List<CustomMetadata> meta;
    protected String fr;
    protected List<EmailInfo> e;
    protected String su;
    protected String mid;
    protected String irt;
    protected InviteInfo inv;
    protected List<KeyValuePair> header;
    @XmlElements({
        @XmlElement(name = "shr", type = ShareNotification.class),
        @XmlElement(name = "mp", type = PartInfo.class)
    })
    protected List<Object> mpOrShr;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute
    protected Long d;
    @XmlAttribute
    protected Long s;
    @XmlAttribute
    protected String l;
    @XmlAttribute
    protected String cid;
    @XmlAttribute
    protected String f;
    @XmlAttribute
    protected String t;
    @XmlAttribute
    protected Integer rev;
    @XmlAttribute
    protected Long md;
    @XmlAttribute
    protected Integer ms;
    @XmlAttribute
    protected String cif;
    @XmlAttribute
    protected String origid;
    @XmlAttribute
    protected String rt;
    @XmlAttribute
    protected String idnt;
    @XmlAttribute
    protected String forAcct;
    @XmlAttribute
    protected Long autoSendTime;
    @XmlAttribute
    protected Long sd;
    @XmlAttribute
    protected Long rd;
    @XmlAttribute
    protected String part;

    /**
     * Gets the value of the meta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the meta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMeta().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomMetadata }
     * 
     * 
     */
    public List<CustomMetadata> getMeta() {
        if (meta == null) {
            meta = new ArrayList<CustomMetadata>();
        }
        return this.meta;
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
     * {@link EmailInfo }
     * 
     * 
     */
    public List<EmailInfo> getE() {
        if (e == null) {
            e = new ArrayList<EmailInfo>();
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
     * Gets the value of the inv property.
     * 
     * @return
     *     possible object is
     *     {@link InviteInfo }
     *     
     */
    public InviteInfo getInv() {
        return inv;
    }

    /**
     * Sets the value of the inv property.
     * 
     * @param value
     *     allowed object is
     *     {@link InviteInfo }
     *     
     */
    public void setInv(InviteInfo value) {
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
     * {@link KeyValuePair }
     * 
     * 
     */
    public List<KeyValuePair> getHeader() {
        if (header == null) {
            header = new ArrayList<KeyValuePair>();
        }
        return this.header;
    }

    /**
     * Gets the value of the mpOrShr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mpOrShr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMpOrShr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShareNotification }
     * {@link PartInfo }
     * 
     * 
     */
    public List<Object> getMpOrShr() {
        if (mpOrShr == null) {
            mpOrShr = new ArrayList<Object>();
        }
        return this.mpOrShr;
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
     * Gets the value of the d property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setD(Long value) {
        this.d = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setS(Long value) {
        this.s = value;
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
     * Gets the value of the cid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCid() {
        return cid;
    }

    /**
     * Sets the value of the cid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCid(String value) {
        this.cid = value;
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

    /**
     * Gets the value of the t property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getT() {
        return t;
    }

    /**
     * Sets the value of the t property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setT(String value) {
        this.t = value;
    }

    /**
     * Gets the value of the rev property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRev() {
        return rev;
    }

    /**
     * Sets the value of the rev property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRev(Integer value) {
        this.rev = value;
    }

    /**
     * Gets the value of the md property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMd() {
        return md;
    }

    /**
     * Sets the value of the md property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMd(Long value) {
        this.md = value;
    }

    /**
     * Gets the value of the ms property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMs() {
        return ms;
    }

    /**
     * Sets the value of the ms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMs(Integer value) {
        this.ms = value;
    }

    /**
     * Gets the value of the cif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCif() {
        return cif;
    }

    /**
     * Sets the value of the cif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCif(String value) {
        this.cif = value;
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
     * Gets the value of the forAcct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForAcct() {
        return forAcct;
    }

    /**
     * Sets the value of the forAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForAcct(String value) {
        this.forAcct = value;
    }

    /**
     * Gets the value of the autoSendTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAutoSendTime() {
        return autoSendTime;
    }

    /**
     * Sets the value of the autoSendTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAutoSendTime(Long value) {
        this.autoSendTime = value;
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

    /**
     * Gets the value of the rd property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRd() {
        return rd;
    }

    /**
     * Sets the value of the rd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRd(Long value) {
        this.rd = value;
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

}
