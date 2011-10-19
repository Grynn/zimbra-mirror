
package generated.zcsclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAttributeName;
import generated.zcsclient.zm.testAttributeSelectorImpl;
import generated.zcsclient.zm.testCursorInfo;


/**
 * <p>Java class for searchCalendarResourcesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchCalendarResourcesRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}attributeSelectorImpl">
 *       &lt;sequence>
 *         &lt;element name="header" type="{urn:zimbra}attributeName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraAccount}calTZInfo" minOccurs="0"/>
 *         &lt;element name="locale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cursor" type="{urn:zimbra}cursorInfo" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="searchFilter" type="{urn:zimbraAccount}entrySearchFilterInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="includeTagDeleted" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="allowableTaskStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="calExpandInstStart" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="calExpandInstEnd" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="query" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inDumpster" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="types" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="groupBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="quick" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fetch" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="read" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="html" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="neuter" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="recip" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="prefetch" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="resultMode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="field" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="limit" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="offset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="galAcctId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchCalendarResourcesRequest", propOrder = {
    "header",
    "tz",
    "locale",
    "cursor",
    "name",
    "searchFilter"
})
public class testSearchCalendarResourcesRequest
    extends testAttributeSelectorImpl
{

    protected List<testAttributeName> header;
    protected testCalTZInfo tz;
    protected String locale;
    protected testCursorInfo cursor;
    protected String name;
    protected testEntrySearchFilterInfo searchFilter;
    @XmlAttribute(name = "includeTagDeleted")
    protected Boolean includeTagDeleted;
    @XmlAttribute(name = "allowableTaskStatus")
    protected String allowableTaskStatus;
    @XmlAttribute(name = "calExpandInstStart")
    protected Long calExpandInstStart;
    @XmlAttribute(name = "calExpandInstEnd")
    protected Long calExpandInstEnd;
    @XmlAttribute(name = "query")
    protected String query;
    @XmlAttribute(name = "inDumpster")
    protected Boolean inDumpster;
    @XmlAttribute(name = "types")
    protected String types;
    @XmlAttribute(name = "groupBy")
    protected String groupBy;
    @XmlAttribute(name = "quick")
    protected Boolean quick;
    @XmlAttribute(name = "sortBy")
    protected String sortBy;
    @XmlAttribute(name = "fetch")
    protected String fetch;
    @XmlAttribute(name = "read")
    protected Boolean read;
    @XmlAttribute(name = "max")
    protected Integer max;
    @XmlAttribute(name = "html")
    protected Boolean html;
    @XmlAttribute(name = "neuter")
    protected Boolean neuter;
    @XmlAttribute(name = "recip")
    protected Boolean recip;
    @XmlAttribute(name = "prefetch")
    protected Boolean prefetch;
    @XmlAttribute(name = "resultMode")
    protected String resultMode;
    @XmlAttribute(name = "field")
    protected String field;
    @XmlAttribute(name = "limit")
    protected Integer limit;
    @XmlAttribute(name = "offset")
    protected Integer offset;
    @XmlAttribute(name = "galAcctId")
    protected String galAcctId;

    /**
     * Gets the value of the header property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the header property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeader().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAttributeName }
     * 
     * 
     */
    public List<testAttributeName> getHeader() {
        if (header == null) {
            header = new ArrayList<testAttributeName>();
        }
        return this.header;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link testCalTZInfo }
     *     
     */
    public testCalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalTZInfo }
     *     
     */
    public void setTz(testCalTZInfo value) {
        this.tz = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocale(String value) {
        this.locale = value;
    }

    /**
     * Gets the value of the cursor property.
     * 
     * @return
     *     possible object is
     *     {@link testCursorInfo }
     *     
     */
    public testCursorInfo getCursor() {
        return cursor;
    }

    /**
     * Sets the value of the cursor property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCursorInfo }
     *     
     */
    public void setCursor(testCursorInfo value) {
        this.cursor = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the searchFilter property.
     * 
     * @return
     *     possible object is
     *     {@link testEntrySearchFilterInfo }
     *     
     */
    public testEntrySearchFilterInfo getSearchFilter() {
        return searchFilter;
    }

    /**
     * Sets the value of the searchFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link testEntrySearchFilterInfo }
     *     
     */
    public void setSearchFilter(testEntrySearchFilterInfo value) {
        this.searchFilter = value;
    }

    /**
     * Gets the value of the includeTagDeleted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeTagDeleted() {
        return includeTagDeleted;
    }

    /**
     * Sets the value of the includeTagDeleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeTagDeleted(Boolean value) {
        this.includeTagDeleted = value;
    }

    /**
     * Gets the value of the allowableTaskStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllowableTaskStatus() {
        return allowableTaskStatus;
    }

    /**
     * Sets the value of the allowableTaskStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowableTaskStatus(String value) {
        this.allowableTaskStatus = value;
    }

    /**
     * Gets the value of the calExpandInstStart property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCalExpandInstStart() {
        return calExpandInstStart;
    }

    /**
     * Sets the value of the calExpandInstStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCalExpandInstStart(Long value) {
        this.calExpandInstStart = value;
    }

    /**
     * Gets the value of the calExpandInstEnd property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCalExpandInstEnd() {
        return calExpandInstEnd;
    }

    /**
     * Sets the value of the calExpandInstEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCalExpandInstEnd(Long value) {
        this.calExpandInstEnd = value;
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
     * Gets the value of the inDumpster property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInDumpster() {
        return inDumpster;
    }

    /**
     * Sets the value of the inDumpster property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInDumpster(Boolean value) {
        this.inDumpster = value;
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
     * Gets the value of the groupBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupBy() {
        return groupBy;
    }

    /**
     * Sets the value of the groupBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupBy(String value) {
        this.groupBy = value;
    }

    /**
     * Gets the value of the quick property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isQuick() {
        return quick;
    }

    /**
     * Sets the value of the quick property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setQuick(Boolean value) {
        this.quick = value;
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
     * Gets the value of the fetch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFetch() {
        return fetch;
    }

    /**
     * Sets the value of the fetch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFetch(String value) {
        this.fetch = value;
    }

    /**
     * Gets the value of the read property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRead() {
        return read;
    }

    /**
     * Sets the value of the read property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRead(Boolean value) {
        this.read = value;
    }

    /**
     * Gets the value of the max property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMax(Integer value) {
        this.max = value;
    }

    /**
     * Gets the value of the html property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHtml() {
        return html;
    }

    /**
     * Sets the value of the html property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHtml(Boolean value) {
        this.html = value;
    }

    /**
     * Gets the value of the neuter property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeuter() {
        return neuter;
    }

    /**
     * Sets the value of the neuter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeuter(Boolean value) {
        this.neuter = value;
    }

    /**
     * Gets the value of the recip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRecip() {
        return recip;
    }

    /**
     * Sets the value of the recip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRecip(Boolean value) {
        this.recip = value;
    }

    /**
     * Gets the value of the prefetch property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrefetch() {
        return prefetch;
    }

    /**
     * Sets the value of the prefetch property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrefetch(Boolean value) {
        this.prefetch = value;
    }

    /**
     * Gets the value of the resultMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMode() {
        return resultMode;
    }

    /**
     * Sets the value of the resultMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMode(String value) {
        this.resultMode = value;
    }

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField(String value) {
        this.field = value;
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
     * Gets the value of the galAcctId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGalAcctId() {
        return galAcctId;
    }

    /**
     * Sets the value of the galAcctId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGalAcctId(String value) {
        this.galAcctId = value;
    }

}
