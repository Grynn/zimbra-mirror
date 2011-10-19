
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for byWeekNoRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="byWeekNoRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="wklist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byWeekNoRule")
public class testByWeekNoRule {

    @XmlAttribute(name = "wklist", required = true)
    protected String wklist;

    /**
     * Gets the value of the wklist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWklist() {
        return wklist;
    }

    /**
     * Sets the value of the wklist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWklist(String value) {
        this.wklist = value;
    }

}
