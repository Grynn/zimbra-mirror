
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for moveBlobsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moveBlobsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="numBlobsMoved" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numBytesMoved" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="totalMailboxes" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moveBlobsResponse")
public class testMoveBlobsResponse {

    @XmlAttribute(name = "numBlobsMoved")
    protected Integer numBlobsMoved;
    @XmlAttribute(name = "numBytesMoved")
    protected Long numBytesMoved;
    @XmlAttribute(name = "totalMailboxes")
    protected Integer totalMailboxes;

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

}
