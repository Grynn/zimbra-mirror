
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deployZimletResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deployZimletResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="progress" type="{urn:zimbraAdmin}zimletDeploymentStatus" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deployZimletResponse", propOrder = {
    "progress"
})
public class testDeployZimletResponse {

    protected List<testZimletDeploymentStatus> progress;

    /**
     * Gets the value of the progress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the progress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProgress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testZimletDeploymentStatus }
     * 
     * 
     */
    public List<testZimletDeploymentStatus> getProgress() {
        if (progress == null) {
            progress = new ArrayList<testZimletDeploymentStatus>();
        }
        return this.progress;
    }

}
