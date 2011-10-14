
package zimbra.generated.accountclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for byHourRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="byHourRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="hrlist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byHourRule")
public class testByHourRule {

    @XmlAttribute(name = "hrlist", required = true)
    protected String hrlist;

    /**
     * Gets the value of the hrlist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHrlist() {
        return hrlist;
    }

    /**
     * Sets the value of the hrlist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHrlist(String value) {
        this.hrlist = value;
    }

}
