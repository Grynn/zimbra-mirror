
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entrySearchFilterInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entrySearchFilterInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="conds" type="{urn:zimbraAccount}entrySearchFilterMultiCond"/>
 *           &lt;element name="cond" type="{urn:zimbraAccount}entrySearchFilterSingleCond"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entrySearchFilterInfo", propOrder = {
    "conds",
    "cond"
})
public class testEntrySearchFilterInfo {

    protected testEntrySearchFilterMultiCond conds;
    protected testEntrySearchFilterSingleCond cond;

    /**
     * Gets the value of the conds property.
     * 
     * @return
     *     possible object is
     *     {@link testEntrySearchFilterMultiCond }
     *     
     */
    public testEntrySearchFilterMultiCond getConds() {
        return conds;
    }

    /**
     * Sets the value of the conds property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEntrySearchFilterMultiCond }
     *     
     */
    public void setConds(testEntrySearchFilterMultiCond value) {
        this.conds = value;
    }

    /**
     * Gets the value of the cond property.
     * 
     * @return
     *     possible object is
     *     {@link testEntrySearchFilterSingleCond }
     *     
     */
    public testEntrySearchFilterSingleCond getCond() {
        return cond;
    }

    /**
     * Sets the value of the cond property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEntrySearchFilterSingleCond }
     *     
     */
    public void setCond(testEntrySearchFilterSingleCond value) {
        this.cond = value;
    }

}
