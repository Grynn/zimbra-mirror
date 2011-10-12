
package zimbra.generated.accountclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.accountclient.admin.testDlInfo;


/**
 * <p>Java class for getAccountMembershipResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAccountMembershipResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dl" type="{urn:zimbraAdmin}dlInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAccountMembershipResponse", propOrder = {
    "dl"
})
public class testGetAccountMembershipResponse {

    protected List<testDlInfo> dl;

    /**
     * Gets the value of the dl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testDlInfo }
     * 
     * 
     */
    public List<testDlInfo> getDl() {
        if (dl == null) {
            dl = new ArrayList<testDlInfo>();
        }
        return this.dl;
    }

}
