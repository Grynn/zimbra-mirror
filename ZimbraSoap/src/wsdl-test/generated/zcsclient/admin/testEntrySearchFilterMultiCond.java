
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entrySearchFilterMultiCond complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entrySearchFilterMultiCond">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="conds" type="{urn:zimbraAdmin}entrySearchFilterMultiCond"/>
 *           &lt;element name="cond" type="{urn:zimbraAdmin}entrySearchFilterSingleCond"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="or" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entrySearchFilterMultiCond", propOrder = {
    "condsOrCond"
})
public class testEntrySearchFilterMultiCond {

    @XmlElements({
        @XmlElement(name = "cond", type = testEntrySearchFilterSingleCond.class),
        @XmlElement(name = "conds", type = testEntrySearchFilterMultiCond.class)
    })
    protected List<Object> condsOrCond;
    @XmlAttribute(name = "not")
    protected Boolean not;
    @XmlAttribute(name = "or")
    protected Boolean or;

    /**
     * Gets the value of the condsOrCond property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the condsOrCond property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCondsOrCond().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testEntrySearchFilterSingleCond }
     * {@link testEntrySearchFilterMultiCond }
     * 
     * 
     */
    public List<Object> getCondsOrCond() {
        if (condsOrCond == null) {
            condsOrCond = new ArrayList<Object>();
        }
        return this.condsOrCond;
    }

    /**
     * Gets the value of the not property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNot() {
        return not;
    }

    /**
     * Sets the value of the not property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNot(Boolean value) {
        this.not = value;
    }

    /**
     * Gets the value of the or property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOr() {
        return or;
    }

    /**
     * Sets the value of the or property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOr(Boolean value) {
        this.or = value;
    }

}
