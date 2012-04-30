
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for verifyStoreManagerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verifyStoreManagerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="storeManagerClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="incomingTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="stageTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="linkTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="fetchTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="deleteTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verifyStoreManagerResponse")
public class testVerifyStoreManagerResponse {

    @XmlAttribute(name = "storeManagerClass")
    protected String storeManagerClass;
    @XmlAttribute(name = "incomingTime")
    protected Long incomingTime;
    @XmlAttribute(name = "stageTime")
    protected Long stageTime;
    @XmlAttribute(name = "linkTime")
    protected Long linkTime;
    @XmlAttribute(name = "fetchTime")
    protected Long fetchTime;
    @XmlAttribute(name = "deleteTime")
    protected Long deleteTime;

    /**
     * Gets the value of the storeManagerClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreManagerClass() {
        return storeManagerClass;
    }

    /**
     * Sets the value of the storeManagerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreManagerClass(String value) {
        this.storeManagerClass = value;
    }

    /**
     * Gets the value of the incomingTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIncomingTime() {
        return incomingTime;
    }

    /**
     * Sets the value of the incomingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIncomingTime(Long value) {
        this.incomingTime = value;
    }

    /**
     * Gets the value of the stageTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStageTime() {
        return stageTime;
    }

    /**
     * Sets the value of the stageTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStageTime(Long value) {
        this.stageTime = value;
    }

    /**
     * Gets the value of the linkTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLinkTime() {
        return linkTime;
    }

    /**
     * Sets the value of the linkTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLinkTime(Long value) {
        this.linkTime = value;
    }

    /**
     * Gets the value of the fetchTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFetchTime() {
        return fetchTime;
    }

    /**
     * Sets the value of the fetchTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFetchTime(Long value) {
        this.fetchTime = value;
    }

    /**
     * Gets the value of the deleteTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDeleteTime() {
        return deleteTime;
    }

    /**
     * Sets the value of the deleteTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDeleteTime(Long value) {
        this.deleteTime = value;
    }

}
