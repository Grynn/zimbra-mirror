
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for moveMailboxInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moveMailboxInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dest" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="src" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="blobs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="secondaryBlobs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="searchIndex" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxSyncs" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="syncFinishThreshold" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="sync" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moveMailboxInfo")
public class testMoveMailboxInfo {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "dest", required = true)
    protected String dest;
    @XmlAttribute(name = "src", required = true)
    protected String src;
    @XmlAttribute(name = "blobs")
    protected String blobs;
    @XmlAttribute(name = "secondaryBlobs")
    protected String secondaryBlobs;
    @XmlAttribute(name = "searchIndex")
    protected String searchIndex;
    @XmlAttribute(name = "maxSyncs")
    protected Integer maxSyncs;
    @XmlAttribute(name = "syncFinishThreshold")
    protected Long syncFinishThreshold;
    @XmlAttribute(name = "sync")
    protected Boolean sync;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the dest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDest() {
        return dest;
    }

    /**
     * Sets the value of the dest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDest(String value) {
        this.dest = value;
    }

    /**
     * Gets the value of the src property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrc() {
        return src;
    }

    /**
     * Sets the value of the src property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrc(String value) {
        this.src = value;
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
     * Gets the value of the maxSyncs property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxSyncs() {
        return maxSyncs;
    }

    /**
     * Sets the value of the maxSyncs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxSyncs(Integer value) {
        this.maxSyncs = value;
    }

    /**
     * Gets the value of the syncFinishThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSyncFinishThreshold() {
        return syncFinishThreshold;
    }

    /**
     * Sets the value of the syncFinishThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSyncFinishThreshold(Long value) {
        this.syncFinishThreshold = value;
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

}
