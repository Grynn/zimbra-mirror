
package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shareInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shareInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ownerId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerEmail" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="folderId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="folderUuid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="folderPath" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="view" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rights" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="granteeType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="granteeId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="granteeName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="granteeDisplayName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shareInfo")
public class testShareInfo {

    @XmlAttribute(name = "ownerId", required = true)
    protected String ownerId;
    @XmlAttribute(name = "ownerEmail", required = true)
    protected String ownerEmail;
    @XmlAttribute(name = "ownerName", required = true)
    protected String ownerName;
    @XmlAttribute(name = "folderId", required = true)
    protected int folderId;
    @XmlAttribute(name = "folderUuid", required = true)
    protected String folderUuid;
    @XmlAttribute(name = "folderPath", required = true)
    protected String folderPath;
    @XmlAttribute(name = "view", required = true)
    protected String view;
    @XmlAttribute(name = "rights", required = true)
    protected String rights;
    @XmlAttribute(name = "granteeType", required = true)
    protected String granteeType;
    @XmlAttribute(name = "granteeId", required = true)
    protected String granteeId;
    @XmlAttribute(name = "granteeName", required = true)
    protected String granteeName;
    @XmlAttribute(name = "granteeDisplayName", required = true)
    protected String granteeDisplayName;
    @XmlAttribute(name = "mid")
    protected String mid;

    /**
     * Gets the value of the ownerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the value of the ownerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerId(String value) {
        this.ownerId = value;
    }

    /**
     * Gets the value of the ownerEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerEmail() {
        return ownerEmail;
    }

    /**
     * Sets the value of the ownerEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerEmail(String value) {
        this.ownerEmail = value;
    }

    /**
     * Gets the value of the ownerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the value of the ownerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerName(String value) {
        this.ownerName = value;
    }

    /**
     * Gets the value of the folderId property.
     * 
     */
    public int getFolderId() {
        return folderId;
    }

    /**
     * Sets the value of the folderId property.
     * 
     */
    public void setFolderId(int value) {
        this.folderId = value;
    }

    /**
     * Gets the value of the folderUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * Sets the value of the folderUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderUuid(String value) {
        this.folderUuid = value;
    }

    /**
     * Gets the value of the folderPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * Sets the value of the folderPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderPath(String value) {
        this.folderPath = value;
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
     * Gets the value of the rights property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRights() {
        return rights;
    }

    /**
     * Sets the value of the rights property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRights(String value) {
        this.rights = value;
    }

    /**
     * Gets the value of the granteeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGranteeType() {
        return granteeType;
    }

    /**
     * Sets the value of the granteeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGranteeType(String value) {
        this.granteeType = value;
    }

    /**
     * Gets the value of the granteeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGranteeId() {
        return granteeId;
    }

    /**
     * Sets the value of the granteeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGranteeId(String value) {
        this.granteeId = value;
    }

    /**
     * Gets the value of the granteeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGranteeName() {
        return granteeName;
    }

    /**
     * Sets the value of the granteeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGranteeName(String value) {
        this.granteeName = value;
    }

    /**
     * Gets the value of the granteeDisplayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGranteeDisplayName() {
        return granteeDisplayName;
    }

    /**
     * Sets the value of the granteeDisplayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGranteeDisplayName(String value) {
        this.granteeDisplayName = value;
    }

    /**
     * Gets the value of the mid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMid() {
        return mid;
    }

    /**
     * Sets the value of the mid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMid(String value) {
        this.mid = value;
    }

}
