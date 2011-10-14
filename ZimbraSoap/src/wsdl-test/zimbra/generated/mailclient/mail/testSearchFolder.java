
package zimbra.generated.mailclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.mailclient.zm.testSearchSortBy;


/**
 * <p>Java class for searchFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchFolder">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}folder">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="query" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortBy" type="{urn:zimbra}searchSortBy" />
 *       &lt;attribute name="types" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchFolder")
public class testSearchFolder
    extends testFolder
{

    @XmlAttribute(name = "query")
    protected String query;
    @XmlAttribute(name = "sortBy")
    protected testSearchSortBy sortBy;
    @XmlAttribute(name = "types")
    protected String types;

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuery(String value) {
        this.query = value;
    }

    /**
     * Gets the value of the sortBy property.
     * 
     * @return
     *     possible object is
     *     {@link testSearchSortBy }
     *     
     */
    public testSearchSortBy getSortBy() {
        return sortBy;
    }

    /**
     * Sets the value of the sortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSearchSortBy }
     *     
     */
    public void setSortBy(testSearchSortBy value) {
        this.sortBy = value;
    }

    /**
     * Gets the value of the types property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypes() {
        return types;
    }

    /**
     * Sets the value of the types property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypes(String value) {
        this.types = value;
    }

}
