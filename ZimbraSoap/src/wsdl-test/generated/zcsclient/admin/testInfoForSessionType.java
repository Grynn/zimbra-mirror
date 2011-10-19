
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for infoForSessionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="infoForSessionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zid" type="{urn:zimbraAdmin}accountSessionInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="s" type="{urn:zimbraAdmin}sessionInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="activeAccounts" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="activeSessions" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoForSessionType", propOrder = {
    "zid",
    "s"
})
public class testInfoForSessionType {

    protected List<testAccountSessionInfo> zid;
    protected List<testSessionInfo> s;
    @XmlAttribute(name = "activeAccounts")
    protected Integer activeAccounts;
    @XmlAttribute(name = "activeSessions", required = true)
    protected int activeSessions;

    /**
     * Gets the value of the zid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the zid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getZid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAccountSessionInfo }
     * 
     * 
     */
    public List<testAccountSessionInfo> getZid() {
        if (zid == null) {
            zid = new ArrayList<testAccountSessionInfo>();
        }
        return this.zid;
    }

    /**
     * Gets the value of the s property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the s property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testSessionInfo }
     * 
     * 
     */
    public List<testSessionInfo> getS() {
        if (s == null) {
            s = new ArrayList<testSessionInfo>();
        }
        return this.s;
    }

    /**
     * Gets the value of the activeAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getActiveAccounts() {
        return activeAccounts;
    }

    /**
     * Sets the value of the activeAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setActiveAccounts(Integer value) {
        this.activeAccounts = value;
    }

    /**
     * Gets the value of the activeSessions property.
     * 
     */
    public int getActiveSessions() {
        return activeSessions;
    }

    /**
     * Sets the value of the activeSessions property.
     * 
     */
    public void setActiveSessions(int value) {
        this.activeSessions = value;
    }

}
