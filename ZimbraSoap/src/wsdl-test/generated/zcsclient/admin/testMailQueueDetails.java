
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailQueueDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailQueueDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="qs" type="{urn:zimbraAdmin}queueSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="qi" type="{urn:zimbraAdmin}queueItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="time" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="scan" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="total" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="more" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailQueueDetails", propOrder = {
    "qs",
    "qi"
})
public class testMailQueueDetails {

    protected List<testQueueSummary> qs;
    protected List<testQueueItem> qi;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "time", required = true)
    protected long time;
    @XmlAttribute(name = "scan", required = true)
    protected boolean scan;
    @XmlAttribute(name = "total", required = true)
    protected int total;
    @XmlAttribute(name = "more", required = true)
    protected boolean more;

    /**
     * Gets the value of the qs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testQueueSummary }
     * 
     * 
     */
    public List<testQueueSummary> getQs() {
        if (qs == null) {
            qs = new ArrayList<testQueueSummary>();
        }
        return this.qs;
    }

    /**
     * Gets the value of the qi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testQueueItem }
     * 
     * 
     */
    public List<testQueueItem> getQi() {
        if (qi == null) {
            qi = new ArrayList<testQueueItem>();
        }
        return this.qi;
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
     * Gets the value of the time property.
     * 
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     */
    public void setTime(long value) {
        this.time = value;
    }

    /**
     * Gets the value of the scan property.
     * 
     */
    public boolean isScan() {
        return scan;
    }

    /**
     * Sets the value of the scan property.
     * 
     */
    public void setScan(boolean value) {
        this.scan = value;
    }

    /**
     * Gets the value of the total property.
     * 
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     */
    public void setTotal(int value) {
        this.total = value;
    }

    /**
     * Gets the value of the more property.
     * 
     */
    public boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     */
    public void setMore(boolean value) {
        this.more = value;
    }

}
