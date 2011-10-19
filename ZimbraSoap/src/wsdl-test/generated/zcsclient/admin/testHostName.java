
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hostName complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hostName">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="hn" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hostName")
public class testHostName {

    @XmlAttribute(name = "hn", required = true)
    protected String hn;

    /**
     * Gets the value of the hn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHn() {
        return hn;
    }

    /**
     * Sets the value of the hn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHn(String value) {
        this.hn = value;
    }

}
