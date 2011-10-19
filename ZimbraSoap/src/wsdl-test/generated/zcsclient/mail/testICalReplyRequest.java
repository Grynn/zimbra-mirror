
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for iCalReplyRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="iCalReplyRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ical" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "iCalReplyRequest", propOrder = {
    "ical"
})
public class testICalReplyRequest {

    @XmlElement(required = true)
    protected String ical;

    /**
     * Gets the value of the ical property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcal() {
        return ical;
    }

    /**
     * Sets the value of the ical property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcal(String value) {
        this.ical = value;
    }

}
