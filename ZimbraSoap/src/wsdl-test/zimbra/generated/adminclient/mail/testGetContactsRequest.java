
package zimbra.generated.adminclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.adminclient.zm.testAttributeName;
import zimbra.generated.adminclient.zm.testId;


/**
 * <p>Java class for getContactsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getContactsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="a" type="{urn:zimbra}attributeName"/>
 *           &lt;element name="cn" type="{urn:zimbra}id"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="sync" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="l" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="derefGroupMember" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="returnHiddenAttrs" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getContactsRequest", propOrder = {
    "aOrCn"
})
public class testGetContactsRequest {

    @XmlElements({
        @XmlElement(name = "cn", type = testId.class),
        @XmlElement(name = "a", type = testAttributeName.class)
    })
    protected List<Object> aOrCn;
    @XmlAttribute(name = "sync")
    protected Boolean sync;
    @XmlAttribute(name = "l")
    protected String l;
    @XmlAttribute(name = "sortBy")
    protected String sortBy;
    @XmlAttribute(name = "derefGroupMember")
    protected Boolean derefGroupMember;
    @XmlAttribute(name = "returnHiddenAttrs")
    protected Boolean returnHiddenAttrs;

    /**
     * Gets the value of the aOrCn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the aOrCn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAOrCn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testId }
     * {@link testAttributeName }
     * 
     * 
     */
    public List<Object> getAOrCn() {
        if (aOrCn == null) {
            aOrCn = new ArrayList<Object>();
        }
        return this.aOrCn;
    }

    /**
     * Gets the value of the sync property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSync() {
        return sync;
    }

    /**
     * Sets the value of the sync property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSync(Boolean value) {
        this.sync = value;
    }

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setL(String value) {
        this.l = value;
    }

    /**
     * Gets the value of the sortBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the value of the sortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortBy(String value) {
        this.sortBy = value;
    }

    /**
     * Gets the value of the derefGroupMember property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDerefGroupMember() {
        return derefGroupMember;
    }

    /**
     * Sets the value of the derefGroupMember property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDerefGroupMember(Boolean value) {
        this.derefGroupMember = value;
    }

    /**
     * Gets the value of the returnHiddenAttrs property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReturnHiddenAttrs() {
        return returnHiddenAttrs;
    }

    /**
     * Sets the value of the returnHiddenAttrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturnHiddenAttrs(Boolean value) {
        this.returnHiddenAttrs = value;
    }

}
