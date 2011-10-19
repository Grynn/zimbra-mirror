
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailQueueQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailQueueQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}query"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="scan" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="wait" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailQueueQuery", propOrder = {
    "query"
})
public class testMailQueueQuery {

    @XmlElement(required = true)
    protected testQueueQuery query;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "scan")
    protected Boolean scan;
    @XmlAttribute(name = "wait")
    protected Long wait;

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link testQueueQuery }
     *     
     */
    public testQueueQuery getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link testQueueQuery }
     *     
     */
    public void setQuery(testQueueQuery value) {
        this.query = value;
    }

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
     * Gets the value of the scan property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScan() {
        return scan;
    }

    /**
     * Sets the value of the scan property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScan(Boolean value) {
        this.scan = value;
    }

    /**
     * Gets the value of the wait property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getWait() {
        return wait;
    }

    /**
     * Sets the value of the wait property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setWait(Long value) {
        this.wait = value;
    }

}
