
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testGranteeType;


/**
 * <p>Java class for accountACEinfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accountACEinfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="zid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="gt" use="required" type="{urn:zimbra}granteeType" />
 *       &lt;attribute name="right" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pw" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="deny" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accountACEinfo")
public class testAccountACEinfo {

    @XmlAttribute(name = "zid")
    protected String zid;
    @XmlAttribute(name = "gt", required = true)
    protected testGranteeType gt;
    @XmlAttribute(name = "right", required = true)
    protected String right;
    @XmlAttribute(name = "d")
    protected String d;
    @XmlAttribute(name = "key")
    protected String key;
    @XmlAttribute(name = "pw")
    protected String pw;
    @XmlAttribute(name = "deny")
    protected Boolean deny;

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
     * Gets the value of the gt property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeType }
     *     
     */
    public testGranteeType getGt() {
        return gt;
    }

    /**
     * Sets the value of the gt property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeType }
     *     
     */
    public void setGt(testGranteeType value) {
        this.gt = value;
    }

    /**
     * Gets the value of the right property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRight(String value) {
        this.right = value;
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
     * Gets the value of the deny property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeny() {
        return deny;
    }

    /**
     * Sets the value of the deny property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeny(Boolean value) {
        this.deny = value;
    }

}
