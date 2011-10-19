
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for byMonthDayRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="byMonthDayRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="modaylist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byMonthDayRule")
public class testByMonthDayRule {

    @XmlAttribute(name = "modaylist", required = true)
    protected String modaylist;

    /**
     * Gets the value of the modaylist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModaylist() {
        return modaylist;
    }

    /**
     * Sets the value of the modaylist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModaylist(String value) {
        this.modaylist = value;
    }

}
