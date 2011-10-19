
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testIdAndType;


/**
 * <p>Java class for adminCreateWaitSetResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminCreateWaitSetResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{urn:zimbra}idAndType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="waitSet" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defTypes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="seq" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminCreateWaitSetResponse", propOrder = {
    "error"
})
public class testAdminCreateWaitSetResponse {

    protected List<testIdAndType> error;
    @XmlAttribute(name = "waitSet")
    protected String waitSet;
    @XmlAttribute(name = "defTypes", required = true)
    protected String defTypes;
    @XmlAttribute(name = "seq", required = true)
    protected int seq;

    /**
     * Gets the value of the error property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testIdAndType }
     * 
     * 
     */
    public List<testIdAndType> getError() {
        if (error == null) {
            error = new ArrayList<testIdAndType>();
        }
        return this.error;
    }

    /**
     * Gets the value of the waitSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaitSet() {
        return waitSet;
    }

    /**
     * Sets the value of the waitSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitSet(String value) {
        this.waitSet = value;
    }

    /**
     * Gets the value of the defTypes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefTypes() {
        return defTypes;
    }

    /**
     * Sets the value of the defTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefTypes(String value) {
        this.defTypes = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     */
    public int getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     */
    public void setSeq(int value) {
        this.seq = value;
    }

}
