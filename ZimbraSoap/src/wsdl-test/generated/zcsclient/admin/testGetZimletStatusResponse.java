
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getZimletStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getZimletStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zimlets" type="{urn:zimbraAdmin}zimletStatusParent"/>
 *         &lt;element name="cos" type="{urn:zimbraAdmin}zimletStatusCos" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getZimletStatusResponse", propOrder = {
    "zimlets",
    "cos"
})
public class testGetZimletStatusResponse {

    @XmlElement(required = true)
    protected testZimletStatusParent zimlets;
    protected List<testZimletStatusCos> cos;

    /**
     * Gets the value of the zimlets property.
     * 
     * @return
     *     possible object is
     *     {@link testZimletStatusParent }
     *     
     */
    public testZimletStatusParent getZimlets() {
        return zimlets;
    }

    /**
     * Sets the value of the zimlets property.
     * 
     * @param value
     *     allowed object is
     *     {@link testZimletStatusParent }
     *     
     */
    public void setZimlets(testZimletStatusParent value) {
        this.zimlets = value;
    }

    /**
     * Gets the value of the cos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testZimletStatusCos }
     * 
     * 
     */
    public List<testZimletStatusCos> getCos() {
        if (cos == null) {
            cos = new ArrayList<testZimletStatusCos>();
        }
        return this.cos;
    }

}
