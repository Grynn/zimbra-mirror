
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for partInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="partInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mp" type="{urn:zimbraMail}partInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="part" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ct" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="cd" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="filename" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ci" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="body" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="truncated" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "partInfo", propOrder = {
    "content",
    "mp"
})
public class testPartInfo {

    protected String content;
    protected List<testPartInfo> mp;
    @XmlAttribute(name = "part", required = true)
    protected String part;
    @XmlAttribute(name = "ct", required = true)
    protected String ct;
    @XmlAttribute(name = "s")
    protected Integer s;
    @XmlAttribute(name = "cd")
    protected String cd;
    @XmlAttribute(name = "filename")
    protected String filename;
    @XmlAttribute(name = "ci")
    protected String ci;
    @XmlAttribute(name = "cl")
    protected String cl;
    @XmlAttribute(name = "body")
    protected Boolean body;
    @XmlAttribute(name = "truncated")
    protected Boolean truncated;

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
     * {@link testPartInfo }
     * 
     * 
     */
    public List<testPartInfo> getMp() {
        if (mp == null) {
            mp = new ArrayList<testPartInfo>();
        }
        return this.mp;
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
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setS(Integer value) {
        this.s = value;
    }

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCd(String value) {
        this.cd = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
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

    /**
     * Gets the value of the cl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCl() {
        return cl;
    }

    /**
     * Sets the value of the cl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCl(String value) {
        this.cl = value;
    }

    /**
     * Gets the value of the body property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBody() {
        return body;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBody(Boolean value) {
        this.body = value;
    }

    /**
     * Gets the value of the truncated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTruncated() {
        return truncated;
    }

    /**
     * Sets the value of the truncated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTruncated(Boolean value) {
        this.truncated = value;
    }

}
