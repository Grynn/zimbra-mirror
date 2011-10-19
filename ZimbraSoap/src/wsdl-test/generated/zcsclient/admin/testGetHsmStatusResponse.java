
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getHsmStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getHsmStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="running" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="startDate" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="endDate" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="wasAborted" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="aborting" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="error" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="numBlobsMoved" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numBytesMoved" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="numMailboxes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="totalMailboxes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="destVolumeId" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="query" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getHsmStatusResponse")
public class testGetHsmStatusResponse {

    @XmlAttribute(name = "running", required = true)
    protected boolean running;
    @XmlAttribute(name = "startDate")
    protected Long startDate;
    @XmlAttribute(name = "endDate")
    protected Long endDate;
    @XmlAttribute(name = "wasAborted")
    protected Boolean wasAborted;
    @XmlAttribute(name = "aborting")
    protected Boolean aborting;
    @XmlAttribute(name = "error")
    protected String error;
    @XmlAttribute(name = "numBlobsMoved")
    protected Integer numBlobsMoved;
    @XmlAttribute(name = "numBytesMoved")
    protected Long numBytesMoved;
    @XmlAttribute(name = "numMailboxes")
    protected Integer numMailboxes;
    @XmlAttribute(name = "totalMailboxes")
    protected Integer totalMailboxes;
    @XmlAttribute(name = "destVolumeId")
    protected Short destVolumeId;
    @XmlAttribute(name = "query")
    protected String query;

    /**
     * Gets the value of the running property.
     * 
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets the value of the running property.
     * 
     */
    public void setRunning(boolean value) {
        this.running = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStartDate(Long value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEndDate(Long value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the wasAborted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWasAborted() {
        return wasAborted;
    }

    /**
     * Sets the value of the wasAborted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWasAborted(Boolean value) {
        this.wasAborted = value;
    }

    /**
     * Gets the value of the aborting property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAborting() {
        return aborting;
    }

    /**
     * Sets the value of the aborting property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAborting(Boolean value) {
        this.aborting = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setError(String value) {
        this.error = value;
    }

    /**
     * Gets the value of the numBlobsMoved property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumBlobsMoved() {
        return numBlobsMoved;
    }

    /**
     * Sets the value of the numBlobsMoved property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumBlobsMoved(Integer value) {
        this.numBlobsMoved = value;
    }

    /**
     * Gets the value of the numBytesMoved property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumBytesMoved() {
        return numBytesMoved;
    }

    /**
     * Sets the value of the numBytesMoved property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumBytesMoved(Long value) {
        this.numBytesMoved = value;
    }

    /**
     * Gets the value of the numMailboxes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumMailboxes() {
        return numMailboxes;
    }

    /**
     * Sets the value of the numMailboxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumMailboxes(Integer value) {
        this.numMailboxes = value;
    }

    /**
     * Gets the value of the totalMailboxes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalMailboxes() {
        return totalMailboxes;
    }

    /**
     * Sets the value of the totalMailboxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalMailboxes(Integer value) {
        this.totalMailboxes = value;
    }

    /**
     * Gets the value of the destVolumeId property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getDestVolumeId() {
        return destVolumeId;
    }

    /**
     * Sets the value of the destVolumeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setDestVolumeId(Short value) {
        this.destVolumeId = value;
    }

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuery(String value) {
        this.query = value;
    }

}
