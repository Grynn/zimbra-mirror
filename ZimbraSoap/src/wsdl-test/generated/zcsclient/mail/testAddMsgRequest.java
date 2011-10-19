
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addMsgRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addMsgRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="m" type="{urn:zimbraMail}addMsgSpec"/>
 *       &lt;/sequence>
 *       &lt;attribute name="filterSent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addMsgRequest", propOrder = {
    "m"
})
public class testAddMsgRequest {

    @XmlElement(required = true)
    protected testAddMsgSpec m;
    @XmlAttribute(name = "filterSent")
    protected Boolean filterSent;

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link testAddMsgSpec }
     *     
     */
    public testAddMsgSpec getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAddMsgSpec }
     *     
     */
    public void setM(testAddMsgSpec value) {
        this.m = value;
    }

    /**
     * Gets the value of the filterSent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFilterSent() {
        return filterSent;
    }

    /**
     * Sets the value of the filterSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFilterSent(Boolean value) {
        this.filterSent = value;
    }

}
