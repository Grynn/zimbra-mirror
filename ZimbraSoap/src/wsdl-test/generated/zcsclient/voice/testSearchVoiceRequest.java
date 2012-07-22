
package generated.zcsclient.voice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchVoiceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchVoiceRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="storeprincipal" type="{urn:zimbraVoice}storePrincipalSpec" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="query" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="limit" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="offset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="types" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchVoiceRequest", propOrder = {
    "storeprincipal"
})
public class testSearchVoiceRequest {

    protected testStorePrincipalSpec storeprincipal;
    @XmlAttribute(name = "query", required = true)
    protected String query;
    @XmlAttribute(name = "limit")
    protected Integer limit;
    @XmlAttribute(name = "offset")
    protected Integer offset;
    @XmlAttribute(name = "types")
    protected String types;
    @XmlAttribute(name = "sortBy")
    protected String sortBy;

    /**
     * Gets the value of the storeprincipal property.
     * 
     * @return
     *     possible object is
     *     {@link testStorePrincipalSpec }
     *     
     */
    public testStorePrincipalSpec getStoreprincipal() {
        return storeprincipal;
    }

    /**
     * Sets the value of the storeprincipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link testStorePrincipalSpec }
     *     
     */
    public void setStoreprincipal(testStorePrincipalSpec value) {
        this.storeprincipal = value;
    }

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
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLimit(Integer value) {
        this.limit = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOffset(Integer value) {
        this.offset = value;
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

}
