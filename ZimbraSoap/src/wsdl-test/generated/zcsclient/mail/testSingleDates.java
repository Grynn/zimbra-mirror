
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for singleDates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="singleDates">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dtval" type="{urn:zimbraMail}dtVal" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="tz" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "singleDates", propOrder = {
    "dtval"
})
public class testSingleDates {

    protected List<testDtVal> dtval;
    @XmlAttribute(name = "tz")
    protected String tz;

    /**
     * Gets the value of the dtval property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dtval property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDtval().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testDtVal }
     * 
     * 
     */
    public List<testDtVal> getDtval() {
        if (dtval == null) {
            dtval = new ArrayList<testDtVal>();
        }
        return this.dtval;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTz(String value) {
        this.tz = value;
    }

}
