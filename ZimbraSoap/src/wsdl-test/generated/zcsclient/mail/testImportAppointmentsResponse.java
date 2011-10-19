
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for importAppointmentsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="importAppointmentsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ids" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="n" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "importAppointmentsResponse")
public class testImportAppointmentsResponse {

    @XmlAttribute(name = "ids", required = true)
    protected String ids;
    @XmlAttribute(name = "n", required = true)
    protected int n;

    /**
     * Gets the value of the ids property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIds() {
        return ids;
    }

    /**
     * Sets the value of the ids property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIds(String value) {
        this.ids = value;
    }

    /**
     * Gets the value of the n property.
     * 
     */
    public int getN() {
        return n;
    }

    /**
     * Sets the value of the n property.
     * 
     */
    public void setN(int value) {
        this.n = value;
    }

}
