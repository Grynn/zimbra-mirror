
package zimbra.generated.accountclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for queryWaitSetResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queryWaitSetResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="waitSet" type="{urn:zimbraAdmin}waitSetInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queryWaitSetResponse", propOrder = {
    "waitSet"
})
public class testQueryWaitSetResponse {

    protected List<testWaitSetInfo> waitSet;

    /**
     * Gets the value of the waitSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the waitSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWaitSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testWaitSetInfo }
     * 
     * 
     */
    public List<testWaitSetInfo> getWaitSet() {
        if (waitSet == null) {
            waitSet = new ArrayList<testWaitSetInfo>();
        }
        return this.waitSet;
    }

}
