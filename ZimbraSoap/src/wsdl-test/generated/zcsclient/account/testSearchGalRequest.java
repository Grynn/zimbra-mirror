
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testCursorInfo;
import generated.zcsclient.zm.testGalSearchType;


/**
 * <p>Java class for searchGalRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchGalRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="locale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cursor" type="{urn:zimbra}cursorInfo" minOccurs="0"/>
 *         &lt;element name="searchFilter" type="{urn:zimbraAccount}entrySearchFilterInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{urn:zimbra}galSearchType" />
 *       &lt;attribute name="needExp" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="needIsOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="needIsMember" type="{urn:zimbraAccount}memberOfSelector" />
 *       &lt;attribute name="needSMIMECerts" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="galAcctId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="quick" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="limit" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="offset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchGalRequest", propOrder = {
    "locale",
    "cursor",
    "searchFilter"
})
public class testSearchGalRequest {

    protected String locale;
    protected testCursorInfo cursor;
    protected testEntrySearchFilterInfo searchFilter;
    @XmlAttribute(name = "ref")
    protected String ref;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "type")
    protected testGalSearchType type;
    @XmlAttribute(name = "needExp")
    protected Boolean needExp;
    @XmlAttribute(name = "needIsOwner")
    protected Boolean needIsOwner;
    @XmlAttribute(name = "needIsMember")
    protected testMemberOfSelector needIsMember;
    @XmlAttribute(name = "needSMIMECerts")
    protected Boolean needSMIMECerts;
    @XmlAttribute(name = "galAcctId")
    protected String galAcctId;
    @XmlAttribute(name = "quick")
    protected Boolean quick;
    @XmlAttribute(name = "sortBy")
    protected String sortBy;
    @XmlAttribute(name = "limit")
    protected Integer limit;
    @XmlAttribute(name = "offset")
    protected Integer offset;

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
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link testGalSearchType }
     *     
     */
    public testGalSearchType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGalSearchType }
     *     
     */
    public void setType(testGalSearchType value) {
        this.type = value;
    }

    /**
     * Gets the value of the needExp property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeedExp() {
        return needExp;
    }

    /**
     * Sets the value of the needExp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedExp(Boolean value) {
        this.needExp = value;
    }

    /**
     * Gets the value of the needIsOwner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeedIsOwner() {
        return needIsOwner;
    }

    /**
     * Sets the value of the needIsOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedIsOwner(Boolean value) {
        this.needIsOwner = value;
    }

    /**
     * Gets the value of the needIsMember property.
     * 
     * @return
     *     possible object is
     *     {@link testMemberOfSelector }
     *     
     */
    public testMemberOfSelector getNeedIsMember() {
        return needIsMember;
    }

    /**
     * Sets the value of the needIsMember property.
     * 
     * @param value
     *     allowed object is
     *     {@link testMemberOfSelector }
     *     
     */
    public void setNeedIsMember(testMemberOfSelector value) {
        this.needIsMember = value;
    }

    /**
     * Gets the value of the needSMIMECerts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeedSMIMECerts() {
        return needSMIMECerts;
    }

    /**
     * Sets the value of the needSMIMECerts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedSMIMECerts(Boolean value) {
        this.needSMIMECerts = value;
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

}
