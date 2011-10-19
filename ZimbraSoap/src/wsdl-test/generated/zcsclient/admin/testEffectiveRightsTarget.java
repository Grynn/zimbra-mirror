
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testTargetType;


/**
 * <p>Java class for effectiveRightsTarget complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="effectiveRightsTarget">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="all" type="{urn:zimbraAdmin}effectiveRightsInfo" minOccurs="0"/>
 *         &lt;element name="inDomains" type="{urn:zimbraAdmin}inDomainInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="entries" type="{urn:zimbraAdmin}rightsEntriesInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{urn:zimbra}targetType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "effectiveRightsTarget", propOrder = {
    "all",
    "inDomains",
    "entries"
})
public class testEffectiveRightsTarget {

    protected testEffectiveRightsInfo all;
    protected List<testInDomainInfo> inDomains;
    protected List<testRightsEntriesInfo> entries;
    @XmlAttribute(name = "type", required = true)
    protected testTargetType type;

    /**
     * Gets the value of the all property.
     * 
     * @return
     *     possible object is
     *     {@link testEffectiveRightsInfo }
     *     
     */
    public testEffectiveRightsInfo getAll() {
        return all;
    }

    /**
     * Sets the value of the all property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEffectiveRightsInfo }
     *     
     */
    public void setAll(testEffectiveRightsInfo value) {
        this.all = value;
    }

    /**
     * Gets the value of the inDomains property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inDomains property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInDomains().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testInDomainInfo }
     * 
     * 
     */
    public List<testInDomainInfo> getInDomains() {
        if (inDomains == null) {
            inDomains = new ArrayList<testInDomainInfo>();
        }
        return this.inDomains;
    }

    /**
     * Gets the value of the entries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testRightsEntriesInfo }
     * 
     * 
     */
    public List<testRightsEntriesInfo> getEntries() {
        if (entries == null) {
            entries = new ArrayList<testRightsEntriesInfo>();
        }
        return this.entries;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link testTargetType }
     *     
     */
    public testTargetType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTargetType }
     *     
     */
    public void setType(testTargetType value) {
        this.type = value;
    }

}
