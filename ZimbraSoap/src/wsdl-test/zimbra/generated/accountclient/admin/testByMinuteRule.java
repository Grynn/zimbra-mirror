
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for byMinuteRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="byMinuteRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="minlist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byMinuteRule")
public class testByMinuteRule {

    @XmlAttribute(name = "minlist", required = true)
    protected String minlist;

    /**
     * Gets the value of the minlist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinlist() {
        return minlist;
    }

    /**
     * Sets the value of the minlist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinlist(String value) {
        this.minlist = value;
    }

}
