
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for browseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="browseRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="browseBy" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="regex" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxToReturn" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "browseRequest")
public class testBrowseRequest {

    @XmlAttribute(name = "browseBy", required = true)
    protected String browseBy;
    @XmlAttribute(name = "regex")
    protected String regex;
    @XmlAttribute(name = "maxToReturn")
    protected Integer maxToReturn;

    /**
     * Gets the value of the browseBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowseBy() {
        return browseBy;
    }

    /**
     * Sets the value of the browseBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowseBy(String value) {
        this.browseBy = value;
    }

    /**
     * Gets the value of the regex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the value of the regex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegex(String value) {
        this.regex = value;
    }

    /**
     * Gets the value of the maxToReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxToReturn() {
        return maxToReturn;
    }

    /**
     * Sets the value of the maxToReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxToReturn(Integer value) {
        this.maxToReturn = value;
    }

}
