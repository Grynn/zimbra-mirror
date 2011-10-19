
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bySetPosRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bySetPosRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="poslist" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bySetPosRule")
public class testBySetPosRule {

    @XmlAttribute(name = "poslist", required = true)
    protected String poslist;

    /**
     * Gets the value of the poslist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoslist() {
        return poslist;
    }

    /**
     * Sets the value of the poslist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoslist(String value) {
        this.poslist = value;
    }

}
