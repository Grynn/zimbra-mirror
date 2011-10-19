
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mountpoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mountpoint">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}folder">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="zid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rid" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="oname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="reminder" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mountpoint")
public class testMountpoint
    extends testFolder
{

    @XmlAttribute(name = "owner")
    protected String owner;
    @XmlAttribute(name = "zid")
    protected String zid;
    @XmlAttribute(name = "rid", required = true)
    protected int rid;
    @XmlAttribute(name = "oname")
    protected String oname;
    @XmlAttribute(name = "reminder")
    protected Boolean reminder;

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the zid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZid() {
        return zid;
    }

    /**
     * Sets the value of the zid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZid(String value) {
        this.zid = value;
    }

    /**
     * Gets the value of the rid property.
     * 
     */
    public int getRid() {
        return rid;
    }

    /**
     * Sets the value of the rid property.
     * 
     */
    public void setRid(int value) {
        this.rid = value;
    }

    /**
     * Gets the value of the oname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOname() {
        return oname;
    }

    /**
     * Sets the value of the oname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOname(String value) {
        this.oname = value;
    }

    /**
     * Gets the value of the reminder property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReminder() {
        return reminder;
    }

    /**
     * Sets the value of the reminder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReminder(Boolean value) {
        this.reminder = value;
    }

}
