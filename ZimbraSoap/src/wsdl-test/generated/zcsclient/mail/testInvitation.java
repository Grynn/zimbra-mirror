
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for invitation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invitation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="comp" type="{urn:zimbraMail}inviteComponent" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="mp" type="{urn:zimbraMail}partInfo"/>
 *           &lt;element name="shr" type="{urn:zimbraMail}shareNotification"/>
 *           &lt;element name="dlSubs" type="{urn:zimbraMail}dlSubscriptionNotification"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="seq" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="compNum" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="recurId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invitation", propOrder = {
    "tz",
    "comp",
    "mpOrShrOrDlSubs"
})
public class testInvitation {

    protected List<testCalTZInfo> tz;
    protected testInviteComponent comp;
    @XmlElements({
        @XmlElement(name = "dlSubs", type = testDlSubscriptionNotification.class),
        @XmlElement(name = "mp", type = testPartInfo.class),
        @XmlElement(name = "shr", type = testShareNotification.class)
    })
    protected List<Object> mpOrShrOrDlSubs;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "seq", required = true)
    protected int seq;
    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "compNum", required = true)
    protected int compNum;
    @XmlAttribute(name = "recurId")
    protected String recurId;

    /**
     * Gets the value of the tz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTz().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCalTZInfo }
     * 
     * 
     */
    public List<testCalTZInfo> getTz() {
        if (tz == null) {
            tz = new ArrayList<testCalTZInfo>();
        }
        return this.tz;
    }

    /**
     * Gets the value of the comp property.
     * 
     * @return
     *     possible object is
     *     {@link testInviteComponent }
     *     
     */
    public testInviteComponent getComp() {
        return comp;
    }

    /**
     * Sets the value of the comp property.
     * 
     * @param value
     *     allowed object is
     *     {@link testInviteComponent }
     *     
     */
    public void setComp(testInviteComponent value) {
        this.comp = value;
    }

    /**
     * Gets the value of the mpOrShrOrDlSubs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mpOrShrOrDlSubs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMpOrShrOrDlSubs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testDlSubscriptionNotification }
     * {@link testPartInfo }
     * {@link testShareNotification }
     * 
     * 
     */
    public List<Object> getMpOrShrOrDlSubs() {
        if (mpOrShrOrDlSubs == null) {
            mpOrShrOrDlSubs = new ArrayList<Object>();
        }
        return this.mpOrShrOrDlSubs;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     */
    public int getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     */
    public void setSeq(int value) {
        this.seq = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the compNum property.
     * 
     */
    public int getCompNum() {
        return compNum;
    }

    /**
     * Sets the value of the compNum property.
     * 
     */
    public void setCompNum(int value) {
        this.compNum = value;
    }

    /**
     * Gets the value of the recurId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecurId() {
        return recurId;
    }

    /**
     * Sets the value of the recurId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecurId(String value) {
        this.recurId = value;
    }

}
