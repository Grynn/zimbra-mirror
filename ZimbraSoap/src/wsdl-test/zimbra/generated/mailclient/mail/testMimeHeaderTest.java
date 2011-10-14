
package zimbra.generated.mailclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mimeHeaderTest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mimeHeaderTest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}filterTest">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="header" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stringComparison" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="caseSensitive" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mimeHeaderTest")
public class testMimeHeaderTest
    extends testFilterTest
{

    @XmlAttribute(name = "header")
    protected String header;
    @XmlAttribute(name = "stringComparison")
    protected String stringComparison;
    @XmlAttribute(name = "value")
    protected String value;
    @XmlAttribute(name = "caseSensitive")
    protected String caseSensitive;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeader(String value) {
        this.header = value;
    }

    /**
     * Gets the value of the stringComparison property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringComparison() {
        return stringComparison;
    }

    /**
     * Sets the value of the stringComparison property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringComparison(String value) {
        this.stringComparison = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the caseSensitive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets the value of the caseSensitive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseSensitive(String value) {
        this.caseSensitive = value;
    }

}
