
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getRightsDocResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRightsDocResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="package" type="{urn:zimbraAdmin}packageRightsInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="notUsed" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="domainAdmin-copypaste-to-zimbra-rights-domainadmin-xml-template">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="right" type="{urn:zimbraAdmin}domainAdminRight" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "getRightsDocResponse", propOrder = {
    "_package",
    "notUsed",
    "domainAdminCopypasteToZimbraRightsDomainadminXmlTemplate"
})
public class testGetRightsDocResponse {

    @XmlElement(name = "package")
    protected List<testPackageRightsInfo> _package;
    protected List<String> notUsed;
    @XmlElement(name = "domainAdmin-copypaste-to-zimbra-rights-domainadmin-xml-template", required = true)
    protected testGetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate domainAdminCopypasteToZimbraRightsDomainadminXmlTemplate;

    /**
     * Gets the value of the package property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the package property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testPackageRightsInfo }
     * 
     * 
     */
    public List<testPackageRightsInfo> getPackage() {
        if (_package == null) {
            _package = new ArrayList<testPackageRightsInfo>();
        }
        return this._package;
    }

    /**
     * Gets the value of the notUsed property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notUsed property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotUsed().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNotUsed() {
        if (notUsed == null) {
            notUsed = new ArrayList<String>();
        }
        return this.notUsed;
    }

    /**
     * Gets the value of the domainAdminCopypasteToZimbraRightsDomainadminXmlTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link testGetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate }
     *     
     */
    public testGetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate getDomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate() {
        return domainAdminCopypasteToZimbraRightsDomainadminXmlTemplate;
    }

    /**
     * Sets the value of the domainAdminCopypasteToZimbraRightsDomainadminXmlTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate }
     *     
     */
    public void setDomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate(testGetRightsDocResponse.DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate value) {
        this.domainAdminCopypasteToZimbraRightsDomainadminXmlTemplate = value;
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
     *         &lt;element name="right" type="{urn:zimbraAdmin}domainAdminRight" maxOccurs="unbounded" minOccurs="0"/>
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
        "right"
    })
    public static class DomainAdminCopypasteToZimbraRightsDomainadminXmlTemplate {

        protected List<testDomainAdminRight> right;

        /**
         * Gets the value of the right property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the right property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRight().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testDomainAdminRight }
         * 
         * 
         */
        public List<testDomainAdminRight> getRight() {
            if (right == null) {
                right = new ArrayList<testDomainAdminRight>();
            }
            return this.right;
        }

    }

}
