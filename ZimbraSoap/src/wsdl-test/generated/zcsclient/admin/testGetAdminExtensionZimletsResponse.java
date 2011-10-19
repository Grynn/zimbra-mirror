
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAdminExtensionZimletsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAdminExtensionZimletsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zimlets">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="zimlet" type="{urn:zimbraAdmin}adminZimletInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAdminExtensionZimletsResponse", propOrder = {
    "zimlets"
})
public class testGetAdminExtensionZimletsResponse {

    @XmlElement(required = true)
    protected testGetAdminExtensionZimletsResponse.Zimlets zimlets;

    /**
     * Gets the value of the zimlets property.
     * 
     * @return
     *     possible object is
     *     {@link testGetAdminExtensionZimletsResponse.Zimlets }
     *     
     */
    public testGetAdminExtensionZimletsResponse.Zimlets getZimlets() {
        return zimlets;
    }

    /**
     * Sets the value of the zimlets property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetAdminExtensionZimletsResponse.Zimlets }
     *     
     */
    public void setZimlets(testGetAdminExtensionZimletsResponse.Zimlets value) {
        this.zimlets = value;
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
     *         &lt;element name="zimlet" type="{urn:zimbraAdmin}adminZimletInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "zimlet"
    })
    public static class Zimlets {

        protected List<testAdminZimletInfo> zimlet;

        /**
         * Gets the value of the zimlet property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the zimlet property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getZimlet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testAdminZimletInfo }
         * 
         * 
         */
        public List<testAdminZimletInfo> getZimlet() {
            if (zimlet == null) {
                zimlet = new ArrayList<testAdminZimletInfo>();
            }
            return this.zimlet;
        }

    }

}
