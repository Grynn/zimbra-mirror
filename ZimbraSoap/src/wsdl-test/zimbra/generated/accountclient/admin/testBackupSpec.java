
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileCopier" type="{urn:zimbraAdmin}fileCopierSpec" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:zimbraAdmin}name" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="before" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sync" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="searchIndex" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="blobs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="secondaryBlobs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="zip" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="zipStore" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupSpec", propOrder = {
    "fileCopier",
    "account"
})
public class testBackupSpec {

    protected testFileCopierSpec fileCopier;
    protected List<testName> account;
    @XmlAttribute(name = "method")
    protected String method;
    @XmlAttribute(name = "target")
    protected String target;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "before")
    protected String before;
    @XmlAttribute(name = "sync")
    protected Boolean sync;
    @XmlAttribute(name = "searchIndex")
    protected String searchIndex;
    @XmlAttribute(name = "blobs")
    protected String blobs;
    @XmlAttribute(name = "secondaryBlobs")
    protected String secondaryBlobs;
    @XmlAttribute(name = "zip")
    protected Boolean zip;
    @XmlAttribute(name = "zipStore")
    protected Boolean zipStore;

    /**
     * Gets the value of the fileCopier property.
     * 
     * @return
     *     possible object is
     *     {@link testFileCopierSpec }
     *     
     */
    public testFileCopierSpec getFileCopier() {
        return fileCopier;
    }

    /**
     * Sets the value of the fileCopier property.
     * 
     * @param value
     *     allowed object is
     *     {@link testFileCopierSpec }
     *     
     */
    public void setFileCopier(testFileCopierSpec value) {
        this.fileCopier = value;
    }

    /**
     * Gets the value of the account property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the account property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testName }
     * 
     * 
     */
    public List<testName> getAccount() {
        if (account == null) {
            account = new ArrayList<testName>();
        }
        return this.account;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethod(String value) {
        this.method = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the before property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBefore() {
        return before;
    }

    /**
     * Sets the value of the before property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBefore(String value) {
        this.before = value;
    }

    /**
     * Gets the value of the sync property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSync() {
        return sync;
    }

    /**
     * Sets the value of the sync property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSync(Boolean value) {
        this.sync = value;
    }

    /**
     * Gets the value of the searchIndex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchIndex() {
        return searchIndex;
    }

    /**
     * Sets the value of the searchIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchIndex(String value) {
        this.searchIndex = value;
    }

    /**
     * Gets the value of the blobs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlobs() {
        return blobs;
    }

    /**
     * Sets the value of the blobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlobs(String value) {
        this.blobs = value;
    }

    /**
     * Gets the value of the secondaryBlobs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryBlobs() {
        return secondaryBlobs;
    }

    /**
     * Sets the value of the secondaryBlobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryBlobs(String value) {
        this.secondaryBlobs = value;
    }

    /**
     * Gets the value of the zip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isZip() {
        return zip;
    }

    /**
     * Sets the value of the zip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setZip(Boolean value) {
        this.zip = value;
    }

    /**
     * Gets the value of the zipStore property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isZipStore() {
        return zipStore;
    }

    /**
     * Sets the value of the zipStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setZipStore(Boolean value) {
        this.zipStore = value;
    }

}
