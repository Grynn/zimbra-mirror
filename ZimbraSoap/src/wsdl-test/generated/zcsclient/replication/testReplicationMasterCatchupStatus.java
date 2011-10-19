
package generated.zcsclient.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationMasterCatchupStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationMasterCatchupStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
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
@XmlType(name = "replicationMasterCatchupStatus")
public class testReplicationMasterCatchupStatus {

    @XmlAttribute(name = "remainingFiles", required = true)
    protected int remainingFiles;
    @XmlAttribute(name = "remainingBytes", required = true)
    protected long remainingBytes;

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
