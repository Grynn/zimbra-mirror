
package generated.zcsclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testKeyValuePair;


/**
 * <p>Java class for accountKeyValuePairs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accountKeyValuePairs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="a" type="{urn:zimbra}keyValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accountKeyValuePairs", propOrder = {
    "a"
})
@XmlSeeAlso({
    testCreateDistributionListRequest.class,
    testAccountCustomMetadata.class,
    testCalendarResourceInfo.class,
    testDistributionListAction.class
})
public class testAccountKeyValuePairs {

    protected List<testKeyValuePair> a;

    /**
     * Gets the value of the a property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the a property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testKeyValuePair }
     * 
     * 
     */
    public List<testKeyValuePair> getA() {
        if (a == null) {
            a = new ArrayList<testKeyValuePair>();
        }
        return this.a;
    }

}
