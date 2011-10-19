
package generated.zcsclient.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationSlaveCatchupStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationSlaveCatchupStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="remainingOps" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="remainingFiles" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="remainingBytes" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationSlaveCatchupStatus")
public class testReplicationSlaveCatchupStatus {

    @XmlAttribute(name = "remainingOps", required = true)
    protected int remainingOps;
    @XmlAttribute(name = "remainingFiles", required = true)
    protected int remainingFiles;
    @XmlAttribute(name = "remainingBytes", required = true)
    protected long remainingBytes;

    /**
     * Gets the value of the remainingOps property.
     * 
     */
    public int getRemainingOps() {
        return remainingOps;
    }

    /**
     * Sets the value of the remainingOps property.
     * 
     */
    public void setRemainingOps(int value) {
        this.remainingOps = value;
    }

    /**
     * Gets the value of the remainingFiles property.
     * 
     */
    public int getRemainingFiles() {
        return remainingFiles;
    }

    /**
     * Sets the value of the remainingFiles property.
     * 
     */
    public void setRemainingFiles(int value) {
        this.remainingFiles = value;
    }

    /**
     * Gets the value of the remainingBytes property.
     * 
     */
    public long getRemainingBytes() {
        return remainingBytes;
    }

    /**
     * Sets the value of the remainingBytes property.
     * 
     */
    public void setRemainingBytes(long value) {
        this.remainingBytes = value;
    }

}
