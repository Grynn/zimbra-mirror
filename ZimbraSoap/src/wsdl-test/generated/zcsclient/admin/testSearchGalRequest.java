
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testGalSearchType;


/**
 * <p>Java class for searchGalRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchGalRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="domain" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="limit" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="type" type="{urn:zimbra}galSearchType" />
 *       &lt;attribute name="galAcctId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchGalRequest")
public class testSearchGalRequest {

    @XmlAttribute(name = "domain", required = true)
    protected String domain;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "limit")
    protected Integer limit;
    @XmlAttribute(name = "type")
    protected testGalSearchType type;
    @XmlAttribute(name = "galAcctId")
    protected String galAcctId;

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLimit(Integer value) {
        this.limit = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link testGalSearchType }
     *     
     */
    public testGalSearchType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGalSearchType }
     *     
     */
    public void setType(testGalSearchType value) {
        this.type = value;
    }

    /**
     * Gets the value of the galAcctId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGalAcctId() {
        return galAcctId;
    }

    /**
     * Sets the value of the galAcctId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGalAcctId(String value) {
        this.galAcctId = value;
    }

}
