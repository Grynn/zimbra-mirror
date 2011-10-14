
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dirPathInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dirPathInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exists" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isDirectory" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="readable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="writable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dirPathInfo")
public class testDirPathInfo {

    @XmlAttribute(name = "path", required = true)
    protected String path;
    @XmlAttribute(name = "exists", required = true)
    protected boolean exists;
    @XmlAttribute(name = "isDirectory", required = true)
    protected boolean isDirectory;
    @XmlAttribute(name = "readable", required = true)
    protected boolean readable;
    @XmlAttribute(name = "writable", required = true)
    protected boolean writable;

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the exists property.
     * 
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * Sets the value of the exists property.
     * 
     */
    public void setExists(boolean value) {
        this.exists = value;
    }

    /**
     * Gets the value of the isDirectory property.
     * 
     */
    public boolean isIsDirectory() {
        return isDirectory;
    }

    /**
     * Sets the value of the isDirectory property.
     * 
     */
    public void setIsDirectory(boolean value) {
        this.isDirectory = value;
    }

    /**
     * Gets the value of the readable property.
     * 
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * Sets the value of the readable property.
     * 
     */
    public void setReadable(boolean value) {
        this.readable = value;
    }

    /**
     * Gets the value of the writable property.
     * 
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * Sets the value of the writable property.
     * 
     */
    public void setWritable(boolean value) {
        this.writable = value;
    }

}
