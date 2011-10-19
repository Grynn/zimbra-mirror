
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkHealthResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkHealthResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *       &lt;/all>
 *       &lt;attribute name="healthy" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkHealthResponse", propOrder = {

})
public class testCheckHealthResponse {

    @XmlAttribute(name = "healthy", required = true)
    protected boolean healthy;

    /**
     * Gets the value of the healthy property.
     * 
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Sets the value of the healthy property.
     * 
     */
    public void setHealthy(boolean value) {
        this.healthy = value;
    }

}
