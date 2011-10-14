
package zimbra.generated.adminclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for folderActionSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="folderActionSelector">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}actionSelector">
 *       &lt;sequence>
 *         &lt;element name="grant" type="{urn:zimbraMail}actionGrantSelector" minOccurs="0"/>
 *         &lt;element name="acl" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="grant" type="{urn:zimbraMail}actionGrantSelector" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{urn:zimbraMail}retentionPolicy" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="recursive" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="excludeFreeBusy" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="zid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="gt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="view" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "folderActionSelector", propOrder = {
    "grant",
    "acl",
    "retentionPolicy"
})
public class testFolderActionSelector
    extends testActionSelector
{

    protected testActionGrantSelector grant;
    protected testFolderActionSelector.Acl acl;
    protected testRetentionPolicy retentionPolicy;
    @XmlAttribute(name = "recursive")
    protected Boolean recursive;
    @XmlAttribute(name = "url")
    protected String url;
    @XmlAttribute(name = "excludeFreeBusy")
    protected Boolean excludeFreeBusy;
    @XmlAttribute(name = "zid")
    protected String zid;
    @XmlAttribute(name = "gt")
    protected String gt;
    @XmlAttribute(name = "view")
    protected String view;

    /**
     * Gets the value of the grant property.
     * 
     * @return
     *     possible object is
     *     {@link testActionGrantSelector }
     *     
     */
    public testActionGrantSelector getGrant() {
        return grant;
    }

    /**
     * Sets the value of the grant property.
     * 
     * @param value
     *     allowed object is
     *     {@link testActionGrantSelector }
     *     
     */
    public void setGrant(testActionGrantSelector value) {
        this.grant = value;
    }

    /**
     * Gets the value of the acl property.
     * 
     * @return
     *     possible object is
     *     {@link testFolderActionSelector.Acl }
     *     
     */
    public testFolderActionSelector.Acl getAcl() {
        return acl;
    }

    /**
     * Sets the value of the acl property.
     * 
     * @param value
     *     allowed object is
     *     {@link testFolderActionSelector.Acl }
     *     
     */
    public void setAcl(testFolderActionSelector.Acl value) {
        this.acl = value;
    }

    /**
     * Gets the value of the retentionPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link testRetentionPolicy }
     *     
     */
    public testRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    /**
     * Sets the value of the retentionPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRetentionPolicy }
     *     
     */
    public void setRetentionPolicy(testRetentionPolicy value) {
        this.retentionPolicy = value;
    }

    /**
     * Gets the value of the recursive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRecursive() {
        return recursive;
    }

    /**
     * Sets the value of the recursive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRecursive(Boolean value) {
        this.recursive = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the excludeFreeBusy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeFreeBusy() {
        return excludeFreeBusy;
    }

    /**
     * Sets the value of the excludeFreeBusy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeFreeBusy(Boolean value) {
        this.excludeFreeBusy = value;
    }

    /**
     * Gets the value of the zid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZid() {
        return zid;
    }

    /**
     * Sets the value of the zid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZid(String value) {
        this.zid = value;
    }

    /**
     * Gets the value of the gt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGt() {
        return gt;
    }

    /**
     * Sets the value of the gt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGt(String value) {
        this.gt = value;
    }

    /**
     * Gets the value of the view property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getView() {
        return view;
    }

    /**
     * Sets the value of the view property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setView(String value) {
        this.view = value;
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
     *         &lt;element name="grant" type="{urn:zimbraMail}actionGrantSelector" maxOccurs="unbounded" minOccurs="0"/>
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
        "grant"
    })
    public static class Acl {

        protected List<testActionGrantSelector> grant;

        /**
         * Gets the value of the grant property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the grant property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGrant().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testActionGrantSelector }
         * 
         * 
         */
        public List<testActionGrantSelector> getGrant() {
            if (grant == null) {
                grant = new ArrayList<testActionGrantSelector>();
            }
            return this.grant;
        }

    }

}
