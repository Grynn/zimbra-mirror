
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testWaitSetAddSpec;


/**
 * <p>Java class for adminCreateWaitSetRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminCreateWaitSetRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="add" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="a" type="{urn:zimbra}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="defTypes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="allAccounts" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminCreateWaitSetRequest", propOrder = {
    "add"
})
public class testAdminCreateWaitSetRequest {

    protected testAdminCreateWaitSetRequest.Add add;
    @XmlAttribute(name = "defTypes", required = true)
    protected String defTypes;
    @XmlAttribute(name = "allAccounts")
    protected Boolean allAccounts;

    /**
     * Gets the value of the add property.
     * 
     * @return
     *     possible object is
     *     {@link testAdminCreateWaitSetRequest.Add }
     *     
     */
    public testAdminCreateWaitSetRequest.Add getAdd() {
        return add;
    }

    /**
     * Sets the value of the add property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAdminCreateWaitSetRequest.Add }
     *     
     */
    public void setAdd(testAdminCreateWaitSetRequest.Add value) {
        this.add = value;
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
     * Gets the value of the allAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllAccounts() {
        return allAccounts;
    }

    /**
     * Sets the value of the allAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllAccounts(Boolean value) {
        this.allAccounts = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="a" type="{urn:zimbra}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "a"
    })
    public static class Add {

        protected List<testWaitSetAddSpec> a;

        /**
         * Gets the value of the a property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the a property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testWaitSetAddSpec }
         * 
         * 
         */
        public List<testWaitSetAddSpec> getA() {
            if (a == null) {
                a = new ArrayList<testWaitSetAddSpec>();
            }
            return this.a;
        }

    }

}
