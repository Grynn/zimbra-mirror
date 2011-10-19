
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for actionGrantSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="actionGrantSelector">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="perm" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="gt" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="zid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="args" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pw" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "actionGrantSelector")
public class testActionGrantSelector {

    @XmlAttribute(name = "perm", required = true)
    protected String perm;
    @XmlAttribute(name = "gt", required = true)
    protected String gt;
    @XmlAttribute(name = "zid")
    protected String zid;
    @XmlAttribute(name = "d")
    protected String d;
    @XmlAttribute(name = "args")
    protected String args;
    @XmlAttribute(name = "pw")
    protected String pw;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the perm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerm() {
        return perm;
    }

    /**
     * Sets the value of the perm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerm(String value) {
        this.perm = value;
    }

    /**
     * Gets the value of the gt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGt() {
        return gt;
    }

    /**
     * Sets the value of the gt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGt(String value) {
        this.gt = value;
    }

    /**
     * Gets the value of the zid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZid() {
        return zid;
    }

    /**
     * Sets the value of the zid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZid(String value) {
        this.zid = value;
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
     * Gets the value of the args property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArgs() {
        return args;
    }

    /**
     * Sets the value of the args property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArgs(String value) {
        this.args = value;
    }

    /**
     * Gets the value of the pw property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPw() {
        return pw;
    }

    /**
     * Sets the value of the pw property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPw(String value) {
        this.pw = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

}
