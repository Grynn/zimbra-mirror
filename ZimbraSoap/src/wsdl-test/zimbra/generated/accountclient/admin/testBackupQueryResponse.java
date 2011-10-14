
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for backupQueryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="backupQueryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="backup" type="{urn:zimbraAdmin}backupQueryInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="totalSpace" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="freeSpace" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="more" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "backupQueryResponse", propOrder = {
    "backup"
})
public class testBackupQueryResponse {

    protected testBackupQueryInfo backup;
    @XmlAttribute(name = "totalSpace", required = true)
    protected long totalSpace;
    @XmlAttribute(name = "freeSpace", required = true)
    protected long freeSpace;
    @XmlAttribute(name = "more")
    protected Boolean more;

    /**
     * Gets the value of the backup property.
     * 
     * @return
     *     possible object is
     *     {@link testBackupQueryInfo }
     *     
     */
    public testBackupQueryInfo getBackup() {
        return backup;
    }

    /**
     * Sets the value of the backup property.
     * 
     * @param value
     *     allowed object is
     *     {@link testBackupQueryInfo }
     *     
     */
    public void setBackup(testBackupQueryInfo value) {
        this.backup = value;
    }

    /**
     * Gets the value of the totalSpace property.
     * 
     */
    public long getTotalSpace() {
        return totalSpace;
    }

    /**
     * Sets the value of the totalSpace property.
     * 
     */
    public void setTotalSpace(long value) {
        this.totalSpace = value;
    }

    /**
     * Gets the value of the freeSpace property.
     * 
     */
    public long getFreeSpace() {
        return freeSpace;
    }

    /**
     * Sets the value of the freeSpace property.
     * 
     */
    public void setFreeSpace(long value) {
        this.freeSpace = value;
    }

    /**
     * Gets the value of the more property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMore(Boolean value) {
        this.more = value;
    }

}
