
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for moveBlobsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moveBlobsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="query" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="types" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sourceVolumeIds" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="destVolumeId" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="maxBytes" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moveBlobsRequest", propOrder = {
    "query"
})
public class testMoveBlobsRequest {

    protected String query;
    @XmlAttribute(name = "types", required = true)
    protected String types;
    @XmlAttribute(name = "sourceVolumeIds", required = true)
    protected String sourceVolumeIds;
    @XmlAttribute(name = "destVolumeId", required = true)
    protected short destVolumeId;
    @XmlAttribute(name = "maxBytes")
    protected Long maxBytes;

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

    /**
     * Gets the value of the types property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypes() {
        return types;
    }

    /**
     * Sets the value of the types property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypes(String value) {
        this.types = value;
    }

    /**
     * Gets the value of the sourceVolumeIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceVolumeIds() {
        return sourceVolumeIds;
    }

    /**
     * Sets the value of the sourceVolumeIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceVolumeIds(String value) {
        this.sourceVolumeIds = value;
    }

    /**
     * Gets the value of the destVolumeId property.
     * 
     */
    public short getDestVolumeId() {
        return destVolumeId;
    }

    /**
     * Sets the value of the destVolumeId property.
     * 
     */
    public void setDestVolumeId(short value) {
        this.destVolumeId = value;
    }

    /**
     * Gets the value of the maxBytes property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxBytes() {
        return maxBytes;
    }

    /**
     * Sets the value of the maxBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxBytes(Long value) {
        this.maxBytes = value;
    }

}
