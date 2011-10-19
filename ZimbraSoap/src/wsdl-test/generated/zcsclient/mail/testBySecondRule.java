
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bySecondRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bySecondRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="seclist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bySecondRule")
public class testBySecondRule {

    @XmlAttribute(name = "seclist", required = true)
    protected String seclist;

    /**
     * Gets the value of the seclist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeclist() {
        return seclist;
    }

    /**
     * Sets the value of the seclist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeclist(String value) {
        this.seclist = value;
    }

}
