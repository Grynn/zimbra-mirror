
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testNamedElement;


/**
 * <p>Java class for rightsEntriesInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rightsEntriesInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entry" type="{urn:zimbra}namedElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rights" type="{urn:zimbraAdmin}effectiveRightsInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rightsEntriesInfo", propOrder = {
    "entry",
    "rights"
})
public class testRightsEntriesInfo {

    protected List<testNamedElement> entry;
    @XmlElement(required = true)
    protected testEffectiveRightsInfo rights;

    /**
     * Gets the value of the entry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testNamedElement }
     * 
     * 
     */
    public List<testNamedElement> getEntry() {
        if (entry == null) {
            entry = new ArrayList<testNamedElement>();
        }
        return this.entry;
    }

    /**
     * Gets the value of the rights property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveRightsInfo }
     *     
     */
    public testEffectiveRightsInfo getRights() {
        return rights;
    }

    /**
     * Sets the value of the rights property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveRightsInfo }
     *     
     */
    public void setRights(testEffectiveRightsInfo value) {
        this.rights = value;
    }

}
