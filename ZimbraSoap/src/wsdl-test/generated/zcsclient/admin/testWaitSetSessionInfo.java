
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for waitSetSessionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waitSetSessionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="interestMask" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="highestChangeId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="lastAccessTime" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="creationTime" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="sessionId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="token" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waitSetSessionInfo")
public class testWaitSetSessionInfo {

    @XmlAttribute(name = "interestMask", required = true)
    protected String interestMask;
    @XmlAttribute(name = "highestChangeId", required = true)
    protected int highestChangeId;
    @XmlAttribute(name = "lastAccessTime", required = true)
    protected long lastAccessTime;
    @XmlAttribute(name = "creationTime", required = true)
    protected long creationTime;
    @XmlAttribute(name = "sessionId", required = true)
    protected String sessionId;
    @XmlAttribute(name = "token")
    protected String token;

    /**
     * Gets the value of the interestMask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterestMask() {
        return interestMask;
    }

    /**
     * Sets the value of the interestMask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterestMask(String value) {
        this.interestMask = value;
    }

    /**
     * Gets the value of the highestChangeId property.
     * 
     */
    public int getHighestChangeId() {
        return highestChangeId;
    }

    /**
     * Sets the value of the highestChangeId property.
     * 
     */
    public void setHighestChangeId(int value) {
        this.highestChangeId = value;
    }

    /**
     * Gets the value of the lastAccessTime property.
     * 
     */
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Sets the value of the lastAccessTime property.
     * 
     */
    public void setLastAccessTime(long value) {
        this.lastAccessTime = value;
    }

    /**
     * Gets the value of the creationTime property.
     * 
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     * 
     */
    public void setCreationTime(long value) {
        this.creationTime = value;
    }

    /**
     * Gets the value of the sessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

}
