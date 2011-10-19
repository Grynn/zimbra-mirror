
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailboxVolumeInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailboxVolumeInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="rootpath" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailboxVolumeInfo")
public class testMailboxVolumeInfo {

    @XmlAttribute(name = "id", required = true)
    protected short id;
    @XmlAttribute(name = "type", required = true)
    protected short type;
    @XmlAttribute(name = "rootpath", required = true)
    protected String rootpath;

    /**
     * Gets the value of the id property.
     * 
     */
    public short getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(short value) {
        this.id = value;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public short getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(short value) {
        this.type = value;
    }

    /**
     * Gets the value of the rootpath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootpath() {
        return rootpath;
    }

    /**
     * Sets the value of the rootpath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootpath(String value) {
        this.rootpath = value;
    }

}
