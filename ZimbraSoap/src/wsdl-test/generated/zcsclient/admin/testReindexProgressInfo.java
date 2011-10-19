
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reindexProgressInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reindexProgressInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="numSucceeded" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numFailed" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="numRemaining" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reindexProgressInfo")
public class testReindexProgressInfo {

    @XmlAttribute(name = "numSucceeded", required = true)
    protected int numSucceeded;
    @XmlAttribute(name = "numFailed", required = true)
    protected int numFailed;
    @XmlAttribute(name = "numRemaining", required = true)
    protected int numRemaining;

    /**
     * Gets the value of the numSucceeded property.
     * 
     */
    public int getNumSucceeded() {
        return numSucceeded;
    }

    /**
     * Sets the value of the numSucceeded property.
     * 
     */
    public void setNumSucceeded(int value) {
        this.numSucceeded = value;
    }

    /**
     * Gets the value of the numFailed property.
     * 
     */
    public int getNumFailed() {
        return numFailed;
    }

    /**
     * Sets the value of the numFailed property.
     * 
     */
    public void setNumFailed(int value) {
        this.numFailed = value;
    }

    /**
     * Gets the value of the numRemaining property.
     * 
     */
    public int getNumRemaining() {
        return numRemaining;
    }

    /**
     * Sets the value of the numRemaining property.
     * 
     */
    public void setNumRemaining(int value) {
        this.numRemaining = value;
    }

}
