
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for exportContactsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exportContactsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ct" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="csvfmt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="csvlocale" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="csvsep" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exportContactsRequest")
public class testExportContactsRequest {

    @XmlAttribute(name = "ct", required = true)
    protected String ct;
    @XmlAttribute(name = "l")
    protected String l;
    @XmlAttribute(name = "csvfmt")
    protected String csvfmt;
    @XmlAttribute(name = "csvlocale")
    protected String csvlocale;
    @XmlAttribute(name = "csvsep")
    protected String csvsep;

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
     * Gets the value of the csvfmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsvfmt() {
        return csvfmt;
    }

    /**
     * Sets the value of the csvfmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsvfmt(String value) {
        this.csvfmt = value;
    }

    /**
     * Gets the value of the csvlocale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsvlocale() {
        return csvlocale;
    }

    /**
     * Sets the value of the csvlocale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsvlocale(String value) {
        this.csvlocale = value;
    }

    /**
     * Gets the value of the csvsep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsvsep() {
        return csvsep;
    }

    /**
     * Sets the value of the csvsep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsvsep(String value) {
        this.csvsep = value;
    }

}
