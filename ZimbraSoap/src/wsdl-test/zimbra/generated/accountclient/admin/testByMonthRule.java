
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for byMonthRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="byMonthRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="molist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byMonthRule")
public class testByMonthRule {

    @XmlAttribute(name = "molist", required = true)
    protected String molist;

    /**
     * Gets the value of the molist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMolist() {
        return molist;
    }

    /**
     * Sets the value of the molist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMolist(String value) {
        this.molist = value;
    }

}
