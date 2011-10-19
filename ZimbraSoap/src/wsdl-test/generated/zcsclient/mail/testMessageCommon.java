
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for messageCommon complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageCommon">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraMail}meta" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="f" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="t" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tn" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rev" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="md" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageCommon", propOrder = {
    "meta"
})
@XmlSeeAlso({
    testMsgWithGroupInfo.class,
    testMessageSummary.class,
    testMessageInfo.class,
    testInviteAsMP.class
})
public class testMessageCommon {

    protected List<testMailCustomMetadata> meta;
    @XmlAttribute(name = "s")
    protected Long s;
    @XmlAttribute(name = "d")
    protected Long d;
    @XmlAttribute(name = "l")
    protected String l;
    @XmlAttribute(name = "cid")
    protected String cid;
    @XmlAttribute(name = "f")
    protected String f;
    @XmlAttribute(name = "t")
    protected String t;
    @XmlAttribute(name = "tn")
    protected String tn;
    @XmlAttribute(name = "rev")
    protected Integer rev;
    @XmlAttribute(name = "md")
    protected Long md;
    @XmlAttribute(name = "ms")
    protected Integer ms;

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
     * {@link testMailCustomMetadata }
     * 
     * 
     */
    public List<testMailCustomMetadata> getMeta() {
        if (meta == null) {
            meta = new ArrayList<testMailCustomMetadata>();
        }
        return this.meta;
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
     * Gets the value of the tn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTn() {
        return tn;
    }

    /**
     * Sets the value of the tn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTn(String value) {
        this.tn = value;
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

}
