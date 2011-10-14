
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for restoreSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="restoreSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileCopier" type="{urn:zimbraAdmin}fileCopierSpec" minOccurs="0"/>
 *         &lt;element name="account" type="{urn:zimbraAdmin}name" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="searchIndex" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="blobs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="secondaryBlobs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sysData" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="includeIncrementals" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="replayRedo" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="continue" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="restoreToTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="restoreToRedoSeq" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="restoreToIncrLabel" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ignoreRedoErrors" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="skipDeleteOps" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="skipDeletedAccounts" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "restoreSpec", propOrder = {
    "fileCopier",
    "account"
})
public class testRestoreSpec {

    protected testFileCopierSpec fileCopier;
    protected List<testName> account;
    @XmlAttribute(name = "method")
    protected String method;
    @XmlAttribute(name = "searchIndex")
    protected String searchIndex;
    @XmlAttribute(name = "blobs")
    protected String blobs;
    @XmlAttribute(name = "secondaryBlobs")
    protected String secondaryBlobs;
    @XmlAttribute(name = "target")
    protected String target;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "sysData")
    protected Boolean sysData;
    @XmlAttribute(name = "includeIncrementals")
    protected Boolean includeIncrementals;
    @XmlAttribute(name = "replayRedo")
    protected Boolean replayRedo;
    @XmlAttribute(name = "continue")
    protected Boolean _continue;
    @XmlAttribute(name = "prefix")
    protected String prefix;
    @XmlAttribute(name = "restoreToTime")
    protected Long restoreToTime;
    @XmlAttribute(name = "restoreToRedoSeq")
    protected Long restoreToRedoSeq;
    @XmlAttribute(name = "restoreToIncrLabel")
    protected String restoreToIncrLabel;
    @XmlAttribute(name = "ignoreRedoErrors")
    protected Boolean ignoreRedoErrors;
    @XmlAttribute(name = "skipDeleteOps")
    protected Boolean skipDeleteOps;
    @XmlAttribute(name = "skipDeletedAccounts")
    protected Boolean skipDeletedAccounts;

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
     * Gets the value of the sysData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSysData() {
        return sysData;
    }

    /**
     * Sets the value of the sysData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSysData(Boolean value) {
        this.sysData = value;
    }

    /**
     * Gets the value of the includeIncrementals property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeIncrementals() {
        return includeIncrementals;
    }

    /**
     * Sets the value of the includeIncrementals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeIncrementals(Boolean value) {
        this.includeIncrementals = value;
    }

    /**
     * Gets the value of the replayRedo property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReplayRedo() {
        return replayRedo;
    }

    /**
     * Sets the value of the replayRedo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReplayRedo(Boolean value) {
        this.replayRedo = value;
    }

    /**
     * Gets the value of the continue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isContinue() {
        return _continue;
    }

    /**
     * Sets the value of the continue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setContinue(Boolean value) {
        this._continue = value;
    }

    /**
     * Gets the value of the prefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the value of the prefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrefix(String value) {
        this.prefix = value;
    }

    /**
     * Gets the value of the restoreToTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRestoreToTime() {
        return restoreToTime;
    }

    /**
     * Sets the value of the restoreToTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRestoreToTime(Long value) {
        this.restoreToTime = value;
    }

    /**
     * Gets the value of the restoreToRedoSeq property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRestoreToRedoSeq() {
        return restoreToRedoSeq;
    }

    /**
     * Sets the value of the restoreToRedoSeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRestoreToRedoSeq(Long value) {
        this.restoreToRedoSeq = value;
    }

    /**
     * Gets the value of the restoreToIncrLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestoreToIncrLabel() {
        return restoreToIncrLabel;
    }

    /**
     * Sets the value of the restoreToIncrLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestoreToIncrLabel(String value) {
        this.restoreToIncrLabel = value;
    }

    /**
     * Gets the value of the ignoreRedoErrors property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIgnoreRedoErrors() {
        return ignoreRedoErrors;
    }

    /**
     * Sets the value of the ignoreRedoErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIgnoreRedoErrors(Boolean value) {
        this.ignoreRedoErrors = value;
    }

    /**
     * Gets the value of the skipDeleteOps property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSkipDeleteOps() {
        return skipDeleteOps;
    }

    /**
     * Sets the value of the skipDeleteOps property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSkipDeleteOps(Boolean value) {
        this.skipDeleteOps = value;
    }

    /**
     * Gets the value of the skipDeletedAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSkipDeletedAccounts() {
        return skipDeletedAccounts;
    }

    /**
     * Sets the value of the skipDeletedAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSkipDeletedAccounts(Boolean value) {
        this.skipDeletedAccounts = value;
    }

}
