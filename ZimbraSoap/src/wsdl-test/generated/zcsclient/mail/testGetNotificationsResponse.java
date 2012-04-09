
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testNamedElement;


/**
 * <p>Java class for getNotificationsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getNotificationsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="op" type="{urn:zimbra}namedElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user" type="{urn:zimbraMail}idEmailName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="a" type="{urn:zimbraMail}activityInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lastSeen" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getNotificationsResponse", propOrder = {
    "op",
    "user",
    "a"
})
public class testGetNotificationsResponse {

    protected List<testNamedElement> op;
    protected List<testIdEmailName> user;
    protected List<testActivityInfo> a;
    @XmlAttribute(name = "lastSeen", required = true)
    protected long lastSeen;

    /**
     * Gets the value of the op property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the op property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testNamedElement }
     * 
     * 
     */
    public List<testNamedElement> getOp() {
        if (op == null) {
            op = new ArrayList<testNamedElement>();
        }
        return this.op;
    }

    /**
     * Gets the value of the user property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the user property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testIdEmailName }
     * 
     * 
     */
    public List<testIdEmailName> getUser() {
        if (user == null) {
            user = new ArrayList<testIdEmailName>();
        }
        return this.user;
    }

    /**
     * Gets the value of the a property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the a property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testActivityInfo }
     * 
     * 
     */
    public List<testActivityInfo> getA() {
        if (a == null) {
            a = new ArrayList<testActivityInfo>();
        }
        return this.a;
    }

    /**
     * Gets the value of the lastSeen property.
     * 
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * Sets the value of the lastSeen property.
     * 
     */
    public void setLastSeen(long value) {
        this.lastSeen = value;
    }

}
