
package zimbra.generated.accountclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calendarAttendee complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calendarAttendee">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xparam" type="{urn:zimbraMail}xParam" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="a" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sentBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cutype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="role" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ptst" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rsvp" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="member" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="delTo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="delFrom" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calendarAttendee", propOrder = {
    "xparam"
})
@XmlSeeAlso({
    testCalendarAttendeeWithGroupInfo.class
})
public class testCalendarAttendee {

    protected List<testXParam> xparam;
    @XmlAttribute(name = "a")
    protected String a;
    @XmlAttribute(name = "url")
    protected String url;
    @XmlAttribute(name = "d")
    protected String d;
    @XmlAttribute(name = "sentBy")
    protected String sentBy;
    @XmlAttribute(name = "dir")
    protected String dir;
    @XmlAttribute(name = "lang")
    protected String lang;
    @XmlAttribute(name = "cutype")
    protected String cutype;
    @XmlAttribute(name = "role")
    protected String role;
    @XmlAttribute(name = "ptst")
    protected String ptst;
    @XmlAttribute(name = "rsvp")
    protected Boolean rsvp;
    @XmlAttribute(name = "member")
    protected String member;
    @XmlAttribute(name = "delTo")
    protected String delTo;
    @XmlAttribute(name = "delFrom")
    protected String delFrom;

    /**
     * Gets the value of the xparam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xparam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXparam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testXParam }
     * 
     * 
     */
    public List<testXParam> getXparam() {
        if (xparam == null) {
            xparam = new ArrayList<testXParam>();
        }
        return this.xparam;
    }

    /**
     * Gets the value of the a property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getA() {
        return a;
    }

    /**
     * Sets the value of the a property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setA(String value) {
        this.a = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the d property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setD(String value) {
        this.d = value;
    }

    /**
     * Gets the value of the sentBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSentBy() {
        return sentBy;
    }

    /**
     * Sets the value of the sentBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSentBy(String value) {
        this.sentBy = value;
    }

    /**
     * Gets the value of the dir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDir() {
        return dir;
    }

    /**
     * Sets the value of the dir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDir(String value) {
        this.dir = value;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the cutype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCutype() {
        return cutype;
    }

    /**
     * Sets the value of the cutype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCutype(String value) {
        this.cutype = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the ptst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPtst() {
        return ptst;
    }

    /**
     * Sets the value of the ptst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPtst(String value) {
        this.ptst = value;
    }

    /**
     * Gets the value of the rsvp property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRsvp() {
        return rsvp;
    }

    /**
     * Sets the value of the rsvp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRsvp(Boolean value) {
        this.rsvp = value;
    }

    /**
     * Gets the value of the member property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMember() {
        return member;
    }

    /**
     * Sets the value of the member property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMember(String value) {
        this.member = value;
    }

    /**
     * Gets the value of the delTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelTo() {
        return delTo;
    }

    /**
     * Sets the value of the delTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelTo(String value) {
        this.delTo = value;
    }

    /**
     * Gets the value of the delFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelFrom() {
        return delFrom;
    }

    /**
     * Sets the value of the delFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelFrom(String value) {
        this.delFrom = value;
    }

}
