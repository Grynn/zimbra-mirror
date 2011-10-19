
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getApplianceHSMFSResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getApplianceHSMFSResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fs" type="{urn:zimbraAdmin}hsmFileSystemInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getApplianceHSMFSResponse", propOrder = {
    "fs"
})
public class testGetApplianceHSMFSResponse {

    protected List<testHsmFileSystemInfo> fs;

    /**
     * Gets the value of the fs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testHsmFileSystemInfo }
     * 
     * 
     */
    public List<testHsmFileSystemInfo> getFs() {
        if (fs == null) {
            fs = new ArrayList<testHsmFileSystemInfo>();
        }
        return this.fs;
    }

}
