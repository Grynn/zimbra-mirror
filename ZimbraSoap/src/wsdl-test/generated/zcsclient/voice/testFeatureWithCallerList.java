
package generated.zcsclient.voice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for featureWithCallerList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="featureWithCallerList">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraVoice}callFeatureInfo">
 *       &lt;sequence>
 *         &lt;element name="phone" type="{urn:zimbraVoice}callerListEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "featureWithCallerList", propOrder = {
    "phone"
})
@XmlSeeAlso({
    testSelectiveCallAcceptanceFeature.class,
    testSelectiveCallRejectionFeature.class,
    testSelectiveCallForwardFeature.class
})
public class testFeatureWithCallerList
    extends testCallFeatureInfo
{

    protected List<testCallerListEntry> phone;

    /**
     * Gets the value of the phone property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the phone property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPhone().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCallerListEntry }
     * 
     * 
     */
    public List<testCallerListEntry> getPhone() {
        if (phone == null) {
            phone = new ArrayList<testCallerListEntry>();
        }
        return this.phone;
    }

}
