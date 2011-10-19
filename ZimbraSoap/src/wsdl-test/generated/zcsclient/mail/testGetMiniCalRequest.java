
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;


/**
 * <p>Java class for getMiniCalRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMiniCalRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="folder" type="{urn:zimbra}id" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="s" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="e" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMiniCalRequest", propOrder = {
    "folder",
    "tz"
})
public class testGetMiniCalRequest {

    protected List<testId> folder;
    protected testCalTZInfo tz;
    @XmlAttribute(name = "s", required = true)
    protected long s;
    @XmlAttribute(name = "e", required = true)
    protected long e;

    /**
     * Gets the value of the folder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testId }
     * 
     * 
     */
    public List<testId> getFolder() {
        if (folder == null) {
            folder = new ArrayList<testId>();
        }
        return this.folder;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link testCalTZInfo }
     *     
     */
    public testCalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalTZInfo }
     *     
     */
    public void setTz(testCalTZInfo value) {
        this.tz = value;
    }

    /**
     * Gets the value of the s property.
     * 
     */
    public long getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     */
    public void setS(long value) {
        this.s = value;
    }

    /**
     * Gets the value of the e property.
     * 
     */
    public long getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     */
    public void setE(long value) {
        this.e = value;
    }

}
